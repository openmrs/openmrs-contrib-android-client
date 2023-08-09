package com.openmrs.android_sdk.library.databases.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.openmrs.android_sdk.library.models.DosageForm

@Entity(tableName = "drugs")
class DrugEntity {

    @PrimaryKey
    val uuid: String = ""

    @ColumnInfo(name = "display")
    val display: String = ""

    @ColumnInfo(name = "description")
    val description: String = ""

    @ColumnInfo(name = "combination")
    val combination: Boolean = false

    @ColumnInfo(name = "maximumDailyDose")
    val maximumDailyDose: Int = 0

    @ColumnInfo(name = "minimumDailyDose")
    val minimumDailyDose: Int = 0

    @ColumnInfo(name = "concept")
    val concept: String = ""

    @Embedded(prefix = "dosageForm_")
    val dosageForm: DosageFormEntity? = null

    @ColumnInfo(name = "drugReferenceMaps")
    val drugReferenceMaps: List<String>? = null

    @ColumnInfo(name = "ingredients")
    val ingredients: List<String>? = null

    @ColumnInfo(name = "name")
    val name: String = ""

    @ColumnInfo(name = "retired")
    val retired: Boolean = false

    @ColumnInfo(name = "strength")
    val strength: String = ""

    @ColumnInfo(name = "resourceVersion")
    val resourceVersion: String = ""
}