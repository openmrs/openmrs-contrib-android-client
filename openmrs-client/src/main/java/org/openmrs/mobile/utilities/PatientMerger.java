package org.openmrs.mobile.utilities;

import org.openmrs.mobile.models.retrofit.Patient;
import org.openmrs.mobile.models.retrofit.Person;
import org.openmrs.mobile.models.retrofit.PersonAddress;
import org.openmrs.mobile.models.retrofit.PersonName;

public class PatientMerger {

    public Patient mergePatient(Patient oldPatient, Patient newPatient){
        mergePatientsPerson(oldPatient.getPerson(), newPatient.getPerson());
        oldPatient.setId(newPatient.getId());
        return oldPatient;
    }

    private void mergePatientsPerson(Person oldPerson, Person newPerson) {
        mergePersonNames(oldPerson.getName(), newPerson.getName());
        mergePersonAddress(oldPerson.getAddress(), newPerson.getAddress());
        oldPerson.setGender(getNewValueIfOldIsNull(oldPerson.getGender(), newPerson.getGender()));
        oldPerson.setBirthdate(getNewValueIfOldIsNull(oldPerson.getBirthdate(), newPerson.getBirthdate()));
    }

    private void mergePersonAddress(PersonAddress oldAddress, PersonAddress newAddress) {
        oldAddress.setAddress1(getNewValueIfOldIsNull(oldAddress.getAddress1(), newAddress.getAddress1()));
        oldAddress.setAddress2(getNewValueIfOldIsNull(oldAddress.getAddress2(), newAddress.getAddress2()));
        oldAddress.setCityVillage(getNewValueIfOldIsNull(oldAddress.getCityVillage(), newAddress.getCityVillage()));
        oldAddress.setCountry(getNewValueIfOldIsNull(oldAddress.getCountry(), newAddress.getCountry()));
        oldAddress.setPostalCode(getNewValueIfOldIsNull(oldAddress.getPostalCode(), newAddress.getPostalCode()));
        oldAddress.setStateProvince(getNewValueIfOldIsNull(oldAddress.getStateProvince(), newAddress.getStateProvince()));
    }

    private void mergePersonNames(PersonName oldName, PersonName newName) {
        oldName.setGivenName(getNewValueIfOldIsNull(oldName.getGivenName(), newName.getGivenName()));
        oldName.setMiddleName(getNewValueIfOldIsNull(oldName.getMiddleName(), newName.getMiddleName()));
        oldName.setFamilyName(getNewValueIfOldIsNull(oldName.getFamilyName(), newName.getFamilyName()));
    }

    private String getNewValueIfOldIsNull(String oldValue, String newValue){
        if(!StringUtils.notNull(oldValue)){
            return newValue;
        }
        return oldValue;
    }
}
