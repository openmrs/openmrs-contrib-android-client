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

package org.openmrs.mobile.databases;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.dao.EncounterDAO;
import org.openmrs.mobile.dao.ObservationDAO;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.databases.entities.AllergyEntity;
import org.openmrs.mobile.databases.entities.ConceptEntity;
import org.openmrs.mobile.databases.entities.EncounterEntity;
import org.openmrs.mobile.databases.entities.LocationEntity;
import org.openmrs.mobile.databases.entities.ObservationEntity;
import org.openmrs.mobile.databases.entities.PatientEntity;
import org.openmrs.mobile.databases.entities.VisitEntity;
import org.openmrs.mobile.models.Allergen;
import org.openmrs.mobile.models.Allergy;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.EncounterType;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.PatientIdentifier;
import org.openmrs.mobile.models.PersonAddress;
import org.openmrs.mobile.models.PersonName;
import org.openmrs.mobile.models.Resource;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitType;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FormService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.schedulers.Schedulers;

public class AppDatabaseHelper {

    public static ObservationEntity observationToEntity(Observation obs, long encounterID) {
        ObservationEntity observationEntity = new ObservationEntity();
        observationEntity.setId(obs.getId());
        observationEntity.setUuid(obs.getUuid());
        observationEntity.setDisplay(obs.getDisplay());
        observationEntity.setEncounterKeyID(encounterID);
        observationEntity.setDisplayValue(obs.getDisplayValue());
        observationEntity.setDiagnosisOrder(obs.getDiagnosisOrder());
        observationEntity.setDiagnosisList(obs.getDiagnosisList());
        observationEntity.setDiagnosisCertainty(obs.getDiagnosisCertainty());
        observationEntity.setDiagnosisNote(obs.getDiagnosisNote());
        if (obs.getConcept() != null) {
            observationEntity.setConceptuuid(obs.getConcept().getUuid());
        } else {
            observationEntity.setConceptuuid(null);
        }
        return observationEntity;
    }

    public static List<Observation> observationEntityToObservation(List<ObservationEntity> observationEntityList) {
        List<Observation> observationList = new ArrayList<>();
        for (ObservationEntity entity : observationEntityList) {
            Observation obs = new Observation();
            obs.setId(entity.getId());
            obs.setEncounterID(entity.getEncounterKeyID());
            obs.setUuid(entity.getUuid());
            obs.setDisplay(entity.getDisplay());
            obs.setDisplayValue(entity.getDisplayValue());
            obs.setDiagnosisOrder(entity.getDiagnosisOrder());
            obs.setDiagnosisList(entity.getDiagnosisList());
            obs.setDiagnosisCertanity(entity.getDiagnosisCertainty());
            obs.setDiagnosisNote(entity.getDiagnosisNote());
            ConceptEntity concept = new ConceptEntity();
            concept.setUuid(entity.getConceptuuid());
            obs.setConcept(concept);
            observationList.add(obs);
        }
        return observationList;
    }

    public static EncounterEntity encounterToEntity(Encounter encounter, Long visitID) {
        EncounterEntity encounterEntity = new EncounterEntity();
        encounterEntity.setId(encounter.getId());
        encounterEntity.setDisplay(encounter.getDisplay());
        encounterEntity.setUuid(encounter.getUuid());
        if (visitID != null) {
            encounterEntity.setVisitKeyId(visitID.toString());
        }
        encounterEntity.setEncounterDateTime(encounter.getEncounterDatetime().toString());
        encounterEntity.setEncounterType(encounter.getEncounterType().getDisplay());
        encounterEntity.setPatientUuid(encounter.getPatientUUID());
        encounterEntity.setFormUuid(encounter.getFormUuid());
        if (null == encounter.getLocation()) {
            encounterEntity.setLocationUuid(null);
        } else {
            encounterEntity.setLocationUuid(encounter.getLocation().getUuid());
        }

        if (0 == encounter.getEncounterProviders().size()) {
            encounterEntity.setEncounterProviderUuid(null);
        } else {
            encounterEntity.setEncounterProviderUuid(encounter.getEncounterProviders().get(0).getUuid());
        }

        return encounterEntity;
    }

