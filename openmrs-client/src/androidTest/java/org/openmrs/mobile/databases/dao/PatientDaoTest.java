package org.openmrs.mobile.databases.dao;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.openmrs.mobile.DatabaseTest;
import org.openmrs.mobile.databases.entities.EncounterEntity;
import org.openmrs.mobile.databases.entities.PatientEntity;

import java.util.List;

import io.reactivex.functions.Predicate;

public class PatientDaoTest extends DatabaseTest {

    private PatientDao subject;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() throws Exception {
        super.setUp();
        subject = database.patientDao();
    }

    @Test
    public void testSave_putTwoEntities_getAllReturnsCollectionOfTwo() {
        EncounterEntity encounterEntity = getEncounterEntity(40l, "123-123", "Encounter",
                "50",  "60", "70", "Visit", "22nd of May");
        PatientEntity entity = getPatient(10l, "123", "M",
                "Beijing", "Shanghai", "34", "China",
                "who knows", "Missouri", "USA", "12/10/1903",
                encounterEntity, "Tranquil", "male", "Jon",
                "101", "Johnson", "https://bit.ly/2W4Ofth",
                "2000000", "China", true);

        subject.save(entity);

        subject.getAll()
                .test()
                .assertValue(new Predicate<List<PatientEntity>>() {
                    @Override
                    public boolean test(List<PatientEntity> patientEntities) throws Exception {
                        return patientEntities.size() == 1
                                && patientEntities.contains(entity);
                    }
                });
    }

    @Test
    public void testDelete_putTwoEntities_getAllReturnsOneWithRightId() {
        EncounterEntity encounterEntity = getEncounterEntity(40l, "123-123", "Encounter",
                "50",  "60", "70", "Visit", "22nd of May");
        PatientEntity entity = getPatient(10l, "123", "M",
                "Beijing", "Shanghai", "34", "China",
                "who knows", "Missouri", "USA", "12/10/1903",
                encounterEntity, "Tranquil", "male", "Jon",
                "101", "Johnson", "https://bit.ly/2W4Ofth",
                "2000000", "China", true);
        EncounterEntity encounterEntitySecond = getEncounterEntity(50l, "423-423", "Encounter",
                "50",  "60", "70", "Visit", "22nd of May");
        PatientEntity entitySecond = getPatient(20l, "123-123", "M",
                "Beijing", "Shanghai", "34", "China",
                "who knows", "Missouri", "USA", "12/10/1903",
                encounterEntitySecond, "Tranquil", "male", "Jon",
                "101", "Johnson", "https://bit.ly/2W4Ofth",
                "2000000", "China", true);

        subject.save(entity);
        subject.save(entitySecond);

        subject.delete("123-123");

        subject.getAll()
                .test()
                .assertValue(new Predicate<List<PatientEntity>>() {
                    @Override
                    public boolean test(List<PatientEntity> patientEntities) throws Exception {
                        return patientEntities.size() == 1
                                && patientEntities.contains(entity);
                    }
                });
    }

    @Test
    public void testGetPatientByUuid_putTwoEntities_getAllReturnsOneEntity() {
        EncounterEntity encounterEntity = getEncounterEntity(40l, "123-123", "Encounter",
                "50",  "60", "70", "Visit", "22nd of May");
        PatientEntity entity = getPatient(10l, "123", "M",
                "Beijing", "Shanghai", "34", "China",
                "who knows", "Missouri", "USA", "12/10/1903",
                encounterEntity, "Tranquil", "male", "Jon",
                "101", "Johnson", "https://bit.ly/2W4Ofth",
                "2000000", "China", true);
        EncounterEntity encounterEntitySecond = getEncounterEntity(50l, "423-423", "Encounter",
                "50",  "60", "70", "Visit", "22nd of May");
        PatientEntity entitySecond = getPatient(20l, "123-123", "M",
                "Beijing", "Shanghai", "34", "China",
                "who knows", "Missouri", "USA", "12/10/1903",
                encounterEntitySecond, "Tranquil", "male", "Jon",
                "101", "Johnson", "https://bit.ly/2W4Ofth",
                "2000000", "China", true);
        subject.save(entity);
        subject.save(entitySecond);

        subject.getPatientByUuid("123-123")
                .test()
                .assertValue(new Predicate<List<PatientEntity>>() {
                    @Override
                    public boolean test(List<PatientEntity> patientEntities) throws Exception {
                        return patientEntities.size() == 1
                                && patientEntities.contains(entity);
                    }
                });
    }

