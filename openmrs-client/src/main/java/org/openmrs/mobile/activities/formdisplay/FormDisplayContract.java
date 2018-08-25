/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.mobile.activities.formdisplay;

import java.util.List;

import org.openmrs.mobile.activities.BasePresenterContract;
import org.openmrs.mobile.activities.BaseView;
import org.openmrs.mobile.models.Question;
import org.openmrs.mobile.utilities.InputField;
import org.openmrs.mobile.utilities.SelectOneField;

import android.widget.LinearLayout;

public interface FormDisplayContract {

    interface View {

        interface MainView extends BaseView<Presenter.MainPresenter> {

            void quitFormEntry();

            void enableSubmitButton(boolean enabled);

            void showToast(String errorMessage);
        }

        interface PageView extends BaseView<Presenter.PagePresenter> {

            void attachSectionToView(LinearLayout linearLayout);

            void attachQuestionToSection(LinearLayout section, LinearLayout question);

            void createAndAttachNumericQuestionEditText(Question question, LinearLayout sectionLinearLayout);

            void createAndAttachSelectQuestionDropdown(Question question, LinearLayout sectionLinearLayout);

            void createAndAttachSelectQuestionRadioButton(Question question, LinearLayout sectionLinearLayout);

            LinearLayout createQuestionGroupLayout(String questionLabel);

            LinearLayout createSectionLayout(String sectionLabel);

            List<SelectOneField> getSelectOneFields();

            void setSelectOneFields(List<SelectOneField> selectOneFields);

            List<InputField> getInputFields();

            void setInputFields(List<InputField> inputFields);
        }

    }

    interface Presenter {

        interface MainPresenter extends BasePresenterContract {

            void createEncounter();
        }

        interface PagePresenter extends BasePresenterContract {

        }

    }

}
