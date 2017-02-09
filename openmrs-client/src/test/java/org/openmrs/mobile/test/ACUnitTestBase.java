package org.openmrs.mobile.test;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.PatientIdentifier;
import org.openmrs.mobile.models.Person;
import org.openmrs.mobile.models.PersonAddress;
import org.openmrs.mobile.models.PersonName;
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
        personName.setGivenName("given_name_" + id);
        personName.setMiddleName("middle_name_" + id);
        personName.setFamilyName("family_name_" + id);
        person.setNames(Collections.singletonList(personName));
        PersonAddress personAddress = new PersonAddress();
        person.setAddresses(Collections.singletonList(personAddress));
        patient.setPerson(person);
        PatientIdentifier identifier = new PatientIdentifier();
        identifier.setIdentifier("some_identifier");
        patient.setIdentifiers(Collections.singletonList(identifier));
        return patient;
    }

    protected <T> Call<Results<T>> mockSuccessCall(List<T> list) {
        return new MockSuccessResponse<>(list);
    }


    protected  <T> Call<T> mockSuccessCall(T object) {
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
