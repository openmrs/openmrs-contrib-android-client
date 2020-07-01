/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.mobile.models;

import android.graphics.Bitmap;

import androidx.room.TypeConverters;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.openmrs.mobile.models.typeConverters.PersonAddressConverter;
import org.openmrs.mobile.models.typeConverters.PersonAttributeConverter;
import org.openmrs.mobile.models.typeConverters.PersonNameConverter;
import org.openmrs.mobile.utilities.ImageUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PersonUpdate extends Resource implements Serializable {

    @TypeConverters(PersonNameConverter.class)
    @SerializedName("names")
    @Expose
    private List<PersonName> names = new ArrayList<>();
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("birthdate")
    @Expose
    private String birthdate;
    @SerializedName("birthdateEstimated")
    @Expose
    private boolean birthdateEstimated;

    @TypeConverters(PersonAddressConverter.class)
    @SerializedName("addresses")
    @Expose
    private List<PersonAddress> addresses = new ArrayList<>();

    @TypeConverters(PersonAttributeConverter.class)
    @SerializedName("attributes")
    @Expose
    private List<PersonAttribute> attributes = new ArrayList<>();

    @SerializedName("dead")
    @Expose
    private Boolean dead = false;

    @SerializedName("causeOfDeath")
    @Expose
    private String causeOfDeath = null;

    private Bitmap photo;

    public PersonUpdate() {
    }

    public PersonUpdate(List<PersonName> names, String gender, String birthdate, boolean birthdateEstimated, List<PersonAddress> addresses, List<PersonAttribute> attributes,
                        Bitmap photo, String causeOfDeath, boolean dead) {
        this.names = names;
        this.gender = gender;
        this.birthdate = birthdate;
        this.birthdateEstimated = birthdateEstimated;
        this.addresses = addresses;
        this.attributes = attributes;
        this.photo = photo;
        this.causeOfDeath = causeOfDeath;
        this.dead = dead;
    }

    /**
     * @return The names
     */
    public List<PersonName> getNames() {
        return names;
    }

    /**
     * @param names The names
     */
    public void setNames(List<PersonName> names) {
        this.names = names;
    }

    public PersonName getName() {
        if (!names.isEmpty()) {
            return names.get(0);
        } else {
            return null;
        }
    }

    /**
     * @return The gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * @param gender The gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * @return The birthdate
     */
    public String getBirthdate() {
        return birthdate;
    }

    /**
     * @param birthdate The birthdate
     */
    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    /**
     * @return The birthdateEstimated
     */
    public boolean getBirthdateEstimated() {
        return birthdateEstimated;
    }

    /**
     * @param birthdateEstimated The birthdate
     */
    public void setBirthdateEstimated(boolean birthdateEstimated) {
        this.birthdateEstimated = birthdateEstimated;
    }

    /**
     * @return The addresses
     */
    public List<PersonAddress> getAddresses() {
        return addresses;
    }

    /**
     * @param addresses The addresses
     */
    public void setAddresses(List<PersonAddress> addresses) {
        this.addresses = addresses;
    }

    public PersonAddress getAddress() {
        if (!addresses.isEmpty()) {
            return addresses.get(0);
        } else {
            return null;
        }
    }

    /**
     * @return The attributes
     */
    public List<PersonAttribute> getAttributes() {
        return attributes;
    }

    /**
     * @param attributes The attributes
     */
    public void setAttributes(List<PersonAttribute> attributes) {
        this.attributes = attributes;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap patientPhoto) {
        this.photo = patientPhoto;
    }

    public Bitmap getResizedPhoto() {
        return ImageUtils.resizePhoto(this.photo);
    }

    public String getCauseOfDeath() {
        return causeOfDeath;
    }

    public void setCauseOfDeath(String causeOfDeath) {
        this.causeOfDeath = causeOfDeath;
    }

    public Boolean getDead() {
        return dead;
    }

    public void setDead(Boolean dead) {
        this.dead = dead;
    }
}