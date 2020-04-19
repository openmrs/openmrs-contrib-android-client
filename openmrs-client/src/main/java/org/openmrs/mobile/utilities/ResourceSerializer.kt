package org.openmrs.mobile.utilities

import com.google.gson.*
import com.google.gson.annotations.Expose
import org.openmrs.mobile.models.Resource
import org.openmrs.mobile.utilities.ActiveAndroid.util.Log
import java.lang.reflect.Type


class ResourceSerializer : JsonSerializer<Resource> {
    override fun serialize(src: Resource, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val myGson = gson
        val srcJson = JsonObject()
        val declaredFields = src.javaClass.declaredFields
        for (field in declaredFields) {
            if (field.getAnnotation(Expose::class.java) != null) {
                field.isAccessible = true
                if (Resource::class.java.isAssignableFrom(field.type)) {
                    try {
                        if (field[src] != null) {
                            srcJson.add(field.name, serializeField(field[src] as Resource, context))
                        }
                    } catch (e: IllegalAccessException) {
                        Log.e(RESOURCE_SERIALIZER, EXCEPTION, e)
                    }
                } else if (MutableCollection::class.java.isAssignableFrom(field.type)) {
                    try {
                        val collection = field[src] as Collection<*>
                        if (collection != null && !collection.isEmpty()) {
                            if (isResourceCollection(collection)) {
                                val jsonArray = JsonArray()
                                for (resource in collection) {
                                    jsonArray.add(serializeField(resource as Resource, context))
                                }
                                srcJson.add(field.name, jsonArray)
                            } else {
                                val jsonArray = JsonArray()
                                for (`object` in collection) {
                                    jsonArray.add(myGson.toJsonTree(`object`))
                                }
                                srcJson.add(field.name, jsonArray)
                            }
                        }
                    } catch (e: IllegalAccessException) {
                        Log.e(RESOURCE_SERIALIZER, EXCEPTION, e)
                    }
                } else {
                    try {
                        srcJson.add(field.name, myGson.toJsonTree(field[src]))
                    } catch (e: IllegalAccessException) {
                        Log.e(RESOURCE_SERIALIZER, EXCEPTION, e)
                    }
                }
            }
        }
        return srcJson
    }

    private val gson: Gson
        private get() {
            val gsonBuilder = GsonBuilder()
            return gsonBuilder
                    .excludeFieldsWithoutExposeAnnotation()
                    .create()
        }

    private fun serializeField(src: Resource, context: JsonSerializationContext): JsonElement {
        return if (src.uuid != null) {
            JsonPrimitive(src.uuid)
        } else {
            context.serialize(src)
        }
    }

    private fun isResourceCollection(collection: Collection<*>): Boolean {
        val array = collection.toTypedArray()
        return Resource::class.java.isAssignableFrom(array[0]!!.javaClass)
    }

    companion object {
        const val RESOURCE_SERIALIZER = "RESOURCE_SERIALIZER"
        const val EXCEPTION = "exception"
    }
}
