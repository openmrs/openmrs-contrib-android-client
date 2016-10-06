package org.openmrs.mobile.listeners.retrofit;

import org.openmrs.mobile.models.retrofit.VisitType;

public interface GetVisitTypeCallbackListener extends DefaultResponseCallbackListener{

    void onGetVisitTypeResponse(VisitType visitType);

}
