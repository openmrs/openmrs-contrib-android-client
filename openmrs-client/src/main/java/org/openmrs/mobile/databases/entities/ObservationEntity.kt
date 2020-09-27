package org.openmrs.mobile.databases.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.openmrs.mobile.models.Resource

@Entity(tableName = "observations")
class ObservationEntity : Resource() {
    @ColumnInfo(name = "encounter_id")
    @SerializedName("encounter_id")
    @Expose
    var encounterKeyID: Long = 0

    @ColumnInfo(name = "displayValue")
    @SerializedName("displayValue")
    @Expose
    var displayValue: String? = null

    @ColumnInfo(name = "diagnosisOrder")
    @SerializedName("diagnosisOrder")
    @Expose
    var diagnosisOrder: String? = null

    @ColumnInfo(name = "diagnosisList")
    @SerializedName("diagnosisList")
    @Expose
    var diagnosisList: String? = null

    @ColumnInfo(name = "diagnosisCertainty")
    @SerializedName("diagnosisCertainty")
    @Expose
    var diagnosisCertainty: String? = null

    @ColumnInfo(name = "diagnosisNote")
    @SerializedName("diagnosisNote")
    @Expose
    var diagnosisNote: String? = null

    @ColumnInfo(name = "conceptUuid")
    @SerializedName("conceptUuid")
    @Expose
    var conceptuuid: String? = null

}
