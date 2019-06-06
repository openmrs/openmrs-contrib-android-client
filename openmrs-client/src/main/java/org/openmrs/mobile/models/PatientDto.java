/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * PatientDTO represents patient data on create/update API.
 * This object is serialized as request/response body to send the patient related over the wire.
 *
 */
public class PatientDto {

    @SerializedName("uuid")
    @Expose
    protected String uuid;

    @SerializedName("identifiers")
    @Expose
    private List<PatientIdentifier> identifiers = new ArrayList<>();

    @SerializedName("person")
    @Expose
    private Person person;

    /**
     *
     * @return
     *     The uuid for the patient
     *
     */
    public String getUuid() {
        return uuid;
    }

    /**
     *
     * @param person
     *      Set the person data for patient.
     *
     */
    public void setPerson(Person person) {
        this.person = person;
    }


    /**
     *
     * @param identifiers
     *     Set the identifiers for the patient.
     *
     */
    public void setIdentifiers(List<PatientIdentifier> identifiers) {
        this.identifiers = identifiers;
    }


    /**
     *
     * @return
     *     Returns the person object from patient.
     *
     */
    public Person getPerson() {
        return person;
    }


    /**
     *
     * @return
     *     The patient object after transformation from patientDto Details
     *
     */
    public Patient getPatient(){
        Patient patient = new Patient();
        Person person = getPerson();
        patient.setIdentifiers(identifiers);
        patient.setUuid(uuid);
        patient.setNames(person.getNames());
        patient.setGender(person.getGender());
        patient.setBirthdate(person.getBirthdate());
        patient.setBirthdateEstimated(person.getBirthdateEstimated());
        patient.setPhoto(person.getPhoto());
        patient.setAttributes(person.getAttributes());
        patient.setAddresses(person.getAddresses());
        return  patient;
    }
}
