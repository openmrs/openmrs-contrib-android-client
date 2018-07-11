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
package org.openmrs.mobile.activities.formappointmentrequest;

import android.widget.TextView;

import org.openmrs.mobile.activities.BasePresenterContract;
import org.openmrs.mobile.activities.BaseView;
import org.openmrs.mobile.models.servicestypemodel.Services;
import org.openmrs.mobile.models.timeblocks.Result;

import java.util.List;


public interface FormAppointmentRequestContract {
    interface View extends BaseView<Presenter> {

        void getCalendar(TextView datetime);

        void endActivity();

        void fillServiceTypeDropDown(List<Services> services);

        void createDialog(List<Result> blocks, String service);

        void updateAdapter(List<Result> blocks);
    }

    interface Presenter extends BasePresenterContract {


        void getServiceTypes();


         void deleteAppointmentRequest(String uuid);

        void getTimeBlocks(String service);
    }
}
