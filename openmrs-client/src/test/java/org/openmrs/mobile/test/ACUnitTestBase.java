package org.openmrs.mobile.test;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Person;
import org.openmrs.mobile.models.PersonAddress;
import org.openmrs.mobile.models.PersonName;
import org.openmrs.mobile.models.Resource;
import org.openmrs.mobile.models.Results;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;

@RunWith(MockitoJUnitRunner.class)
public abstract class ACUnitTestBase {

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    protected Patient createPatient(Long id) {
        Patient patient = new Patient();
        patient.setId(id);
        patient.setUuid("patient_one_uuid"+id);
        Person person = new Person();
        PersonName personName = new PersonName();
        person.setNames(Collections.singletonList(personName));
        PersonAddress personAddress = new PersonAddress();
        person.setAddresses(Collections.singletonList(personAddress));
        patient.setPerson(person);
        return patient;
    }

    protected <T> Call<Results<T>> mockSuccessCall(List<T> list) {
        return new MockSuccessResponse<>(list);
    }


    protected  <T extends Resource> Call<T> mockSuccessCall(T object) {
        return new MockSuccessResponse<>(object);
    }

    protected <T> Call<T> mockErrorCall(int code){
        return new MockErrorResponse<>(code);
    }

    protected <T> Call<T> mockFailureCall() {
        Throwable throwable = Mockito.mock(Throwable.class);
        return new MockFailure<>(throwable);
    }
}
