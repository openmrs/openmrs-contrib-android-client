package org.openmrs.mobile.utilities;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.openmrs.mobile.models.retrofit.Resource;

import java.lang.reflect.Type;

public class ResourceSerializer implements JsonSerializer<Resource>{
    @Override
    public JsonElement serialize(Resource src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getUuid());
    }
}
