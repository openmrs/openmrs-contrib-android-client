/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.mobile.utilities;


import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.openmrs.mobile.models.Form;
import org.openmrs.mobile.models.FormResource;

import java.lang.reflect.Modifier;
import java.util.List;

public class FormService {

    public static Form getForm(String valueReference) {
        String unescapedValueReference = StringUtils.unescapeJavaString(valueReference);

        GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
        builder.excludeFieldsWithoutExposeAnnotation();
        Gson gson = builder.create();

        return gson.fromJson(unescapedValueReference, Form.class);
    }

    public static Form getFormByUuid(String uuid) {
        if(!StringUtils.isBlank(uuid)){
            FormResource formResource = new Select()
                    .from(FormResource.class)
                    .where("uuid = ?", uuid)
                    .executeSingle();
            if(formResource != null){
                List<FormResource> resourceList = formResource.getResourceList();
                for (FormResource resource : resourceList) {
                    if("json".equals(resource.getName())){
                        String valueRefString = resource.getValueReference();
                        Form form = FormService.getForm(valueRefString);
                        form.setValueReference(valueRefString);
                        form.setName(formResource.getName());
                        return form;
                    }
                }
            }
        }
        return null;
    }

    public static FormResource getFormResourceByName(String name) {
        return new Select()
                .from(FormResource.class)
                .where("name = ?", name)
                .executeSingle();
    }

    public static List<FormResource> getFormResourceList(){
        return new Select()
                .from(FormResource.class)
                .execute();
    }

}
