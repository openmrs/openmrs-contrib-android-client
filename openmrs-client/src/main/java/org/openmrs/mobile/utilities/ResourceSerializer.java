/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.mobile.utilities;

import android.support.annotation.NonNull;

import com.activeandroid.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;

import org.openmrs.mobile.models.Resource;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;

public class ResourceSerializer implements JsonSerializer<Resource> {

    public static final String RESOURCE_SERIALIZER = "RESOURCE_SERIALIZER";
    public static final String EXCEPTION = "exception";

    @Override
    public JsonElement serialize(Resource src, Type typeOfSrc, JsonSerializationContext context) {
        Gson myGson = getGson();
        JsonObject srcJson = new JsonObject();
        Field[] declaredFields = src.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.getAnnotation(Expose.class) != null) {
                field.setAccessible(true);
                if (Resource.class.isAssignableFrom(field.getType())) {
                    try {
                        if (field.get(src) != null) {
                            srcJson.add(field.getName(), serializeField((Resource) field.get(src), context));
                        }
                    } catch (IllegalAccessException e) {
                        Log.e(RESOURCE_SERIALIZER, EXCEPTION, e);
                    }
                } else if (Collection.class.isAssignableFrom(field.getType())) {
                    try {
                        Collection collection = ((Collection) field.get(src));
                        if (collection != null && !collection.isEmpty()) {
                            if (isResourceCollection(collection)) {
                                JsonArray jsonArray = new JsonArray();
                                for (Object resource : collection) {
                                    jsonArray.add(serializeField((Resource) resource, context));
                                }
                                srcJson.add(field.getName(), jsonArray);
                            } else {
                                JsonArray jsonArray = new JsonArray();
                                for (Object object : collection) {
                                    jsonArray.add(myGson.toJsonTree(object));
                                }
                                srcJson.add(field.getName(), jsonArray);
                            }
                        }
                    } catch (IllegalAccessException e) {
                        Log.e(RESOURCE_SERIALIZER, EXCEPTION, e);
                    }
                } else {
                    try {
                        srcJson.add(field.getName(), myGson.toJsonTree(field.get(src)));
                    } catch (IllegalAccessException e) {
                        Log.e(RESOURCE_SERIALIZER, EXCEPTION, e);
                    }
                }
            }
        }
        return srcJson;
    }

    @NonNull
    private Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        return gsonBuilder
                .excludeFieldsWithoutExposeAnnotation()
                .create();
    }

    private JsonElement serializeField(Resource src, JsonSerializationContext context) {
        if (src.getUuid() != null) {
            return new JsonPrimitive(src.getUuid());
        } else {
            return context.serialize(src);
        }
    }


    private boolean isResourceCollection(Collection collection) {
        return Resource.class.isAssignableFrom(collection.toArray()[0].getClass());
    }
}
