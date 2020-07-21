package org.openmrs.mobile.databases.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.openmrs.mobile.models.Resource

@Entity(tableName = "locations")
class LocationEntity : Resource {
    @ColumnInfo(name = "name")
    @SerializedName("name")
    @Expose
    var name: String? = null

    @ColumnInfo(name = "description")
    @SerializedName("description")
    @Expose
    var description: String? = null

    @ColumnInfo(name = "address1")
    @SerializedName("address1")
    @Expose
    var address_1: String? = null

    @ColumnInfo(name = "address2")
    @SerializedName("address2")
    @Expose
    var address_2: String? = null

    @ColumnInfo(name = "city")
    @SerializedName("cityVillage")
    @Expose
    var city: String? = null

    @ColumnInfo(name = "state")
    @SerializedName("stateProvince")
    @Expose
    var state: String? = null

    @ColumnInfo(name = "country")
    @SerializedName("country")
    @Expose
    var country: String? = null

    @ColumnInfo(name = "postalCode")
    @SerializedName("postalCode")
    @Expose
    var postalCode: String? = null

    @ColumnInfo(name = "parentLocationUuid")
    @SerializedName("parentLocationUuid")
    @Expose
    var parentLocationuuid: String? = null

    constructor(display: String) {
        this.display = display
    }

    constructor(id: Long?, name: String, parentLocationUuid: String, description: String, address2: String, address1: String, cityVillage: String, stateProvince: String, country: String, postalCode: String) {
        this.id = id
        this.name = name
        this.parentLocationuuid = parentLocationUuid
        this.description = description
        this.address_2 = address2
        this.address_1 = address1
        this.city = cityVillage
        this.state = stateProvince
        this.country = country
        this.postalCode = postalCode
    }
}