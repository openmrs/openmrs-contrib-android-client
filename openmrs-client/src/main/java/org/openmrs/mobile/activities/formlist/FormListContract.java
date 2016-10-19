package org.openmrs.mobile.activities.formlist;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.activities.BaseView;

public interface FormListContract {

    interface View extends BaseView<Presenter>{

        boolean isActive();

        void showFormList(String[] forms);

        void startFormDisplayActivity(String formName, Long patientId, String valueRefString, String encounterType);
    }

    interface Presenter extends BasePresenter{

        void loadFormResourceList();

        void listItemClicked(int position, String formName);
    }

}
