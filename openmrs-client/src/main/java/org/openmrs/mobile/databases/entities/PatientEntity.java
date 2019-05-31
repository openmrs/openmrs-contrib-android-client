package org.openmrs.mobile.databases.entities;

import org.openmrs.mobile.models.Resource;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

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
    private String photo;

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

    @Embedded(prefix = "patient_")
    private EncounterEntity encounters;

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

    public void setPhoto(String photo) {
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

    public void setEncounters(EncounterEntity encounters) {
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

    public String getPhoto() {
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

    public EncounterEntity getEncounters() {
        return encounters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PatientEntity)) return false;

        PatientEntity entity = (PatientEntity) o;

        if (synced != entity.synced) return false;
        if (identifier != null ? !identifier.equals(entity.identifier) : entity.identifier != null)
            return false;
        if (givenName != null ? !givenName.equals(entity.givenName) : entity.givenName != null)
            return false;
        if (middleName != null ? !middleName.equals(entity.middleName) : entity.middleName != null)
            return false;
        if (familyName != null ? !familyName.equals(entity.familyName) : entity.familyName != null)
            return false;
        if (gender != null ? !gender.equals(entity.gender) : entity.gender != null) return false;
        if (birthDate != null ? !birthDate.equals(entity.birthDate) : entity.birthDate != null)
            return false;
        if (deathDate != null ? !deathDate.equals(entity.deathDate) : entity.deathDate != null)
            return false;
        if (causeOfDeath != null ? !causeOfDeath.equals(entity.causeOfDeath) : entity.causeOfDeath != null)
            return false;
        if (age != null ? !age.equals(entity.age) : entity.age != null) return false;
        if (photo != null ? !photo.equals(entity.photo) : entity.photo != null) return false;
        if (address_1 != null ? !address_1.equals(entity.address_1) : entity.address_1 != null)
            return false;
        if (address_2 != null ? !address_2.equals(entity.address_2) : entity.address_2 != null)
            return false;
        if (city != null ? !city.equals(entity.city) : entity.city != null) return false;
        if (state != null ? !state.equals(entity.state) : entity.state != null) return false;
        if (country != null ? !country.equals(entity.country) : entity.country != null)
            return false;
        if (postalCode != null ? !postalCode.equals(entity.postalCode) : entity.postalCode != null)
            return false;
        return encounters != null ? encounters.equals(entity.encounters) : entity.encounters == null;
    }

    @Override
    public int hashCode() {
        int result = (synced ? 1 : 0);
        result = 31 * result + (identifier != null ? identifier.hashCode() : 0);
        result = 31 * result + (givenName != null ? givenName.hashCode() : 0);
        result = 31 * result + (middleName != null ? middleName.hashCode() : 0);
        result = 31 * result + (familyName != null ? familyName.hashCode() : 0);
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        result = 31 * result + (birthDate != null ? birthDate.hashCode() : 0);
        result = 31 * result + (deathDate != null ? deathDate.hashCode() : 0);
        result = 31 * result + (causeOfDeath != null ? causeOfDeath.hashCode() : 0);
        result = 31 * result + (age != null ? age.hashCode() : 0);
        result = 31 * result + (photo != null ? photo.hashCode() : 0);
        result = 31 * result + (address_1 != null ? address_1.hashCode() : 0);
        result = 31 * result + (address_2 != null ? address_2.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (postalCode != null ? postalCode.hashCode() : 0);
        result = 31 * result + (encounters != null ? encounters.hashCode() : 0);
        return result;
    }
}
