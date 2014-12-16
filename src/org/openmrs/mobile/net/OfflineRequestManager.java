package org.openmrs.mobile.net;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.openmrs.mobile.activities.SettingsActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.models.OfflineRequest;

import java.util.List;

public class OfflineRequestManager extends BaseManager {

    public OfflineRequestManager(Context context) {
        super(context);
    }

    public void sendAllOldRequestOneByOne() {
        List<OfflineRequest> offlineRequestList = OpenMRS.getInstance().getOfflineRequestQueue();
        if (offlineRequestList.size() > 0) {
            sendOldRequest(offlineRequestList.get(0), true);
        } else {
            ((SettingsActivity) mContext).setListView();
        }
    }

    public void sendOldRequest(final OfflineRequest offlineRequest, final boolean sendNext) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(offlineRequest.getMethod(), offlineRequest.getUrl(), offlineRequest.getJSONRequest(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                logger.d(response.toString());
                //remove from queue if ok
                List<OfflineRequest> offlineRequestList = OpenMRS.getInstance().getOfflineRequestQueue();
                offlineRequestList.remove(offlineRequest);
                OpenMRS.getInstance().setOfflineRequestQueue(offlineRequestList);
                if (sendNext) {
                    sendAllOldRequestOneByOne();
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
