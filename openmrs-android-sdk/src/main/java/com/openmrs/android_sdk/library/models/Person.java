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

package com.openmrs.android_sdk.library.models;

import android.graphics.Bitmap;

import androidx.room.TypeConverters;

import com.openmrs.android_sdk.library.models.typeConverters.PersonAddressConverter;
import com.openmrs.android_sdk.library.models.typeConverters.PersonAttributeConverter;
import com.openmrs.android_sdk.library.models.typeConverters.PersonNameConverter;
import com.openmrs.android_sdk.utilities.ImageUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Person.
 *
 * <p> More on Subresources of Person https://rest.openmrs.org/#person </p>
 */
public class Person extends Resource implements Serializable {

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
    private Boolean dead;

    @SerializedName("causeOfDeath")
    @Expose
    private Resource causeOfDeath = null;

    private Bitmap photo;

    /**
     * Instantiates a new Person.
     */
    public Person() {
    }

    /**
     * Instantiates a new Person.
     *
     * @param names              the names
     * @param gender             the gender
     * @param birthdate          the birthdate
     * @param birthdateEstimated the birthdate estimated
     * @param addresses          the addresses
     * @param attributes         the attributes
     * @param photo              the photo
     * @param causeOfDeath       the cause of death
     * @param dead               the dead
     */
    public Person(List<PersonName> names, String gender, String birthdate, boolean birthdateEstimated, List<PersonAddress> addresses, List<PersonAttribute> attributes,
                  Bitmap photo, Resource causeOfDeath, boolean dead) {
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
     * Gets names.
     *
     * @return The names
     */
    public List<PersonName> getNames() {
        return names;
    }

    /**
     * Sets names.
     *
     * @param names The names
     */
    public void setNames(List<PersonName> names) {
        this.names = names;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public PersonName getName() {
        if (!names.isEmpty()) {
            return names.get(0);
        } else {
            return null;
        }
    }

    /**
     * Gets gender.
     *
     * @return The gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * Sets gender.
     *
     * @param gender The gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Gets birthdate.
     *
     * @return The birthdate
     */
    public String getBirthdate() {
        return birthdate;
    }

    /**
     * Sets birthdate.
     *
     * @param birthdate The birthdate
     */
    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    /**
     * Gets birthdate estimated.
     *
     * @return The birthdateEstimated
     */
    public boolean getBirthdateEstimated() {
        return birthdateEstimated;
    }

    /**
     * Sets birthdate estimated.
     *
     * @param birthdateEstimated The birthdate
     */
    public void setBirthdateEstimated(boolean birthdateEstimated) {
        this.birthdateEstimated = birthdateEstimated;
    }

    /**
     * Gets addresses.
     *
     * @return The addresses
     */
    public List<PersonAddress> getAddresses() {
        return addresses;
    }

    /**
     * Sets addresses.
     *
     * @param addresses The addresses
     */
    public void setAddresses(List<PersonAddress> addresses) {
        this.addresses = addresses;
    }

    /**
     * Gets address.
     *
     * @return the address
     */
    public PersonAddress getAddress() {
        if (!addresses.isEmpty()) {
            return addresses.get(0);
        } else {
            return null;
        }
    }

    /**
     * Gets attributes.
     *
     * @return The attributes
     */
    public List<PersonAttribute> getAttributes() {
        return attributes;
    }

    /**
     * Sets attributes.
     *
     * @param attributes The attributes
     */
    public void setAttributes(List<PersonAttribute> attributes) {
        this.attributes = attributes;
    }

    /**
     * Gets photo.
     *
     * @return the photo
     */
    public Bitmap getPhoto() {
        return photo;
    }

    /**
     * Sets photo.
     *
     * @param patientPhoto the patient photo
     */
    public void setPhoto(Bitmap patientPhoto) {
        this.photo = patientPhoto;
    }

    /**
     * Gets resized photo.
     *
     * @return the resized photo
     */
    public Bitmap getResizedPhoto() {
        return ImageUtils.resizePhoto(this.photo);
    }

    /**
     * Gets cause of death.
     *
     * @return the cause of death
     */
    public Resource getCauseOfDeath() {
        return causeOfDeath;
    }

    /**
     * Sets cause of death.
     *
     * @param causeOfDeath the cause of death
     */
    public void setCauseOfDeath(Resource causeOfDeath) {
        this.causeOfDeath = causeOfDeath;
    }

    /**
     * Is deceased boolean.
     *
     * @return the boolean
     */
    public Boolean isDeceased() {
        return dead;
    }

    /**
     * Sets deceased.
     *
     * @param dead the dead
     */
    public void setDeceased(Boolean dead) {
        this.dead = dead;
    }
}