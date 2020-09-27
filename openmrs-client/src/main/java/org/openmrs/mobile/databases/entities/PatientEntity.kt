package org.openmrs.mobile.databases.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.openmrs.mobile.models.Resource

@Entity(tableName = "patients")
class PatientEntity : Resource() {
    @ColumnInfo(name = "synced")
    @SerializedName("synced")
    @Expose
    var isSynced = false

    @ColumnInfo(name = "identifier")
    @SerializedName("identifier")
    @Expose
    var identifier: String? = null

    @ColumnInfo(name = "givenName")
    @SerializedName("givenName")
    @Expose
    var givenName: String? = null

    @ColumnInfo(name = "middleName")
    @SerializedName("middleName")
    @Expose
    var middleName: String? = null

    @ColumnInfo(name = "familyName")
    @SerializedName("familyName")
    @Expose
    var familyName: String? = null

    @ColumnInfo(name = "gender")
    @SerializedName("gender")
    @Expose
    var gender: String? = null

    @ColumnInfo(name = "birthDate")
    @SerializedName("birthDate")
    @Expose
    var birthDate: String? = null

    @ColumnInfo(name = "deathDate")
    @SerializedName("deathDate")
    @Expose
    var deathDate: String? = null

    @ColumnInfo(name = "causeOfDeath")
    @SerializedName("causeOfDeath")
    @Expose
    var causeOfDeath: String? = null

    @ColumnInfo(name = "age")
    @SerializedName("age")
    @Expose
    var age: String? = null

    @ColumnInfo(name = "photo")
    @SerializedName("photo")
    @Expose
    var photo: ByteArray? = null

    @ColumnInfo(name = "address1")
    @SerializedName("address1")
    @Expose
    var address_1: String? = null

    @ColumnInfo(name = "address2")
    @SerializedName("address2")
    @Expose
    var address_2: String? = null

    @ColumnInfo(name = "city")
    @SerializedName("city")
    @Expose
    var city: String? = null

    @ColumnInfo(name = "state")
    @SerializedName("state")
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

    @ColumnInfo(name = "dead")
    @SerializedName("dead")
    @Expose
    var deceased: String? = null

    @ColumnInfo(name = "encounters")
    @SerializedName("encounters")
    @Expose
    var encounters: String? = null

}
