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

package org.openmrs.mobile.activities.activevisits;

import org.openmrs.mobile.activities.BasePresenterContract;
import org.openmrs.mobile.activities.BaseView;
import org.openmrs.mobile.models.Visit;

import java.util.List;

public interface ActiveVisitsContract {

    interface View extends BaseView<Presenter> {

        void updateListVisibility(List<Visit> visitList);

        void setEmptyListText(int stringId);

        void setEmptyListText(int stringId, String query);
    }

    interface Presenter extends BasePresenterContract{

        void updateVisitsInDatabaseList();

        void updateVisitsInDatabaseList(String query);

    }
}
