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

import org.openmrs.mobile.activities.BasePresenterContract;
import org.openmrs.mobile.activities.BaseView;

public interface FormListContract {

    interface View extends BaseView<Presenter>{

        void showFormList(String[] forms);

        void startFormDisplayActivity(String formName, Long patientId, String valueRefString, String encounterType);

        void showError(String message);

        Boolean formCreate(String uuid,String formName);
    }

    interface Presenter extends BasePresenterContract {

        void loadFormResourceList();

        void listItemClicked(int position, String formName);
    }

}
