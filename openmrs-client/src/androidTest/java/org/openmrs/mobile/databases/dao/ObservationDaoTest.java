package org.openmrs.mobile.databases.dao;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.openmrs.mobile.DatabaseTest;
import org.openmrs.mobile.databases.entities.ObservationEntity;

import java.util.List;

import io.reactivex.functions.Predicate;

public class ObservationDaoTest extends DatabaseTest {


    private ObservationDao subject;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        subject = database.observationDao();
    }

    @Test
    public void testSave_putObservation_findAllReturnCollectionWithOneEntity() {
        ObservationEntity entity = getObservation(
                100, "123-123", "concept-123-123", "88%",
                "list", "note", "Order",
                "Display", 123, "world"
                );

        subject.save(entity);

        subject.getAll()
                .test()
                .assertValue(new Predicate<List<ObservationEntity>>() {
                    @Override
                    public boolean test(List<ObservationEntity> observations) throws Exception {
                        return observations.size() == 2;
                    }
                });
    }

    @Test
    public void testDelete_putTwoObservations_getAllReturnsOneObservationWithRightUUID() {
        ObservationEntity firstEntity = getObservation(
                100, "123-123", "concept-123-123", "88%",
                "list", "note", "Order",
                "Display", 123, "world"
        );
        ObservationEntity secondEntity = getObservation(
                200, "123-321", "concept-123-321", "88%",
                "list", "note", "Order",
                "Display", 123, "world"
        );
        subject.save(firstEntity);
        subject.save(secondEntity);

        subject.delete("123-321");

        subject.getAll()
                .test()
                .assertValue(new Predicate<List<ObservationEntity>>() {
                    @Override
                    public boolean test(List<ObservationEntity> entities) throws Exception {
                        return entities.get(0).equals(firstEntity);
                    }
                });
    }

    @Test
    public void testFindObservationByEncounterId_putTwoConcepts_returnObservationWithRightId() {
        ObservationEntity firstEntity = getObservation(
                100, "123-123", "concept-123-123", "88%",
                "list", "note", "Order",
                "Display", 123, "world"
        );
        ObservationEntity secondEntity = getObservation(
                200, "123-321", "concept-123-321", "88%",
                "list", "note", "Order",
                "Display", 123, "world"
        );
        subject.save(firstEntity);
        subject.save(secondEntity);

        subject.findObservationByUUID("123-321")
                .test()
                .assertValue(new Predicate<List<ObservationEntity>>() {
                    @Override
                    public boolean test(List<ObservationEntity> list) throws Exception {
                        return list.get(0).equals(secondEntity);
                    }
                });
    }

    @Test
    public void testFindObservationByEncounterId_putTwoObservations_returnObservationWithRightConceptsId() {
        ObservationEntity firstEntity = getObservation(
                100, "123-123", "concept-123-123", "88%",
                "list", "note", "Order",
                "Display", 123, "world"
        );
        ObservationEntity secondEntity = getObservation(
                200, "123-321", "concept-123-321", "88%",
                "list", "note", "Order",
                "Display", 1234, "world"
        );
        subject.save(firstEntity);
        subject.save(secondEntity);

        subject.findObservationByEncounterId(String.valueOf(123))
                .test()
                .assertValue(new Predicate<List<ObservationEntity>>() {
                    @Override
                    public boolean test(List<ObservationEntity> list) throws Exception {
                        return list.get(0).equals(firstEntity);
                    }
                });
    }

    @Test
    public void testFindObservationByEncounterId_putTwoSimilarObservations_returnBothObservation() {
        ObservationEntity firstEntity = getObservation(
                100, "123-123", "concept-123-123", "88%",
                "list", "note", "Order",
                "Display", 123, "world"
        );
        ObservationEntity secondEntity = getObservation(
                200, "123-321", "concept-123-321", "88%",
                "list", "note", "Order",
                "Display", 123, "world"
        );
        subject.save(firstEntity);
        subject.save(secondEntity);

        subject.findObservationByEncounterId(String.valueOf(123))
                .test()
                .assertValue(new Predicate<List<ObservationEntity>>() {
                    @Override
                    public boolean test(List<ObservationEntity> list) throws Exception {
                        return list.get(0).equals(firstEntity);
                    }
                });
    }

    private ObservationEntity getObservation(long id, String uuid, String conceptUuid, String certanity,
                                             String list, String note, String order,
                                             String value, int encounterKeyId, String display) {
        ObservationEntity entity = new ObservationEntity();
        entity.setId(id);
        entity.setUuid(uuid);
        entity.setConceptuuid(conceptUuid);
        entity.setDiagnosisCertainty(certanity);
        entity.setDiagnosisList(list);
        entity.setDiagnosisNote(note);
        entity.setDiagnosisOrder(order);
        entity.setDisplayValue(value);
        entity.setEncounterKeyID(encounterKeyId);
        entity.setDisplay(display);
        return entity;
    }
}