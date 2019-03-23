package org.openmrs.mobile.databases.entities;

import org.openmrs.mobile.models.Resource;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "locations")
public class LocationEntity extends Resource {

    @ColumnInfo(name = "name")
    private String name;

    @NonNull
    @ColumnInfo(name = "description")
    private String description;

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

    @ColumnInfo(name = "parentLocationUuid")
    private String parentLocationuuid;

    public LocationEntity() {
    }

    public void setName(String name) {
        this.name = name;
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

    public void setDescription(@NonNull String description) {
        this.description = description;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setParentLocationuuid(String parentLocationuuid) {
        this.parentLocationuuid = parentLocationuuid;
    }

    public String getName() {
        return name;
    }

    @NonNull
    public String getDescription() {
        return description;
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

    public String getParentLocationuuid() {
        return parentLocationuuid;
    }
}
