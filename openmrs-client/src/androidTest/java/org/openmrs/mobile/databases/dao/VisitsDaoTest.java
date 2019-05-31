package org.openmrs.mobile.databases.dao;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.openmrs.mobile.DatabaseTest;
import org.openmrs.mobile.databases.entities.VisitsEntity;

import java.util.Collections;
import java.util.List;

import io.reactivex.functions.Predicate;

public class VisitsDaoTest extends DatabaseTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private VisitsDao subject;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        subject = database.visitDao();
    }

    @Test
    public void testInsert_putOneEntity_getAllReturnsThisEntity() {
        VisitsEntity entity = getEntity(10L, "123-123", "visit",
                123, "21/03/1900 15:14", "21/03/1900 15:48",
                "Aman Hospital", "checkup");

        subject.save(entity);

        subject.getAll()
                .test()
                .assertValue(new Predicate<List<VisitsEntity>>() {
                    @Override
                    public boolean test(List<VisitsEntity> visitsEntities) throws Exception {
                        return visitsEntities.size() == 1;
                    }
                });
    }

    @Test
    public void testDelete_putTwoEntities_getAllReturnsOneEntityWithDifferentUuid() {
        VisitsEntity entityOne = getEntity(10L, "123", "visit",
                123, "21/03/1900 15:14", "21/03/1900 15:48",
                "Aman Hospital", "checkup");
        VisitsEntity entitySecond = getEntity(20L, "123-123", "visit",
                123, "21/03/1900 15:14", "21/03/1900 15:48",
                "Aman Hospital", "checkup");
        subject.save(entityOne);
        subject.save(entitySecond);

        subject.delete("123-123");

        subject.getAll()
                .test()
                .assertValue(new Predicate<List<VisitsEntity>>() {
                    @Override
                    public boolean test(List<VisitsEntity> visitsEntities) throws Exception {
                        return visitsEntities.size() == 1
                                && visitsEntities.get(0).equals(entityOne);
                    }
                });
    }

    @Test
    public void testDeleteByPatientId_putTwoEntities_getAllReturnsOneEntityWithDifferentId() {
        VisitsEntity entityOne = getEntity(10L, "123", "visit",
                123, "21/03/1900 15:14", "21/03/1900 15:48",
                "Aman Hospital", "checkup");
        VisitsEntity entitySecond = getEntity(20L, "123-123", "visit",
                1234, "21/03/1900 15:14", "21/03/1900 15:48",
                "Aman Hospital", "checkup");
        subject.save(entityOne);
        subject.save(entitySecond);

        subject.deleteByPatientId(String.valueOf(1234));

        subject.getAll()
                .test()
                .assertValue(new Predicate<List<VisitsEntity>>() {
                    @Override
                    public boolean test(List<VisitsEntity> visitsEntities) throws Exception {
                        return visitsEntities.size() == 1
                                && visitsEntities.get(0).equals(entityOne);
                    }
                });
    }

    @Test
    public void testDeleteByPatientId_twoEntitiesWithTheSameId_getAllReturnsEmptyList() {
        VisitsEntity entityOne = getEntity(10L, "123", "visit",
                123, "21/03/1900 15:14", "21/03/1900 15:48",
                "Aman Hospital", "checkup");
        VisitsEntity entitySecond = getEntity(20L, "123-123", "visit",
                123, "21/03/1900 15:14", "21/03/1900 15:48",
                "Aman Hospital", "checkup");
        subject.save(entityOne);
        subject.save(entitySecond);

        subject.deleteByPatientId(String.valueOf(123));

        subject.getAll()
                .test()
                .assertValue(new Predicate<List<VisitsEntity>>() {
                    @Override
                    public boolean test(List<VisitsEntity> visitsEntities) throws Exception {
                        return visitsEntities.isEmpty();
                    }
                });
    }

    @Test
    public void testGetActiveVisits_putThreeEntitiesTwoAreActiveVisit_returnsTwoEntities() {
        VisitsEntity entityOne = getEntity(10L, "123", "visit",
                123, "21/03/1900 15:14", null,
                "Aman Hospital", "checkup");
        VisitsEntity entitySecond = getEntity(20L, "123-123", "visit",
                123, "21/03/1900 15:14", "",
                "Aman Hospital", "checkup");
        VisitsEntity entityThird = getEntity(30L, "123-123", "visit",
                123, "21/03/1900 15:14", "21/03/1900 15:48",
                "Aman Hospital", "checkup");
        subject.save(entityOne);
        subject.save(entitySecond);
        subject.save(entityThird);

        subject.getActiveVisits()
                .test()
                .assertValue(new Predicate<List<VisitsEntity>>() {
                    @Override
                    public boolean test(List<VisitsEntity> visitsEntities) throws Exception {
                        return visitsEntities.size() == 2
                                && !visitsEntities.contains(entityThird);
                    }
                });
    }

    @Test
    public void testGetVisitsByPatientId_putTwoEntitiesWithDifferentPatientId_returnOneEntity() {
        VisitsEntity entityFirst = getEntity(10L, "123", "visit",
                123, "21/03/1900 15:14", "21/03/1900 15:48",
                "Aman Hospital", "checkup");
        VisitsEntity entitySecond = getEntity(20L, "123-123", "visit",
                1234, "21/03/1900 15:14", "21/03/1900 15:48",
                "Aman Hospital", "checkup");
        subject.save(entityFirst);
        subject.save(entitySecond);

        subject.getVisitsByPatientId("1234")
                .test()
                .assertValue(new Predicate<List<VisitsEntity>>() {
                    @Override
                    public boolean test(List<VisitsEntity> visitsEntities) throws Exception {
                        return visitsEntities.size() == 1
                                && visitsEntities.get(0).equals(entitySecond);
                    }
                });
    }


    @Test
    public void testGetVisitsByPatientId_putTwoEntitiesWithSamePatientId_returnBothEntities() {
        VisitsEntity entityFirst = getEntity(10L, "123", "visit",
                123, "21/03/1900 15:14", "21/03/1900 15:48",
                "Aman Hospital", "checkup");
        VisitsEntity entitySecond = getEntity(20L, "123-123", "visit",
                123, "21/03/1900 15:14", "21/03/1900 15:48",
                "Aman Hospital", "checkup");
        subject.save(entityFirst);
        subject.save(entitySecond);

        subject.getVisitsByPatientId("123")
                .test()
                .assertValue(new Predicate<List<VisitsEntity>>() {
                    @Override
                    public boolean test(List<VisitsEntity> visitsEntities) throws Exception {
                        return visitsEntities.size() == 2
                                && visitsEntities.contains(visitsEntities.get(0))
                                && visitsEntities.contains(visitsEntities.get(1));
                    }
                });
    }

    @Test
    public void testGetActiveVisitsByPatientId_putThreeEntitiesTwoAreActiveVisitOnlyOneHasRightPatientId_returnsOneEntities() {
        VisitsEntity entityOne = getEntity(10L, "123", "visit",
                123, "21/03/1900 15:14", null,
                "Aman Hospital", "checkup");
        VisitsEntity entitySecond = getEntity(20L, "123-123", "visit",
                1234, "21/03/1900 15:14", "",
                "Aman Hospital", "checkup");
        VisitsEntity entityThird = getEntity(30L, "123-123", "visit",
                123, "21/03/1900 15:14", "21/03/1900 15:48",
                "Aman Hospital", "checkup");
        subject.save(entityOne);
        subject.save(entitySecond);
        subject.save(entityThird);

        subject.getActiveVisitsByPatientId("1234")
                .test()
                .assertValue(new Predicate<List<VisitsEntity>>() {
                    @Override
                    public boolean test(List<VisitsEntity> visitsEntities) throws Exception {
                        return visitsEntities.size() == 1
                                && visitsEntities.contains(entitySecond);
                    }
                });
    }

    @Test
    public void testGetVisitsByUuid_putSeveralEntities_returnOneWithRightUuid() {
        VisitsEntity entityOne = getEntity(10L, "123", "visit",
                123, "21/03/1900 15:14", null,
                "Aman Hospital", "checkup");
        VisitsEntity entitySecond = getEntity(20L, "123-123", "visit",
                1234, "21/03/1900 15:14", "",
                "Aman Hospital", "checkup");
        VisitsEntity entityThird = getEntity(30L, "123-123", "visit",
                123, "21/03/1900 15:14", "21/03/1900 15:48",
                "Aman Hospital", "checkup");
        subject.save(entityOne);
        subject.save(entitySecond);
        subject.save(entityThird);

        subject.getVisitsByUuid("123")
                .test()
                .assertValue(new Predicate<List<VisitsEntity>>() {
                    @Override
                    public boolean test(List<VisitsEntity> visitsEntities) throws Exception {
                        return visitsEntities.size() == 1
                                && visitsEntities.contains(entityOne);
                    }
                });
    }

    private VisitsEntity getEntity(long id, String uuid, String display, int patientId,
                                   String startDate, String stopDate, String visitPlace,
                                   String visitType) {
        VisitsEntity entity = new VisitsEntity();
        entity.setId(id);
        entity.setUuid(uuid);
        entity.setDisplay(display);
        entity.setPatientKeyID(patientId);
        entity.setStartDate(startDate);
        entity.setStopDate(stopDate);
        entity.setVisitPlace(visitPlace);
        entity.setVisitType(visitType);
        return entity;
    }
}