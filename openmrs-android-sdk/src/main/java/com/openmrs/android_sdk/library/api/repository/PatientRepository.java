/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package com.openmrs.android_sdk.library.api.repository;

import static com.openmrs.android_sdk.utilities.ApplicationConstants.PRIMARY_KEY_ID;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;

import com.openmrs.android_sdk.R;
import com.openmrs.android_sdk.library.OpenmrsAndroid;
import com.openmrs.android_sdk.library.api.RestApi;
import com.openmrs.android_sdk.library.api.RestServiceBuilder;
import com.openmrs.android_sdk.library.api.workers.UpdatePatientWorker;
import com.openmrs.android_sdk.library.dao.EncounterCreateRoomDAO;
import com.openmrs.android_sdk.library.dao.PatientDAO;
import com.openmrs.android_sdk.library.databases.AppDatabaseHelper;
import com.openmrs.android_sdk.library.models.Encountercreate;
import com.openmrs.android_sdk.library.models.IdGenPatientIdentifiers;
import com.openmrs.android_sdk.library.models.IdentifierType;
import com.openmrs.android_sdk.library.models.Module;
import com.openmrs.android_sdk.library.models.Patient;
import com.openmrs.android_sdk.library.models.PatientDto;
import com.openmrs.android_sdk.library.models.PatientDtoUpdate;
import com.openmrs.android_sdk.library.models.PatientIdentifier;
import com.openmrs.android_sdk.library.models.PatientPhoto;
import com.openmrs.android_sdk.library.models.ResultType;
import com.openmrs.android_sdk.library.models.Results;
import com.openmrs.android_sdk.library.models.SystemProperty;
import com.openmrs.android_sdk.utilities.ApplicationConstants;
import com.openmrs.android_sdk.utilities.ModuleUtils;
import com.openmrs.android_sdk.utilities.NetworkUtils;
import com.openmrs.android_sdk.utilities.PatientComparator;
import com.openmrs.android_sdk.utilities.ToastUtil;

/**
 * The type Patient repository.
 */
@Singleton
public class PatientRepository extends BaseRepository {
    private final PatientDAO patientDAO;
    private final LocationRepository locationRepository;
    private final EncounterRepository encounterRepository;

    /**
     * Instantiates a new Patient repository.
     */
    @Inject
    public PatientRepository(PatientDAO patientDAO, LocationRepository locationRepository,
                             EncounterRepository encounterRepository) {
        this.patientDAO = patientDAO;
        this.locationRepository = locationRepository;
        this.encounterRepository = encounterRepository;
    }

    /**
     * Uploads a patient to the server.
     *
     * @param patient the patient to be registered in the server
     */
    public Observable<Patient> syncPatient(final Patient patient) {
        return AppDatabaseHelper.createObservableIO(() -> {
            final List<PatientIdentifier> identifiers = new ArrayList<>();
            final PatientIdentifier identifier = new PatientIdentifier();
            identifier.setLocation(locationRepository.getLocation());
            identifier.setIdentifier(getIdGenPatientIdentifier());
            identifier.setIdentifierType(getPatientIdentifierType());
            identifiers.add(identifier);

            patient.setIdentifiers(identifiers);

            PatientDto patientDto = patient.getPatientDto();

            Response<PatientDto> response = restApi.createPatient(patientDto).execute();
            if (response.isSuccessful()) {
                PatientDto returnedPatientDto = response.body();

                patient.setUuid(returnedPatientDto.getUuid());
                if (patient.getPhoto() != null) {
                    uploadPatientPhoto(patient);
                }

                patientDAO.updatePatient(patient.getId(), patient);
                if (!patient.getEncounters().equals("")) {
                    addEncounters(patient);
                }

                return patient;
            } else {
                throw new Exception("syncPatient error: " + response.message());
            }
        });
    }

