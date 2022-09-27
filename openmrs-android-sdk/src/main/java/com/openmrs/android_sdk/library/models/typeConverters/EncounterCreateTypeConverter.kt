package com.openmrs.android_sdk.library.models.typeConverters

import androidx.room.TypeConverter
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.openmrs.android_sdk.library.models.EncounterProviderCreate
import java.io.Serializable
import java.lang.reflect.Modifier

object EncounterProviderCreateTypeConverter : Serializable {
    @JvmStatic
    @TypeConverter
    fun fromString(value: String?): List<EncounterProviderCreate> {
        val listType = object : TypeToken<List<EncounterProviderCreate?>?>() {}.type
        val builder = GsonBuilder()
        builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
        val gson = builder.create()
        return gson.fromJson(value, listType)
    }

    @JvmStatic
    @TypeConverter
    fun listToString(list: List<EncounterProviderCreate?>?): String {
        val builder = GsonBuilder()
        builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
        val gson = builder.create()
        return gson.toJson(list)
    }
}
