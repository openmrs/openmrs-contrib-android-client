package org.openmrs.mobile;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.openmrs.mobile.databases.dao.EncounterDao;
import org.openmrs.mobile.databases.entities.EncounterEntity;

import java.util.List;

import io.reactivex.functions.Predicate;

public class EncounterDaoTest extends DatabaseTest {

    private EncounterDao subject;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        subject = database.encounterDao();
    }

    @Test
    public void testInsertEncounter_validEncounter_insertAndGet() {
        String uuid = "123-123";
        EncounterEntity encounterEntity = new EncounterEntity();
        encounterEntity.setId(10L);
        encounterEntity.setUuid(uuid);
        encounterEntity.setDisplay("Encounter");
        encounterEntity.setVisitKeyId("20");
        encounterEntity.setPatientUuid("30");
        encounterEntity.setFormUuid("40");
        encounterEntity.setEncounterType("Visit");
        encounterEntity.setEncounterDateTime("21st of May");

        subject.save(encounterEntity);

        subject.getEncounterByUUID(uuid)
                .test()
                .assertValue(new Predicate<List<EncounterEntity>>() {
                    @Override
                    public boolean test(List<EncounterEntity> encounterEntities) throws Exception {
                        return encounterEntity.equals(encounterEntities.get(0));
                    }
                });
    }

    @Test
    public void testGetLastVitalsEncounterId_setTwoValidEncounters_getEncounterForUuid() {
        String firstUuid = "123-123";
        String secondUuid = "456-456";
        EncounterEntity entityFirst = getEncounterEntity(10l, firstUuid, "Display",
                "20",  "30", "30", "Visit", "21st of May");
        EncounterEntity entitySecond = getEncounterEntity(40l, secondUuid, "Encounter",
                null,  "60", "70", "Visit", "22nd of May");
        subject.save(entityFirst);
        subject.save(entitySecond);

        subject.getLastVitalsEncounterId("60")
                .test()
                .assertValue(new Predicate<Long>() {
                    @Override
                    public boolean test(Long value) throws Exception {
                        return value.equals(entitySecond.getId());
                    }
                });
    }

    @Test
    public void testGetEncounterTypeByFormName_validEncounterType_getEncounterType() {
        String firstUuid = "123-123";
        String secondUuid = "456-456";
        EncounterEntity entityFirst = getEncounterEntity(10l, firstUuid, "Display",
                "20",  "30", "30", "Visit", "21st of May");
        EncounterEntity entitySecond = getEncounterEntity(40l, secondUuid, "Encounter",
                "50",  "60", "70", "Visit", "22nd of May");
        subject.save(entityFirst);
        subject.save(entitySecond);

        subject.getEncounterTypeByFormName("Encounter")
                .test()
                .assertValue(new Predicate<List<EncounterEntity>>() {
                    @Override
                    public boolean test(List<EncounterEntity> encounterEntities) throws Exception {
                        return entitySecond.equals(encounterEntities.get(0));
                    }
                });
    }

    @Test
    public void testGetLastVitalsEncounter_putTwoEncounters_getVitalsEncounter() {
        String firstUuid = "123-123";
        String secondUuid = "456-456";
        EncounterEntity entityFirst = getEncounterEntity(10l, firstUuid, "Display",
                "20",  "30", "30", "Visit", "21st of May");
        EncounterEntity entitySecond = getEncounterEntity(40l, secondUuid, "Encounter",
                "50",  "60", "70", "Visit", "22nd of May");
        subject.save(entityFirst);
        subject.save(entitySecond);

        subject.getLastVitalsEncounter("60", "Visit")
                .test()
                .assertValue(new Predicate<List<EncounterEntity>>() {
                    @Override
                    public boolean test(List<EncounterEntity> encounterEntities) throws Exception {
                        return encounterEntities.size() == 1 && encounterEntities.get(0).getId().equals(40l);
                    }
                });
    }

    @Test
    public void testGetEncounterByVisitId_putTwoEncounters_getOneEncounterWithRightId() {
        String firstUuid = "123-123";
        String secondUuid = "456-456";
        EncounterEntity entityFirst = getEncounterEntity(10l, firstUuid, "Display",
                "20",  "30", "30", "Visit", "21st of May");
        EncounterEntity entitySecond = getEncounterEntity(40l, secondUuid, "Encounter",
                "50",  "60", "70", "Visit", "22nd of May");
        subject.save(entityFirst);
        subject.save(entitySecond);

        subject.getEncounterByVisitId(Long.valueOf("50"))
                .test()
                .assertValue(new Predicate<List<EncounterEntity>>() {
                    @Override
                    public boolean test(List<EncounterEntity> encounterEntities) throws Exception {
                        return encounterEntities.size() == 1 && encounterEntities.get(0).getId().equals(40l);
                    }
                });
    }

    @Test
    public void testGetEncounterByUUID_putTwoElements__getOneEncounterWithRightId() {
        String firstUuid = "123-123";
        String secondUuid = "456-456";
        EncounterEntity entityFirst = getEncounterEntity(10l, firstUuid, "Display",
                "20",  "30", "30", "Visit", "21st of May");
        EncounterEntity entitySecond = getEncounterEntity(40l, secondUuid, "Encounter",
                "50",  "60", "70", "Visit", "22nd of May");
        subject.save(entityFirst);
        subject.save(entitySecond);

        subject.getEncounterByUUID(secondUuid)
                .test()
                .assertValue(new Predicate<List<EncounterEntity>>() {
                    @Override
                    public boolean test(List<EncounterEntity> encounterEntities) throws Exception {
                        return encounterEntities.size() == 1 && encounterEntities.get(0).getUuid().equals(secondUuid);
                    }
                });
    }

    @Test
    public void testDelete_putTwoEncounters_returnOneEncounterWithRightId() {
        String firstUuid = "123-123";
        String secondUuid = "456-456";
        EncounterEntity entityFirst = getEncounterEntity(10l, firstUuid, "Display",
                "20",  "30", "30", "Visit", "21st of May");
        EncounterEntity entitySecond = getEncounterEntity(40l, secondUuid, "Encounter",
                "50",  "60", "70", "Visit", "22nd of May");
        subject.save(entityFirst);
        subject.save(entitySecond);

        subject.delete(firstUuid);

        subject.getAll()
                .test()
                .assertValue(new Predicate<List<EncounterEntity>>() {
                    @Override
                    public boolean test(List<EncounterEntity> encounterEntities) throws Exception {
                        return encounterEntities.size() == 1 && encounterEntities.get(0).getUuid().equals(secondUuid);
                    }
                });
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