    private void uploadPatientPhoto(final Patient patient) {
        PatientPhoto patientPhoto = new PatientPhoto();
        patientPhoto.setPhoto(patient.getPhoto());
        patientPhoto.setPerson(patient);
        Call<PatientPhoto> personPhotoCall =
                restApi.uploadPatientPhoto(patient.getUuid(), patientPhoto);
        personPhotoCall.enqueue(new Callback<PatientPhoto>() {
            @Override
            public void onResponse(@NonNull Call<PatientPhoto> call, @NonNull Response<PatientPhoto> response) {
                if (!response.isSuccessful()) {
                    getLogger().e(response.message());
                    //string resource added "patient_photo_update_unsuccessful"
                    ToastUtil.error("Patient photo cannot be synced due to server error " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<PatientPhoto> call, @NonNull Throwable t) {
                getLogger().e(t.getMessage());
                //string resource added "patient_photo_update_unsuccessful"
                ToastUtil.error("Patient photo cannot be synced due to server error " + t.toString());
            }
        });
    }

    /**
     * Registers a patient locally or to the server, according to network state.
     *
     * @param patient the patient to be registered
     * @return Observable result type of registration process
     */
    public Observable<Patient> registerPatient(final Patient patient) {
        return AppDatabaseHelper.createObservableIO(() -> {
            Long id = patientDAO.savePatient(patient).single().toBlocking().first();
            patient.setId(id);
            if (NetworkUtils.isOnline()) syncPatient(patient).single().toBlocking().first();
            return patient;
        });
    }

    /**
     * Updates patient locally and remotely.
     *
     * @param patient the patient
     * @return Observable result type
     */
    public Observable<ResultType> updatePatient(final Patient patient) {
        return AppDatabaseHelper.createObservableIO(() -> {
            if (NetworkUtils.isOnline()) {
                Call<PatientDto> call = restApi.updatePatient(
                        patient.getUpdatedPatientDto(), patient.getUuid(), "full");
                Response<PatientDto> response = call.execute();

                if (response.isSuccessful()) {
                    PatientDto patientDto = response.body();
                    patient.setBirthdate(patientDto.getPerson().getBirthdate());
                    patient.setUuid(patientDto.getUuid());

                    if (patient.getPhoto() != null) uploadPatientPhoto(patient);

                    patientDAO.updatePatient(patient.getId(), patient);

                    return ResultType.PatientUpdateSuccess;
                } else {
                    throw new Exception("updatePatient error: " + response.message());
                }
            } else {
                patientDAO.updatePatient(patient.getId(), patient);

                Data data = new Data.Builder().putString(PRIMARY_KEY_ID, patient.getId().toString()).build();
                Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
                getWorkManager().enqueue(new OneTimeWorkRequest.Builder(UpdatePatientWorker.class).setConstraints(constraints).setInputData(data).build());

                return ResultType.PatientUpdateLocalSuccess;
            }
        });
    }

    /**
     * Update matching patient.
     *
     * @param patient the locally merged patient
     */
    public Observable<Patient> updateMatchingPatient(final Patient patient) {
        return AppDatabaseHelper.createObservableIO(() -> {

            PatientDtoUpdate patientDto = patient.getUpdatedPatientDto();

            Call<PatientDto> call = restApi.updatePatient(patientDto, patient.getUuid(), ApplicationConstants.API.FULL);
            Response<PatientDto> response = call.execute();

            if (response.isSuccessful()) return patient;
            else throw new IOException(response.message());
        });
    }

    /**
     * Download patient by uuid.
     *
     * @param uuid patient uuid
     * @return Patient observable
     */
    public Observable<Patient> downloadPatientByUuid(@NonNull final String uuid) {
        return AppDatabaseHelper.createObservableIO(() -> {
            Call<PatientDto> call = restApi.getPatientByUUID(uuid, "full");
            Response<PatientDto> response = call.execute();
            if (response.isSuccessful()) {
                final PatientDto newPatientDto = response.body();

                Bitmap photo = downloadPatientPhotoByUuid(newPatientDto.getUuid()).toBlocking().first();
                if (photo != null) newPatientDto.getPerson().setPhoto(photo);

                return newPatientDto.getPatient();
            } else {
                throw new IOException("Error with downloading patient: " + response.message());
            }
        });
    }

    /**
     * Download patient photo by uuid.
     *
     * @param uuid patient uuid
     * @return Photo bitmap or null bitmap observable
     */
    public Observable<Bitmap> downloadPatientPhotoByUuid(String uuid) {
        return AppDatabaseHelper.createObservableIO(() -> {
            Call<ResponseBody> call = restApi.downloadPatientPhoto(uuid);
            Response<ResponseBody> response = call.execute();

            if (response.isSuccessful()) {
                try {
                    InputStream inputStream = response.body().byteStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                    return bitmap;
                } catch (Exception e) {
                    getLogger().e(e.getMessage());
                }
            }
            return null;
        });
    }

    /**
     * Add encounters.
     *
     * @param patient the patient
     */
    public void addEncounters(Patient patient) {
        EncounterCreateRoomDAO dao = db.encounterCreateRoomDAO();
        String enc = patient.getEncounters();
        List<Long> list = new ArrayList<>();
        for (String s : enc.split(","))
            list.add(Long.parseLong(s));

        for (long id : list) {
            Encountercreate encountercreate = dao.getCreatedEncountersByID(id);
            encountercreate.setPatient(patient.getUuid());
            encountercreate.setSynced(false);
            encounterRepository.updateEncounterCreate(encountercreate);
        }
    }

    /**
     * Gets id gen patient identifier.
     *
     * @return the id gen patient identifier
     */
    public String getIdGenPatientIdentifier() throws IOException {
        IdGenPatientIdentifiers idList = null;

        RestApi patientIdentifierService = RestServiceBuilder.createServiceForPatientIdentifier(RestApi.class);
        Call<IdGenPatientIdentifiers> call = patientIdentifierService.getPatientIdentifiers(OpenmrsAndroid.getUsername(), OpenmrsAndroid.getPassword());

        Response<IdGenPatientIdentifiers> response = call.execute();
        if (response.isSuccessful()) {
            idList = response.body();
        }

        return idList.getIdentifiers().get(0);
    }

    /**
     * Gets patient identifier type (only has uuid).
     *
     * @return the patient identifier type
     */
    public IdentifierType getPatientIdentifierType() throws IOException {
        Call<Results<IdentifierType>> call = restApi.getIdentifierTypes();
        Response<Results<IdentifierType>> response = call.execute();
        if (response.isSuccessful()) {
            Results<IdentifierType> idResList = response.body();
            for (IdentifierType result : idResList.getResults()) {
                if (result.getDisplay().equals("OpenMRS ID")) {
                    return result;
                }
            }
        }
        return null;
    }

    /**
     * Find patients.
     *
     * @param query patient query string
     * @return observable list of patients with matching query
     */
    public Observable<List<Patient>> findPatients(String query) {
        return AppDatabaseHelper.createObservableIO(() -> {
            Call<Results<Patient>> call = restApi.getPatients(query, ApplicationConstants.API.FULL);
            Response<Results<Patient>> response = call.execute();
            if (response.isSuccessful()) {
                return response.body().getResults();
            } else {
                throw new Exception("Error with finding patients: " + response.message());
            }
        });
    }

    /**
     * Load more patients.
     *
     * @param limit      the limit
     * @param startIndex the start index
     * @return observable list of last viewed patients
     */
    public Observable<Results<Patient>> loadMorePatients(int limit, int startIndex) {
        return AppDatabaseHelper.createObservableIO(() -> {
            Call<Results<Patient>> call = restApi.getLastViewedPatients(limit, startIndex);
            Response<Results<Patient>> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                throw new Exception("Error with loading last viewed patients: " + response.message());
            }
        });
    }

    /**
     * Gets cause of death global id.
     *
     * @return Observable string UUID for cause of death Concept
     */
    public Observable<String> getCauseOfDeathGlobalConceptID() {
        return AppDatabaseHelper.createObservableIO(() -> {
            Call<Results<SystemProperty>> call = restApi.getSystemProperty(ApplicationConstants.CAUSE_OF_DEATH, ApplicationConstants.API.FULL);
            Response<Results<SystemProperty>> response = call.execute();
            if (response.isSuccessful()) {
                return response.body().getResults().get(0).getConceptUUID();
            } else {
                throw new Exception("Error with fetching Cause of Death Concept: " + response.message());
            }
        });
    }

    /**
     * Fetches similar patients by different strategies:
     * <br> 1. Fetch similar patients from server directly using an API.
     * <br> 2. Fetch patients with similar names, then compare their other similarities locally.
     * <br> 3. Fetch locally saved patients, then compare their similarities.
     *
     * @param patient to find similar patients to
     * @return Observable list of similar patients
     */
    public Observable<List<Patient>> fetchSimilarPatients(final Patient patient) {
        return AppDatabaseHelper.createObservableIO(() -> {
            if (!NetworkUtils.isOnline()) {
                List<Patient> localPatients = patientDAO.getAllPatients().toBlocking().first();
                return new PatientComparator().findSimilarPatient(localPatients, patient);
            }

            Call<Results<Module>> moduleCall = restApi.getModules(ApplicationConstants.API.FULL);
            Response<Results<Module>> response = moduleCall.execute();

            if (!response.isSuccessful()) return fetchSimilarPatientsAndCalculateLocally(patient);

            if (ModuleUtils.isRegistrationCore1_7orAbove(response.body().getResults())) {
                //return fetchSimilarPatientsFromServer(patient); //Uncomment this line when server API is fixed
                return fetchSimilarPatientsAndCalculateLocally(patient); //Remove this line when server API is fixed
            } else {
                ToastUtil.notifyLong(context.getString(R.string.registration_core_info));
                return fetchSimilarPatientsAndCalculateLocally(patient);
            }
        });
    }

    /**
     * Fetches similar patients directly from server.
     *
     * @param patient the patient to fetch similar patient to
     * @return list of similar patients
     */
    private List<Patient> fetchSimilarPatientsFromServer(final Patient patient) throws Exception {
        Call<Results<Patient>> call = restApi.getSimilarPatients(patient.toMap());
        Response<Results<Patient>> response = call.execute();
        if (response.isSuccessful()) return response.body().getResults();
        else throw new Exception("fetchSimilarPatientsFromServer error: " + response.message());
    }

    /**
     * Fetches patients with similar names from server, then calculates other similarities locally.
     *
     * @param patient the patient to fetch similar patient to
     * @return list of similar patients
     */
    private List<Patient> fetchSimilarPatientsAndCalculateLocally(final Patient patient) throws Exception {
        Call<Results<PatientDto>> call = restApi.getPatientsDto(patient.getName().getGivenName(), ApplicationConstants.API.FULL);
        Response<Results<PatientDto>> response = call.execute();
        if (response.isSuccessful()) {
            List<Patient> patientList = new ArrayList<>();
            for (PatientDto p : response.body().getResults()) patientList.add(p.getPatient());
            return new PatientComparator().findSimilarPatient(patientList, patient);
        } else {
            throw new Exception("fetchSimilarPatientAndCalculateLocally error: " + response.message());
        }
    }
}
