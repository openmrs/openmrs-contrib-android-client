package org.openmrs.mobile.net;

import org.openmrs.mobile.activities.SettingsActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.listeners.offline.JsonObjectOfflineRequestListener;
import org.openmrs.mobile.listeners.offline.MultiPartOfflineRequestListener;
import org.openmrs.mobile.models.OfflineRequest;
import org.openmrs.mobile.net.volley.wrappers.JsonObjectRequestWrapper;
import org.openmrs.mobile.net.volley.wrappers.MultiPartRequest;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.io.File;
import java.util.List;

public class OfflineRequestManager extends BaseManager {

    private final SettingsActivity mActivityCaller;

    public OfflineRequestManager(SettingsActivity activityCaller) {
        super();
        mActivityCaller = activityCaller;
    }

    public void sendAllOldRequestOneByOne() {
        List<OfflineRequest> offlineRequestList = OpenMRS.getInstance().getOfflineRequestQueue();
        if (offlineRequestList.size() > 0) {
            sendOldRequest(offlineRequestList.get(0), 0, true);
        }
    }

    public void sendOldRequest(final OfflineRequest offlineRequest, final int id, final boolean sendNext) {
        OfflineRequest requestData = offlineRequest;

        if (requestData.getWrapperName() != null &&
                requestData.getWrapperName().equals(MultiPartRequest.class.getName())) {
            MultiPartOfflineRequestListener mporListener = new MultiPartOfflineRequestListener(offlineRequest, sendNext, this);
            MultiPartRequest multipartRequest = new MultiPartRequest(requestData.getUrl(),
                    mporListener, mporListener, new File(requestData.getRequest()), requestData.getObjectUUID(), false) {
            };
            mOpenMRS.addToRequestQueue(multipartRequest);
        } else {
            if (ApplicationConstants.OfflineRequests.INACTIVATE_VISIT.equals(offlineRequest.getActionName()) && offlineRequest.getUrl() == null) {
                requestData = prepareInactivateVisitRequest(offlineRequest);
            }
            JsonObjectOfflineRequestListener joorListener = new JsonObjectOfflineRequestListener(offlineRequest, sendNext, this);

            JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(requestData.getMethod(), requestData.getUrl(),
                    requestData.getJSONRequest(), joorListener, joorListener, false);

            mOpenMRS.addToRequestQueue(jsObjRequest);
        }
    }

    public void removeFromQueue(OfflineRequest offlineRequest, boolean sendNext) {
        List<OfflineRequest> offlineRequestList = OpenMRS.getInstance().getOfflineRequestQueue();
        offlineRequestList.remove(offlineRequest);
        OpenMRS.getInstance().setOfflineRequestQueue(offlineRequestList);
        mActivityCaller.setListView();
        if (sendNext) {
            sendAllOldRequestOneByOne();
        }
    }

    private OfflineRequest prepareInactivateVisitRequest(OfflineRequest offlineRequest) {
        String visitUUID = new VisitDAO().getVisitsByID(offlineRequest.getObjectID()).getUuid();
        String visitURL = mOpenMRS.getServerUrl() + ApplicationConstants.API.REST_ENDPOINT + ApplicationConstants.API.VISIT_DETAILS + File.separator + visitUUID;
        offlineRequest.setUrl(visitURL);

        return offlineRequest;
    }
}