    @Test
    public void testGetUnSyncedPatients_putOnlySyncEntity_returnsEmptyCollection() {
        EncounterEntity encounterEntity = getEncounterEntity(40l, "123-123", "Encounter",
                "50",  "60", "70", "Visit", "22nd of May");
        PatientEntity entity = getPatient(10l, "123", "M",
                "Beijing", "Shanghai", "34", "China",
                "who knows", "Missouri", "USA", "12/10/1903",
                encounterEntity, "Tranquil", "male", "Jon",
                "101", "Johnson", "https://bit.ly/2W4Ofth",
                "2000000", "China", true);
        subject.save(entity);

        subject.getUnsyncedPatients()
                .test()
                .assertValue(new Predicate<List<PatientEntity>>() {
                    @Override
                    public boolean test(List<PatientEntity> patientEntities) throws Exception {
                        return patientEntities.isEmpty();
                    }
                });
    }

    @Test
    public void testGetUnSyncedPatients_putOneUnSyncEntity_returnsOneEntity() {
        EncounterEntity encounterEntity = getEncounterEntity(40l, "123-123", "Encounter",
                "50",  "60", "70", "Visit", "22nd of May");
        PatientEntity entity = getPatient(10l, "123", "M",
                "Beijing", "Shanghai", "34", "China",
                "who knows", "Missouri", "USA", "12/10/1903",
                encounterEntity, "Tranquil", "male", "Jon",
                "101", "Johnson", "https://bit.ly/2W4Ofth",
                "2000000", "China", false);
        EncounterEntity encounterEntitySecond = getEncounterEntity(50l, "423-423", "Encounter",
                "50",  "60", "70", "Visit", "22nd of May");
        PatientEntity entitySecond = getPatient(20l, "123", "M",
                "Beijing", "Shanghai", "34", "China",
                "who knows", "Missouri", "USA", "12/10/1903",
                encounterEntitySecond, "Tranquil", "male", "Jon",
                "101", "Johnson", "https://bit.ly/2W4Ofth",
                "2000000", "China", true);
        subject.save(entity);
        subject.save(entitySecond);

        subject.getUnsyncedPatients()
                .test()
                .assertValue(new Predicate<List<PatientEntity>>() {
                    @Override
                    public boolean test(List<PatientEntity> patientEntities) throws Exception {
                        return patientEntities.size() == 1
                                && !patientEntities.get(0).isSynced();
                    }
                });
    }

    @Test
    public void testGetPatientById_putTwoEntities_returnPatientWIthRightId() {
        EncounterEntity encounterEntity = getEncounterEntity(40l, "123-123", "Encounter",
                "50",  "60", "70", "Visit", "22nd of May");
        PatientEntity entity = getPatient(10l, "123", "M",
                "Beijing", "Shanghai", "34", "China",
                "who knows", "Missouri", "USA", "12/10/1903",
                encounterEntity, "Tranquil", "male", "Jon",
                "101", "Johnson", "https://bit.ly/2W4Ofth",
                "2000000", "China", false);
        subject.save(entity);

        subject.getPatientById(10)
                .test()
                .assertValue(new Predicate<List<PatientEntity>>() {
                    @Override
                    public boolean test(List<PatientEntity> patientEntities) throws Exception {
                        return patientEntities.size() == 1
                                && patientEntities.contains(entity);
                    }
                });
    }

    private PatientEntity getPatient(long id, String uuid, String display,
                                     String address1, String address2, String age,
                                     String birthDate, String causeOfDeath, String city,
                                     String country, String deathDate, EncounterEntity encounterEntity,
                                     String familyName, String gender, String givenName,
                                     String identifier, String middleName, String photo,
                                     String postalCode, String state, boolean synced) {
        PatientEntity entity = new PatientEntity();
        entity.setId(id);
        entity.setUuid(uuid);
        entity.setDisplay(display);
        entity.setAddress_1(address1);
        entity.setAddress_2(address2);
        entity.setAge(age);
        entity.setBirthDate(birthDate);
        entity.setCauseOfDeath(causeOfDeath);
        entity.setCity(city);
        entity.setCountry(country);
        entity.setDeathDate(deathDate);
        entity.setEncounters(encounterEntity);
        entity.setFamilyName(familyName);
        entity.setGender(gender);
        entity.setGivenName(givenName);
        entity.setIdentifier(identifier);
        entity.setMiddleName(middleName);
        entity.setPhoto(photo);
        entity.setPostalCode(postalCode);
        entity.setState(state);
        entity.setSynced(synced);
        return entity;
    }

    private EncounterEntity getEncounterEntity(Long id, String uuid, String display, String visitKeyId,
                                               String patientUuid, String formUuid, String encounterType,
                                               String encounterDateTime) {
        EncounterEntity encounterEntity = new EncounterEntity();
        encounterEntity.setId(id);
        encounterEntity.setUuid(uuid);
        encounterEntity.setDisplay(display);
        encounterEntity.setVisitKeyId(visitKeyId);
        encounterEntity.setPatientUuid(patientUuid);
        encounterEntity.setFormUuid(formUuid);
        encounterEntity.setEncounterType(encounterType);
        encounterEntity.setEncounterDateTime(encounterDateTime);
        return encounterEntity;
    }
}