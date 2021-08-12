package com.openmrs.android_sdk.library.databases.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.openmrs.android_sdk.library.models.Resource;

/**
 * The type Patient entity.
 */
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

    /**
     * Instantiates a new Patient entity.
     */
    public PatientEntity() {
    }

    /**
     * Sets synced.
     *
     * @param synced the synced
     */
    public void setSynced(boolean synced) {
        this.synced = synced;
    }

    /**
     * Sets identifier.
     *
     * @param identifier the identifier
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Sets given name.
     *
     * @param givenName the given name
     */
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    /**
     * Sets middle name.
     *
     * @param middleName the middle name
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    /**
     * Sets family name.
     *
     * @param familyName the family name
     */
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    /**
     * Sets gender.
     *
     * @param gender the gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Sets birth date.
     *
     * @param birthDate the birth date
     */
    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * Sets death date.
     *
     * @param deathDate the death date
     */
    public void setDeathDate(String deathDate) {
        this.deathDate = deathDate;
    }

    /**
     * Sets cause of death.
     *
     * @param causeOfDeath the cause of death
     */
    public void setCauseOfDeath(String causeOfDeath) {
        this.causeOfDeath = causeOfDeath;
    }

    /**
     * Sets age.
     *
     * @param age the age
     */
    public void setAge(String age) {
        this.age = age;
    }

    /**
     * Sets photo.
     *
     * @param photo the photo
     */
    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    /**
     * Sets address 1.
     *
     * @param address_1 the address 1
     */
    public void setAddress_1(String address_1) {
        this.address_1 = address_1;
    }

    /**
     * Sets address 2.
     *
     * @param address_2 the address 2
     */
    public void setAddress_2(String address_2) {
        this.address_2 = address_2;
    }

    /**
     * Sets city.
     *
     * @param city the city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Sets state.
     *
     * @param state the state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Sets country.
     *
     * @param country the country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Sets postal code.
     *
     * @param postalCode the postal code
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Sets deceased.
     *
     * @param deceased the deceased
     */
    public void setDeceased(String deceased) {
        this.deceased = deceased;
    }

    /**
     * Sets encounters.
     *
     * @param encounters the encounters
     */
    public void setEncounters(String encounters) {
        this.encounters = encounters;
    }

    /**
     * Is synced boolean.
     *
     * @return the boolean
     */
    public boolean isSynced() {
        return synced;
    }

    /**
     * Gets identifier.
     *
     * @return the identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Gets given name.
     *
     * @return the given name
     */
    public String getGivenName() {
        return givenName;
    }

    /**
     * Gets middle name.
     *
     * @return the middle name
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Gets family name.
     *
     * @return the family name
     */
    public String getFamilyName() {
        return familyName;
    }

    /**
     * Gets gender.
     *
     * @return the gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * Gets birth date.
     *
     * @return the birth date
     */
    public String getBirthDate() {
        return birthDate;
    }

    /**
     * Gets death date.
     *
     * @return the death date
     */
    public String getDeathDate() {
        return deathDate;
    }

    /**
     * Gets cause of death.
     *
     * @return the cause of death
     */
    public String getCauseOfDeath() {
        return causeOfDeath;
    }

    /**
     * Gets age.
     *
     * @return the age
     */
    public String getAge() {
        return age;
    }

    /**
     * Get photo byte [ ].
     *
     * @return the byte [ ]
     */
    public byte[] getPhoto() {
        return photo;
    }

    /**
     * Gets address 1.
     *
     * @return the address 1
     */
    public String getAddress_1() {
        return address_1;
    }

    /**
     * Gets address 2.
     *
     * @return the address 2
     */
    public String getAddress_2() {
        return address_2;
    }

    /**
     * Gets city.
     *
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * Gets state.
     *
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * Gets country.
     *
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Gets postal code.
     *
     * @return the postal code
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Gets deceased.
     *
     * @return the deceased
     */
    public String getDeceased() {
        return deceased;
    }

    /**
     * Gets encounters.
     *
     * @return the encounters
     */
    public String getEncounters() {
        return encounters;
    }
}
