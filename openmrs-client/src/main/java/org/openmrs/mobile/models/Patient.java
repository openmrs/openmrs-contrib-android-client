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

import org.openmrs.mobile.utilities.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Patient extends Person implements Serializable{

    private Long id;
    private String encounters = "";

    @SerializedName("identifiers")
    @Expose
    private List<PatientIdentifier> identifiers = new ArrayList<>();

    public Long getId() {
        return id;
    }

    @SerializedName("person")
    public Person getPerson(){
        Person person = new Person();
        person.setNames(getNames());
        person.setGender(getGender());
        person.setAddresses(getAddresses());
        person.setAttributes(getAttributes());
        person.setPhoto(getPhoto());
        person.setBirthdate(getBirthdate());
        person.setBirthdateEstimated(getBirthdateEstimated());

        return person;
    }

    public PatientDto getPatientDto(){
        PatientDto patientDto = new PatientDto();
        patientDto.setPerson(getPerson());
        patientDto.setIdentifiers(getIdentifiers());
        return patientDto;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The identifiers
     */
    public List<PatientIdentifier> getIdentifiers() {
        return identifiers;
    }

    /**
     * 
     * @param identifiers
     *     The identifiers
     */
    public void setIdentifiers(List<PatientIdentifier> identifiers) {
        this.identifiers = identifiers;
    }

    public PatientIdentifier getIdentifier() {
        if (!identifiers.isEmpty()) {
            return identifiers.get(0);
        } else {
            return null;
        }
    }


    public boolean isSynced()
    {
        return !StringUtils.isBlank(getUuid());
        //Keeping it this way until the synced flag can be made to work
    }

    public String getEncounters() {
        return encounters;
    }

    public void setEncounters(String encounters) {
         this.encounters=encounters;
    }


    public void addEncounters(Long encid)
    {
        this.encounters += encid+",";
    }

    public Map<String, String> toMap(){
        Map<String, String> map = new HashMap<>();
        puToMapIfNotNull(map, "givenname", getName().getGivenName());
        puToMapIfNotNull(map, "middlename",getName().getMiddleName());
        puToMapIfNotNull(map, "familyname", getName().getFamilyName());
        puToMapIfNotNull(map, "gender", getGender());
        puToMapIfNotNull(map, "birthdate", getBirthdate());
        puToMapIfNotNull(map, "address1", getAddress().getAddress1());
        puToMapIfNotNull(map, "address2", getAddress().getAddress2());
        puToMapIfNotNull(map, "city", getAddress().getCityVillage());
        puToMapIfNotNull(map, "state", getAddress().getStateProvince());
        puToMapIfNotNull(map, "postalcode", getAddress().getPostalCode());
        puToMapIfNotNull(map, "country", getAddress().getCountry());
        return map;
    }

    private void puToMapIfNotNull(Map<String, String> map, String key, String value) {
        if(StringUtils.notNull(value)){
            map.put(key, value);
        }
    }
}
