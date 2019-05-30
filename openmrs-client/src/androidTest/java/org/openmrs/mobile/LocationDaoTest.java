package org.openmrs.mobile;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.openmrs.mobile.databases.dao.LocationDao;
import org.openmrs.mobile.databases.entities.LocationEntity;

import java.util.List;

import io.reactivex.functions.Predicate;

public class LocationDaoTest extends DatabaseTest {

    private LocationDao subject;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        subject = database.locationsDao();
    }

    @Test
    public void testSaveLocation_putLocation_getLocationReturnsOneEntity() {
        LocationEntity entity = getLocationData(10L, "location", "123-123",
                "data", "desc", "shanghai", "tiantong lu",
                "Shanghai", "Shanghai", "China", "123412",
                "123");
        subject.save(entity);

        subject.getAll()
                .test()
                .assertValue(new Predicate<List<LocationEntity>>() {
                    @Override
                    public boolean test(List<LocationEntity> locationEntities) throws Exception {
                        return locationEntities.size() == 1
                                && locationEntities.get(0).equals(entity);
                    }
                });
    }

    @Test
    public void testDeleteAll_saveTwoLocations_getAllReturnsEmptyCollection() {
        LocationEntity entityOne = getLocationData(10L, "location", "123-123",
                "data", "desc", "shanghai", "tiantong lu",
                "Shanghai", "Shanghai", "China", "123412",
                "123");
        subject.save(entityOne);
        LocationEntity entityTwo = getLocationData(20L, "location", "123-123",
                "data", "desc", "shanghai", "tiantong lu",
                "Shanghai", "Shanghai", "China", "123412",
                "123");
        subject.save(entityTwo);

        subject.deleteAll();

        subject.getAll()
                .test()
                .assertValue(new Predicate<List<LocationEntity>>() {
                    @Override
                    public boolean test(List<LocationEntity> locationEntities) throws Exception {
                        return locationEntities.isEmpty();
                    }
                });
    }

    @Test
    public void testFindLocationByName_saveTwoLocations_returnLocationWithRightDisplayValue() {
        LocationEntity entityOne = getLocationData(10L, "location", "123-123",
                "data", "desc", "shanghai", "tiantong lu",
                "Shanghai", "Shanghai", "China", "123412",
                "123");
        subject.save(entityOne);
        LocationEntity entityTwo = getLocationData(20L, "location2", "123-123",
                "data", "desc", "shanghai", "tiantong lu",
                "Shanghai", "Shanghai", "China", "123412",
                "123");
        subject.save(entityTwo);

        subject.findLocationByName("location")
                .test()
                .assertValue(new Predicate<List<LocationEntity>>() {
                    @Override
                    public boolean test(List<LocationEntity> locationEntities) throws Exception {
                        return locationEntities.size() == 1
                                && locationEntities.get(0).equals(entityOne);
                    }
                });
    }

    private LocationEntity getLocationData(Long id, String display, String uuid, String name,
                                           String desc, String address1, String address2,
                                           String city, String state, String country,
                                           String postalCode, String parentLocationUuid) {
        LocationEntity entity = new LocationEntity();
        entity.setId(id);
        entity.setDisplay(display);
        entity.setUuid(uuid);
        entity.setName(name);
        entity.setDescription(desc);
        entity.setAddress_1(address1);
        entity.setAddress_2(address2);
        entity.setCity(city);
        entity.setState(state);
        entity.setCountry(country);
        entity.setPostalCode(postalCode);
        entity.setParentLocationuuid(parentLocationUuid);
        return entity;
    }
}