    public static Encounter encounterEntityToEncounter(EncounterEntity entity) {
        Encounter encounter = new Encounter();
        if (null != entity.getEncounterType()) {
            encounter.setEncounterType(new EncounterType(entity.getEncounterType()));
        }
        encounter.setId(entity.getId());
        if (null != entity.getVisitKeyId()) {
            encounter.setVisitID(Long.parseLong(entity.getVisitKeyId()));
        }
        encounter.setUuid(entity.getUuid());
        encounter.setDisplay(entity.getDisplay());
        Long dateTime = Long.parseLong(entity.getEncounterDateTime());
        encounter.setEncounterDatetime(DateUtils.convertTime(dateTime, DateUtils.OPEN_MRS_REQUEST_FORMAT));
        encounter.setObservations(new ObservationDAO().findObservationByEncounterID(entity.getId()));
        LocationEntity location;
        try {
            location = AppDatabase
                    .getDatabase(OpenMRS.getInstance().getApplicationContext())
                    .locationRoomDAO()
                    .findLocationByUUID(entity.getLocationUuid())
                    .blockingGet();
        } catch (Exception e) {
            location = null;
        }
        encounter.setLocation(location);
        encounter.setForm(FormService.getFormByUuid(entity.getFormUuid()));
        return encounter;
    }

    public static Visit visitEntityToVisit(VisitEntity visitEntity) {
        Visit visit = new Visit();
        visit.setId(visitEntity.getId());
        visit.setUuid(visitEntity.getUuid());
        visit.setDisplay(visitEntity.getDisplay());
        visit.setVisitType(new VisitType(visitEntity.getVisitType()));
        try {
            LocationEntity locationEntity = AppDatabase
                    .getDatabase(OpenMRS.getInstance().getApplicationContext())
                    .locationRoomDAO()
                    .findLocationByName(visitEntity.getVisitPlace())
                    .blockingGet();
            visit.setLocation(locationEntity);
        } catch (Exception e) {
            visit.setLocation(new LocationEntity(visitEntity.getVisitPlace()));
        }
        visit.setStartDatetime(visitEntity.getStartDate());
        visit.setStopDatetime(visitEntity.getStopDate());
        visit.setEncounters(new EncounterDAO().findEncountersByVisitID(visitEntity.getId()));
        visit.setPatient(new PatientDAO().findPatientByID(String.valueOf(visitEntity.getPatientKeyID())));

        return visit;
    }

    public static VisitEntity visitToVisitEntity(Visit visit) {
        VisitEntity visitEntity = new VisitEntity();
        visitEntity.setId(visit.getId());
        visitEntity.setUuid(visit.getUuid());
        visitEntity.setPatientKeyID(visit.getPatient().getId());
        visitEntity.setVisitType(visit.getVisitType().getDisplay());
        visitEntity.setVisitPlace(visit.getLocation().getDisplay());
        visitEntity.setStartDate(visit.getStartDatetime());
        visitEntity.setStopDate(visit.getStopDatetime());
        return visitEntity;
    }

    public static Patient patientEntityToPatient(PatientEntity patientEntity) {
        Patient patient = new Patient(patientEntity.getId(), patientEntity.getEncounters(), null);
        patient.setDisplay(patientEntity.getDisplay());
        patient.setUuid(patientEntity.getUuid());

        PatientIdentifier patientIdentifier = new PatientIdentifier();
        patientIdentifier.setIdentifier(patientEntity.getIdentifier());
        if (patient.getIdentifiers() == null) {
            patient.setIdentifiers(new ArrayList<>());
        }
        patient.getIdentifiers().add(patientIdentifier);

        PersonName personName = new PersonName();
        personName.setGivenName(patientEntity.getGivenName());
        personName.setMiddleName(patientEntity.getMiddleName());
        personName.setFamilyName(patientEntity.getFamilyName());
        patient.getNames().add(personName);

        patient.setGender(patientEntity.getGender());
        patient.setBirthdate(patientEntity.getBirthDate());
        byte[] photoByteArray = patientEntity.getPhoto();
        if (photoByteArray != null) {
            patient.setPhoto(byteArrayToBitmap(photoByteArray));
        }

        PersonAddress personAddress = new PersonAddress();
        personAddress.setAddress1(patientEntity.getAddress_1());
        personAddress.setAddress2(patientEntity.getAddress_2());
        personAddress.setPostalCode(patientEntity.getPostalCode());
        personAddress.setCountry(patientEntity.getCountry());
        personAddress.setStateProvince(patientEntity.getState());
        personAddress.setCityVillage(patientEntity.getCity());
        patient.getAddresses().add(personAddress);

        if (patientEntity.getCauseOfDeath() != null) {
            patient.setCauseOfDeath(new Resource(ApplicationConstants.EMPTY_STRING, patientEntity.getCauseOfDeath(), new ArrayList<>(), 0));
        }
        if (patientEntity.getDeceased().equals("true")) {
            patient.setDeceased(true);
        } else {
            patient.setDeceased(false);
        }

        return patient;
    }

