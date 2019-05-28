package org.openmrs.mobile;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.openmrs.mobile.databases.entities.ConceptEntity;

import java.util.Collections;
import java.util.List;

import io.reactivex.functions.Predicate;

public class ConceptsDaoTest extends DatabaseTest {

    private org.openmrs.mobile.databases.dao.ConceptsDao subject;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        subject = database.conceptsDao();
    }

    @Test
    public void insertConcept_validConcept_insertAndGetId() {
        String uuid = "123-213";
        ConceptEntity concept = new ConceptEntity();
        concept.setId(10L);
        concept.setDisplay("Concepts");
        concept.setUuid(uuid);
        concept.setLinks(Collections.emptyList());

        subject.save(concept);
        subject.findByUUID(uuid)
                .test()
                .assertValue(new Predicate<ConceptEntity>() {

            @Override
            public boolean test(ConceptEntity conceptEntity) throws Exception {
                return concept.equals(conceptEntity);
            }

        });
    }

    @Test
    public void getAll_saveTwoConcepts_returnTwoConcepts() {
        subject.save(generateTestData(10l, "First concept", "000-111"));
        subject.save(generateTestData(20l, "Second concept", "111-222"));

        subject.getAll()
                .test()
                .assertValue(new Predicate<List<ConceptEntity>>() {
                    @Override
                    public boolean test(List<ConceptEntity> conceptEntities) throws Exception {
                        return conceptEntities.size() == 2;
                    }
                });
    }

    @Test
    public void getCount_saveThreeConcepts_returnThreeConcepts() {
        subject.save(generateTestData(10l, "First concept", "000-111"));
        subject.save(generateTestData(20l, "Second concept", "111-222"));
        subject.save(generateTestData(30l, "Third concept", "222-333"));

        subject.getCount()
                .test()
                .assertValue(new Predicate<Long>() {
                    @Override
                    public boolean test(Long count) throws Exception {
                        return count == 3;
                    }
                });
    }

    @Test
    public void updateConcept_putConceptInCacheAndUpdate_returnUpdatedConcept() {
        String firstUuid = "000-111";
        String secondUuid = "111-222";
        ConceptEntity firstConcept = generateTestData(10l, "First concept", firstUuid);
        ConceptEntity secondConcept = generateTestData(20l, "Second concept", secondUuid);
        subject.save(firstConcept);

        subject.update(firstUuid, secondConcept.getUuid(), secondConcept.getDisplay());

        subject.findByUUID(secondUuid)
                .test()
                .assertValue(new Predicate<ConceptEntity>() {

                    @Override
                    public boolean test(ConceptEntity conceptEntity) throws Exception {
                        return conceptEntity.getUuid().equals(secondUuid)
                                && conceptEntity.getDisplay().equals("Second concept");
                    }

                });
        subject.findByUUID(firstUuid)
                .test()
                .assertNoValues();
    }

    private ConceptEntity generateTestData(long id, String display, String uuid) {
        ConceptEntity concept = new ConceptEntity();
        concept.setId(id);
        concept.setDisplay(display);
        concept.setUuid(uuid);
        concept.setLinks(Collections.emptyList());
        return concept;
    }
}
