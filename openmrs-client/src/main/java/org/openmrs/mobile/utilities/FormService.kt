package org.openmrs.mobile.utilities

import com.google.gson.GsonBuilder
import org.openmrs.mobile.models.Form
import org.openmrs.mobile.models.FormResource
import org.openmrs.mobile.utilities.ActiveAndroid.query.Select
import org.openmrs.mobile.utilities.StringUtils.isBlank
import org.openmrs.mobile.utilities.StringUtils.unescapeJavaString
import java.lang.reflect.Modifier


object FormService {

    @JvmStatic
    fun getForm(valueReference: String?): Form {
        val unescapedValueReference = unescapeJavaString(valueReference!!)
        val builder = GsonBuilder()
        builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
        builder.excludeFieldsWithoutExposeAnnotation()
        val gson = builder.create()
        return gson.fromJson(unescapedValueReference, Form::class.java)
    }

    @JvmStatic
    fun getFormByUuid(uuid: String?): Form? {
        if (!isBlank(uuid)) {
            val formResource = Select()
                    .from(FormResource::class.java)
                    .where("uuid = ?", uuid)
                    .executeSingle<FormResource>()
            if (formResource != null) {
                val resourceList = formResource.resourceList
                for (resource in resourceList!!) {
                    if ("json" == resource.name) {
                        val valueRefString = resource.valueReference
                        val form = getForm(valueRefString)
                        form.valueReference = valueRefString
                        form.name = formResource.name
                        return form
                    }
                }
            }
        }
        return null
    }

    @JvmStatic
    fun getFormResourceByName(name: String?): FormResource {
        return Select()
                .from(FormResource::class.java)
                .where("name = ?", name)
                .executeSingle()
    }

    @JvmStatic
    fun getFormResourceList(): List<FormResource> {
        return Select()
                .from(FormResource::class.java)
                .execute()
    }
}