    public static PatientEntity patientToPatientEntity(Patient patient) {
        PatientEntity patientEntity = new PatientEntity();
        patientEntity.setDisplay(patient.getName().getNameString());
        patientEntity.setUuid(patient.getUuid());
        patientEntity.setSynced(patient.isSynced());

        if (patient.getIdentifier() != null) {
            patientEntity.setIdentifier(patient.getIdentifier().getIdentifier());
        } else {
            patientEntity.setIdentifier(null);
        }

        patientEntity.setGivenName(patient.getName().getGivenName());
        patientEntity.setMiddleName(patient.getName().getMiddleName());
        patientEntity.setFamilyName(patient.getName().getFamilyName());
        patientEntity.setGender(patient.getGender());
        patientEntity.setBirthDate(patient.getBirthdate());
        patientEntity.setDeathDate(null);

        if (null != patient.getCauseOfDeath()) {
            if (patient.getCauseOfDeath().getDisplay() == null) {
                patientEntity.setCauseOfDeath(null);
            } else {
                patientEntity.setCauseOfDeath(patient.getCauseOfDeath().getDisplay());
            }
        } else {
            patientEntity.setCauseOfDeath(null);
        }
        patientEntity.setAge(null);

        if (patient.getPhoto() != null) {
            patientEntity.setPhoto(bitmapToByteArray(patient.getPhoto()));
        } else {
            patientEntity.setPhoto(null);
        }

        if (null != patient.getAddress()) {
            patientEntity.setAddress_1(patient.getAddress().getAddress1());
            patientEntity.setAddress_2(patient.getAddress().getAddress2());
            patientEntity.setPostalCode(patient.getAddress().getPostalCode());
            patientEntity.setCountry(patient.getAddress().getCountry());
            patientEntity.setState(patient.getAddress().getStateProvince());
            patientEntity.setCity(patient.getAddress().getCityVillage());
        }

        patientEntity.setEncounters(patient.getEncounters());
        patientEntity.setDeceased(patient.isDeceased().toString());

        return patientEntity;
    }

    private static byte[] bitmapToByteArray(Bitmap image) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    private static Bitmap byteArrayToBitmap(byte[] imageByteArray) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageByteArray);
        return BitmapFactory.decodeStream(inputStream);
    }

    public static Allergy allergyEntityToAllergy(AllergyEntity allergyEntity) {
        Allergy allergy = new Allergy();
        allergy.setId(allergyEntity.getId());

        allergy.setComment(allergyEntity.getComment());

        if (allergyEntity.getAllergyReactions() != null) {
            allergy.setReactions(allergyEntity.getAllergyReactions());
        } else {
            allergy.setReactions(new ArrayList<>());
        }

        Allergen allergen = new Allergen();
        allergen.setCodedAllergen(new Resource(allergyEntity.getAllergenUUID(), allergyEntity.getAllergenDisplay(), new ArrayList<>(), 1));
        allergy.setAllergen(allergen);

        if (allergyEntity.getSeverityDisplay() != null) {
            allergy.setSeverity(new Resource(allergyEntity.getSeverityUUID(), allergyEntity.getSeverityDisplay(), new ArrayList<>(), 1));
        }
        return allergy;
    }

    public static AllergyEntity allergyToEntity(Allergy allergy, String patientID) {
        AllergyEntity allergyEntity = new AllergyEntity();
        allergyEntity.setPatientId(patientID);
        allergyEntity.setComment(allergy.getComment());
        if (allergy.getSeverity() != null) {
            allergyEntity.setSeverityDisplay(allergy.getSeverity().getDisplay());
            allergyEntity.setSeverityUUID(allergy.getSeverity().getUuid());
        }
        allergyEntity.setAllergenDisplay(allergy.getAllergen().getCodedAllergen().getDisplay());
        allergyEntity.setAllergenUUID(allergy.getAllergen().getCodedAllergen().getUuid());
        allergyEntity.setAllergyReactions(allergy.getReactions());
        return allergyEntity;
    }

    public static List<Allergy> allergyEntityListToAllergyList(List<AllergyEntity> entities) {
        ArrayList<Allergy> allergies = new ArrayList<>();
        for (AllergyEntity allergyEntity : entities) {
            allergies.add(AppDatabaseHelper.allergyEntityToAllergy(allergyEntity));
        }
        return allergies;
    }

    public static <T> Observable<T> createObservableIO(final Callable<T> func) {
        return Observable.fromCallable(func)
                .subscribeOn(Schedulers.io());
    }

}

