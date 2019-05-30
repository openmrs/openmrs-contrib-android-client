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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LocationEntity)) return false;

        LocationEntity entity = (LocationEntity) o;

        if (name != null ? !name.equals(entity.name) : entity.name != null) return false;
        if (!description.equals(entity.description)) return false;
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
        return parentLocationuuid != null ? parentLocationuuid.equals(entity.parentLocationuuid) : entity.parentLocationuuid == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + description.hashCode();
        result = 31 * result + (address_1 != null ? address_1.hashCode() : 0);
        result = 31 * result + (address_2 != null ? address_2.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (postalCode != null ? postalCode.hashCode() : 0);
        result = 31 * result + (parentLocationuuid != null ? parentLocationuuid.hashCode() : 0);
        return result;
    }
}
