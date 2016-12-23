package org.openmrs.mobile.activities.matchingPatients;

import org.openmrs.mobile.R;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.api.retrofit.PatientApi;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.PatientAndMatchingPatients;
import org.openmrs.mobile.utilities.PatientMerger;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.Queue;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatchingPatientsPresenter implements MachingPatientsContract.Presenter{

    private MachingPatientsContract.View view;
    private Queue<PatientAndMatchingPatients> matchingPatientsList;
    private Patient selectedPatient;

    public MatchingPatientsPresenter(MachingPatientsContract.View view, Queue<PatientAndMatchingPatients> matchingPatientsList) {
        this.view = view;
        this.matchingPatientsList = matchingPatientsList;
        this.view.setPresenter(this);
    }

    @Override
    public void start() {
        view.showPatientsData(matchingPatientsList.peek().getPatient(), matchingPatientsList.peek().getMatchingPatientList());
        setSelectedIfOnlyOneMatching();
    }

    @Override
    public void setSelectedPatient(Patient patient) {
        selectedPatient = patient;
    }

    @Override
    public void removeSelectedPatient() {
        selectedPatient = null;
    }

    @Override
    public void mergePatients() {
        if (selectedPatient != null) {
            Patient patientToMerge = matchingPatientsList.poll().getPatient();
            Patient mergedPatient = new PatientMerger().mergePatient(selectedPatient, patientToMerge);
            updatePatient(mergedPatient);
            removeSelectedPatient();
            if (matchingPatientsList.peek() != null) {
               start();
            } else {
                view.finishActivity();
            }
        } else {
            view.notifyUser(R.string.no_patient_selected);
        }
    }

    private void updatePatient(final Patient patient) {
        patient.getPerson().setUuid(null);
        RestApi restApi = RestServiceBuilder.createService(RestApi.class);
        Call<Patient> call = restApi.updatePatient(patient, patient.getUuid(), ApplicationConstants.API.FULL);
        call.enqueue(new Callback<Patient>() {
            @Override
            public void onResponse(Call<Patient> call, Response<Patient> response) {
                if(response.isSuccessful()){
                    PatientDAO patientDAO = new PatientDAO();
                    patient.setSynced(true);
                    if(patientDAO.isUserAlreadySaved(patient.getUuid())){
                        Long id = patientDAO.findPatientByUUID(patient.getUuid()).getId();
                        patientDAO.updatePatient(id, patient);
                        patientDAO.deletePatient(patient.getId());
                    } else {
                        patientDAO.updatePatient(patient.getId(), patient);
                    }
                    ToastUtil.success("Patient " +patient.getPerson().getName().getNameString()
                            +" merged successfully");
                } else {
                    ToastUtil.error(response.message());
                }
            }

            @Override
            public void onFailure(Call<Patient> call, Throwable t) {
                ToastUtil.error(t.getMessage());
            }
        });
    }

    @Override
    public void registerNewPatient() {
        final Patient patient = matchingPatientsList.poll().getPatient();
        new PatientApi().syncPatient(patient);
        removeSelectedPatient();
        if (matchingPatientsList.peek() != null) {
            start();
        } else {
            view.finishActivity();
        }
    }

    private void setSelectedIfOnlyOneMatching() {
        if(matchingPatientsList.peek().getMatchingPatientList().size() == 1){
            selectedPatient = matchingPatientsList.peek().getMatchingPatientList().get(0);
        }
    }
}
