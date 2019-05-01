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


import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.dao.EncounterDAO;
import org.openmrs.mobile.models.EncounterType;
import org.openmrs.mobile.models.FormResource;
import org.openmrs.mobile.utilities.FormService;
import org.openmrs.mobile.utilities.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class FormListPresenter extends BasePresenter implements FormListContract.Presenter {

    private static String[] formsStringArray = null;

    private FormListContract.View view;
    private Long patientId;
    private List<FormResource> formResourceList;
    private EncounterDAO encounterDAO;

    public FormListPresenter(FormListContract.View view, long patientId) {
        this.view = view;
        this.view.setPresenter(this);
        this.patientId = patientId;
        this.encounterDAO = new EncounterDAO();
    }

    public FormListPresenter(FormListContract.View view, long patientId, EncounterDAO encounterDAO) {
        this.view = view;
        this.view.setPresenter(this);
        this.patientId = patientId;
        this.encounterDAO = encounterDAO;
    }

    @Override
    public void subscribe() {
        loadFormResourceList();
    }

    @Override
    public void loadFormResourceList() {
        formResourceList = new ArrayList<>();
        List<FormResource> allFormResourcesList = FormService.getFormResourceList();
        for (FormResource formResource : allFormResourcesList) {
            List<FormResource> valueRef = formResource.getResourceList();
            String valueRefString = null;

            for (FormResource resource : valueRef) {
                if (resource.getName().equals("json")) {
                    valueRefString = resource.getValueReference();
                }
            }
            if (!StringUtils.isBlank(valueRefString)) {
                formResourceList.add(formResource);
            } else {
                if (view.formCreate(formResource.getUuid(), formResource.getName().toLowerCase())) {
                    formResourceList.add(formResource);
                }
            }

        }

        int size = formResourceList.size();
        formsStringArray = new String[size];
        for (int i = 0; i < size; i++) {
            formsStringArray[i] = formResourceList.get(i).getName();
        }
        view.showFormList(formsStringArray);
    }

    @Override
    public void listItemClicked(int position, String formName) {
        List<FormResource> valueRef = formResourceList.get(position).getResourceList();
        String valueRefString = null;
        for (FormResource resource : valueRef) {
            if (resource.getName().equals("json"))
                valueRefString = resource.getValueReference();
        }

        EncounterType encType = encounterDAO.getEncounterTypeByFormName(formsStringArray[position]);
        if (encType != null) {
            String encounterType = encType.getUuid();
            view.startFormDisplayActivity(formName, patientId, valueRefString, encounterType);
        } else {
            view.showError("There is no encounter type called " + formName);
        }
    }

}
