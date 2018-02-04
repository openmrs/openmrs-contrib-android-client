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
package org.openmrs.mobile.activities.formaddeditservice;

import android.text.Editable;

import org.openmrs.mobile.activities.BasePresenterContract;
import org.openmrs.mobile.activities.BaseView;


public interface FormAddEditServiceContract {
    interface View extends BaseView<Presenter> {

        void endActivity();
    }

    interface Presenter extends BasePresenterContract {


        void deleteServiceTypes(String Uuid);

        void editServiceTypes(Editable mServiceNameText, Editable mServiceDiscriptionText, Editable mServiceDurationText, String uuid);

        void addServiceTypes(Editable mServiceNameText, Editable mServiceDiscriptionText, Editable mServiceDurationText);
    }
}
