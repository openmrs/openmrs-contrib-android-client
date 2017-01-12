/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.mobile.activities.formdisplay;

import android.app.Fragment;
import android.widget.LinearLayout;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.activities.BaseView;
import org.openmrs.mobile.models.Question;
import org.openmrs.mobile.utilities.InputField;
import org.openmrs.mobile.utilities.SelectOneField;

import java.util.List;

public interface FormDisplayContract {

    interface View extends BaseView<Presenter> {

        interface MainView extends View {
            void quitFormEntry();
            void enableSubmitButton(boolean enabled);
            void showToast(String errorMessage);
        }

        interface PageView extends View {
            void attachSectionToView(LinearLayout linearLayout);
            void attachQuestionToSection(LinearLayout section, LinearLayout question);
            void createAndAttachNumericQuestionEditText(Question question, LinearLayout sectionLinearLayout);
            void createAndAttachSelectQuestionDropdown(Question question, LinearLayout sectionLinearLayout);
            void createAndAttachSelectQuestionRadioButton(Question question, LinearLayout sectionLinearLayout);
            LinearLayout createQuestionGroupLayout(String questionLabel);
            LinearLayout createSectionLayout(String sectionLabel);
            List<SelectOneField> getSelectOneFields();
            List<InputField> getInputFields();
            void setInputFields(List<InputField> inputFields);
            void setSelectOneFields(List<SelectOneField> selectOneFields);
        }

    }

    interface Presenter extends BasePresenter {

        interface MainPresenter extends Presenter {
            void createEncounter();
            void addFragment(Fragment fragment);
        }

        interface PagePresenter extends Presenter{}

    }

}
