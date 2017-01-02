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

package org.openmrs.mobile.activities.formlist;

import com.activeandroid.query.Select;

import org.openmrs.mobile.models.EncounterType;
import org.openmrs.mobile.models.FormResource;

import java.util.Iterator;
import java.util.List;

import static org.openmrs.mobile.utilities.FormService.getFormResourceList;

public class FormListPresenter implements FormListContract.Presenter {

    private static String[] FORMS = null;

    private FormListContract.View view;
    private Long patientId;
    private List<FormResource> formResourceList;

    public static EncounterType getEncounterType(String formname) {
        return new Select()
                .from(EncounterType.class)
                .where("display = ?", formname)
                .executeSingle();
    }

    public FormListPresenter(FormListContract.View view, long patientId) {
        this.view = view;
        this.patientId = patientId;
        view.setPresenter(this);
    }

    @Override
    public void start() {
        loadFormResourceList();
    }

    @Override
    public void loadFormResourceList() {
        formResourceList =getFormResourceList();

        Iterator<FormResource> iterator= formResourceList.iterator();

        while (iterator.hasNext())
        {
            FormResource formResource = iterator.next();
            List<FormResource> valueRef = formResource.getResourceList();
            String valueRefString=null;
            for(FormResource resource:valueRef)
            {
                if(resource.getName().equals("json"))
                    valueRefString = resource.getValueReference();
            }
            if(valueRefString==null) {
                iterator.remove();
            }
        }

        int size= formResourceList.size();
        FORMS=new String [size];
        for (int i=0;i<size;i++)
        {
            FORMS[i]= formResourceList.get(i).getName();
        }
        view.showFormList(FORMS);
    }

    @Override
    public void listItemClicked(int position, String formName) {
        List<FormResource> valueRef = formResourceList.get(position).getResourceList();
        String valueRefString = null;
        for (FormResource resource : valueRef) {
            if (resource.getName().equals("json"))
                valueRefString = resource.getValueReference();
        }

        EncounterType encType = getEncounterType(FORMS[position]);
        if (encType != null) {
            String encounterType = encType.getUuid();
            view.startFormDisplayActivity(formName, patientId, valueRefString, encounterType);
        } else {
            view.showError("There is no encounter type called " + formName);
        }
    }

}
