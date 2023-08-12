package com.openmrs.android_sdk.library.databases.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.openmrs.android_sdk.library.models.DosageForm

@Entity(tableName = "drugs")
class DrugEntity {

    @PrimaryKey
    var uuid: String = ""

    @ColumnInfo(name = "display")
    var display: String = ""

    @ColumnInfo(name = "description")
    var description: String = ""

    @ColumnInfo(name = "combination")
    var combination: Boolean = false

    @ColumnInfo(name = "maximumDailyDose")
    var maximumDailyDose: Int = 0

    @ColumnInfo(name = "minimumDailyDose")
    var minimumDailyDose: Int = 0

    @ColumnInfo(name = "concept")
    var concept: String = ""

    @Embedded(prefix = "dosageForm_")
    var dosageForm: DosageFormEntity? = null

    @ColumnInfo(name = "drugReferenceMaps")
    var drugReferenceMaps: List<String>? = null

    @ColumnInfo(name = "ingredients")
    var ingredients: List<String>? = null

    @ColumnInfo(name = "name")
    var name: String = ""

    @ColumnInfo(name = "retired")
    var retired: Boolean = false

    @ColumnInfo(name = "strength")
    var strength: String = ""

    @ColumnInfo(name = "resourceVersion")
    var resourceVersion: String = ""
}