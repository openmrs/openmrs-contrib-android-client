package com.example.openmrs_android_sdk.library.databases.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.example.openmrs_android_sdk.library.models.Resource;

@Entity(tableName = "patients")
public class PatientEntity extends Resource {
    @NonNull
    @ColumnInfo(name = "synced")
    private boolean synced;
    @ColumnInfo(name = "identifier")
    private String identifier;
    @ColumnInfo(name = "givenName")
    private String givenName;
    @ColumnInfo(name = "middleName")
    private String middleName;
    @ColumnInfo(name = "familyName")
    private String familyName;
    @ColumnInfo(name = "gender")
    private String gender;
    @ColumnInfo(name = "birthDate")
    private String birthDate;
    @ColumnInfo(name = "deathDate")
    private String deathDate;
    @ColumnInfo(name = "causeOfDeath")
    private String causeOfDeath;
    @ColumnInfo(name = "age")
    private String age;
    @ColumnInfo(name = "photo")
    private byte[] photo;
    @ColumnInfo(name = "address1")
    private String address_1;
    @ColumnInfo(name = "address2")
    private String address_2;
    @ColumnInfo(name = "city")
    private String city;
    @ColumnInfo(name = "state")
    private String state;
    @ColumnInfo(name = "country")
    private String country;
    @ColumnInfo(name = "postalCode")
    private String postalCode;
    @ColumnInfo(name = "dead")
    private String deceased;
    @ColumnInfo(name = "encounters")
    private String encounters;

    public PatientEntity() {
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public void setDeathDate(String deathDate) {
        this.deathDate = deathDate;
    }

    public void setCauseOfDeath(String causeOfDeath) {
        this.causeOfDeath = causeOfDeath;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public void setAddress_1(String address_1) {
        this.address_1 = address_1;
    }

    public void setAddress_2(String address_2) {
        this.address_2 = address_2;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setDeceased(String deceased) {
        this.deceased = deceased;
    }

    public void setEncounters(String encounters) {
        this.encounters = encounters;
    }

    public boolean isSynced() {
        return synced;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getDeathDate() {
        return deathDate;
    }

    public String getCauseOfDeath() {
        return causeOfDeath;
    }

    public String getAge() {
        return age;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public String getAddress_1() {
        return address_1;
    }

    public String getAddress_2() {
        return address_2;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getDeceased() {
        return deceased;
    }

    public String getEncounters() {
        return encounters;
    }
}
