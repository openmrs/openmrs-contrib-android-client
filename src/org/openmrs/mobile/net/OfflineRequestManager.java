package org.openmrs.mobile.net;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import org.openmrs.mobile.activities.SettingsActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.models.OfflineRequest;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.mappers.VisitMapper;
import org.openmrs.mobile.net.volley.wrappers.MultiPartRequest;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.io.File;
import java.util.List;

public class OfflineRequestManager extends BaseManager {

    public OfflineRequestManager(Context context) {
        super(context);
    }

    public void sendAllOldRequestOneByOne() {
        List<OfflineRequest> offlineRequestList = OpenMRS.getInstance().getOfflineRequestQueue();
        if (offlineRequestList.size() > 0) {
            sendOldRequest(offlineRequestList.get(0), 0, true);
        }
    }

    public void sendOldRequest(final OfflineRequest offlineRequest, final int id, final boolean sendNext) {
        RequestQueue queue = Volley.newRequestQueue(mContext);

        OfflineRequest requestData = offlineRequest;

        if (requestData.getWrapperName() != null &&
                requestData.getWrapperName().equals(MultiPartRequest.class.getName())) {
            MultiPartRequest multipartRequest = new MultiPartRequest(requestData.getUrl(),
                    new GeneralErrorListenerImpl(mContext),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            logger.d(response.toString());
                            //remove from queue if ok
                            List<OfflineRequest> offlineRequestList = OpenMRS.getInstance().getOfflineRequestQueue();
                            offlineRequestList.remove(id);
                            OpenMRS.getInstance().setOfflineRequestQueue(offlineRequestList);
                            ((SettingsActivity) mContext).setListView();
                            if (sendNext) {
                                sendAllOldRequestOneByOne();
                            }
                        }
                    }, new File(requestData.getRequest()), requestData.getObjectUUID()) {
            };

            queue.add(multipartRequest);
        } else {
            if (ApplicationConstants.OfflineRequests.INACTIVATE_VISIT.equals(offlineRequest.getActionName()) && offlineRequest.getUrl() == null) {
                requestData = prepareInactivateVisitRequest(offlineRequest);
            }

            JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(requestData.getMethod(), requestData.getUrl(), requestData.getJSONRequest(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(final JSONObject response) {
                    logger.d(response.toString());
                    //remove from queue if ok
                    boolean updateSuccessful = true;

                    if (ApplicationConstants.OfflineRequests.START_VISIT.equals(offlineRequest.getActionName())) {
                        try {
                            Visit updatedVisit = VisitMapper.map(response);
                            Visit visit = new VisitDAO().getVisitsByID(offlineRequest.getObjectID());
                            visit.setUuid(updatedVisit.getUuid());
                            new VisitDAO().updateVisit(visit, visit.getId(), visit.getPatientID());
                        } catch (JSONException e) {
                            logger.d(e.toString());
                            updateSuccessful = false;
                        }
                    }

                    if (updateSuccessful) {
                        List<OfflineRequest> offlineRequestList = OpenMRS.getInstance().getOfflineRequestQueue();
                        offlineRequestList.remove(id);
                        OpenMRS.getInstance().setOfflineRequestQueue(offlineRequestList);
                        ((SettingsActivity) mContext).setListView();
                        if (sendNext) {
                            sendAllOldRequestOneByOne();
                        }
                    }
                }
            }
                    , new GeneralErrorListenerImpl(mContext) {

                @Override
                public void onErrorResponse(VolleyError error) {
                    super.onErrorResponse(error);
                    //don't remove from queue if error occurred
                }
            }
            );

            queue.add(jsObjRequest);
        }
    }

    private OfflineRequest prepareInactivateVisitRequest(OfflineRequest offlineRequest) {
        String visitUUID = new VisitDAO().getVisitsByID(offlineRequest.getObjectID()).getUuid();
        String visitURL = mOpenMRS.getServerUrl() + ApplicationConstants.API.REST_ENDPOINT + ApplicationConstants.API.VISIT_DETAILS + File.separator + visitUUID;
        offlineRequest.setUrl(visitURL);

        return offlineRequest;
    }
}
