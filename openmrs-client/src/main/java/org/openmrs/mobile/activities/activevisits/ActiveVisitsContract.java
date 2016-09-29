package org.openmrs.mobile.activities.activevisits;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.activities.BaseView;
import org.openmrs.mobile.models.Visit;

import java.util.List;

public interface ActiveVisitsContract {

    interface View extends BaseView<Presenter> {

        boolean isActive();

        void updateListVisibility(List<Visit> visitList);

        void setAdapterFiltering(boolean filtering);

        void setEmptyListText(int stringId);

        void setEmptyListText(int stringId, String query);
    }

    interface Presenter extends BasePresenter{

        void updateVisitsInDatabaseList();

        void updateVisitsInDatabaseList(String query);

    }
}
