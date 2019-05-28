package org.openmrs.mobile.databases.converters;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.openmrs.mobile.databases.entities.FormResourceEntity;
import org.openmrs.mobile.databases.entities.ObscreateEntity;

import java.lang.reflect.Type;
import java.util.List;

public class AppTypeConverter {

    private static Gson formResourceGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    private static Type formResourceListType = new TypeToken<List<FormResourceEntity>>(){}.getType();

    private static Gson obscreateGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    private static Type obscreateType = new TypeToken<List<ObscreateEntity>>(){}.getType();

    @TypeConverter
    public static List<FormResourceEntity> toFormResourceEntity(String resourcelist) {
        if (resourcelist == null) {
            return null;
        }
        return formResourceGson.fromJson(resourcelist, formResourceListType);
    }

    @TypeConverter
    public static String fromFormResourceEntity(List<FormResourceEntity> resourcelist) {
        if (resourcelist == null) {
            return null;
        }
        return formResourceGson.toJson(resourcelist, formResourceListType);
    }

    @TypeConverter
    public static List<ObscreateEntity> toObscreateEntity(String list) {
        if (list == null) {
            return null;
        }
        return obscreateGson.fromJson(list, obscreateType);
    }

    @TypeConverter
    public static String fromObscreateEntity(List<ObscreateEntity> list) {
        if (list == null) {
            return null;
        }
        return obscreateGson.toJson(list, obscreateType);
    }
}
