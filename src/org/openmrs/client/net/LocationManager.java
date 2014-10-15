package org.openmrs.client.net;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.client.activities.LoginActivity;
import org.openmrs.client.models.Location;
import org.openmrs.client.models.mappers.LocationMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.openmrs.client.utilities.ApplicationConstants.API;

public class LocationManager extends BaseManager {
    private static final String LOCATION_QUERY = "location?tag=Login%20Location&v=full";

    public LocationManager(Context context) {
        super(context);
    }

    public void getAvailableLocation() {
        RequestQueue queue = Volley.newRequestQueue(mContext);

        mOpenMRS.setServerUrl("http://devtest01.openmrs.org/openmrs"); //ToDO: Remove when url dialog show before loginActivity

        String locationURL = mOpenMRS.getServerUrl() + API.COMMON_PART + LOCATION_QUERY;
        logger.d("Sending request to : " + locationURL);

        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.GET,
                locationURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                logger.d(response.toString());

                try {
                    JSONArray locationResultJSON = response.getJSONArray(RESULTS_KEY);
                    List<Location> locationList = new ArrayList<Location>();
                    if (locationResultJSON.length() > 0) {
                        for (int i = 0; i < locationResultJSON.length(); i++) {
                            locationList.add(LocationMapper.map(locationResultJSON.getJSONObject(i)));
                        }
                    }
                    ((LoginActivity) mContext).setLocationList(locationList);
                } catch (JSONException e) {
                    logger.d(e.toString());
                }
            }
        }
                , new GeneralErrorListenerImpl(mContext)) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        queue.add(jsObjRequest);
    }
}
