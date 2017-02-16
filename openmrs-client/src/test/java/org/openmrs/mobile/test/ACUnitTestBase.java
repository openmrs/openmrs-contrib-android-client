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
        patient.setPerson(createPerson(id));
        patient.setIdentifiers(Collections.singletonList(createIdentifier(id)));
        return patient;
    }

    protected PatientIdentifier createIdentifier(Long id) {
        PatientIdentifier identifier = new PatientIdentifier();
        identifier.setIdentifier("some_identifier_" + id);
        return identifier;
    }

    protected Person createPerson(Long id) {
        Person person = new Person();
        person.setNames(Collections.singletonList(createPersonName(id)));
        person.setAddresses(Collections.singletonList(createPersonAddress(id)));
        person.setGender("M");
        person.setBirthdate("25-02-2016");
        return person;
    }

    protected PersonAddress createPersonAddress(Long id) {
        PersonAddress personAddress = new PersonAddress();
        personAddress.setAddress1("address_1_" + id);
        personAddress.setAddress2("address_2_" + id);
        personAddress.setCityVillage("city_" + id);
        personAddress.setStateProvince("state_" + id);
        personAddress.setCountry("country_" + id);
        personAddress.setPostalCode("postal_code_" + id);
        return personAddress;
    }

    protected PersonName createPersonName(Long id) {
        PersonName personName = new PersonName();
        personName.setGivenName("given_name_" + id);
        personName.setMiddleName("middle_name_" + id);
        personName.setFamilyName("family_name_" + id);
        return personName;
    }

    protected Patient createPatient(Long id, String identifier) {
        Patient patient = createPatient(id);
        PatientIdentifier patientIdentifier = new PatientIdentifier();
        patientIdentifier.setIdentifier(identifier);
        patient.setIdentifiers(Collections.singletonList(patientIdentifier));
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
