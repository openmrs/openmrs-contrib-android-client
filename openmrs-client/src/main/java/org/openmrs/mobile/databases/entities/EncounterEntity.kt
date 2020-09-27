package org.openmrs.mobile.databases.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.openmrs.mobile.models.Resource

@Entity(tableName = "encounters")
class EncounterEntity : Resource() {
    @ColumnInfo(name = "visit_id")
    @SerializedName("visit_id")
    @Expose
    var visitKeyId: String? = null

    @ColumnInfo(name = "encounterDatetime")
    @SerializedName("encounterDatetime")
    @Expose
    lateinit var encounterDateTime: String

    @ColumnInfo(name = "type")
    @SerializedName("type")
    @Expose
    var encounterType: String? = null

    @ColumnInfo(name = "patient_uuid")
    @SerializedName("patient_uuid")
    @Expose
    var patientUuid: String? = null

    @ColumnInfo(name = "form_uuid")
    @SerializedName("form_uuid")
    @Expose
    var formUuid: String? = null

    @ColumnInfo(name = "location_uuid")
    @SerializedName("location_uuid")
    @Expose
    var locationUuid: String? = null

    @ColumnInfo(name = "encounter_provider_uuid")
    @SerializedName("encounter_provider_uuid")
    @Expose
    var encounterProviderUuid: String? = null

}