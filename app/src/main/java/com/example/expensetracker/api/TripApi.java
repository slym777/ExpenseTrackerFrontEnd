package com.example.expensetracker.api;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.expensetracker.model.Trip;
import com.example.expensetracker.utils.BaseApp;
import com.example.expensetracker.utils.RequestQueueHelper;
import com.google.gson.Gson;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.subjects.BehaviorSubject;
import timber.log.Timber;

public class TripApi {

    public static BehaviorSubject<List<Trip>> getAllTrips() {
        String url = BaseApp.serverUrl + "/trips/allTrips";

        final BehaviorSubject<List<Trip>> behaviorSubject = BehaviorSubject.create();

        JsonArrayRequest jsonArrayRequest  = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            Gson gson = new Gson();
            List<Trip> trips = new ArrayList<>();
            try {
                for (int index = 0; index < response.length(); index++) {
                    Trip trip = gson.fromJson(response.getJSONObject(index).toString(), Trip.class);
                    trips.add(trip);
                }
                behaviorSubject.onNext(trips);
                Timber.d("Trips retrieved successfully");

            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                behaviorSubject.onError(error);
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=UTF-8");
//                params.put("Authorization", "Bearer " + SharedPreferencesUtils.retrieveTokenFromSharedPref());
//                Timber.d("retrieved token is " + SharedPreferencesUtils.retrieveTokenFromSharedPref());
                return params;
            }
        };

        RequestQueueHelper.getRequestQueueHelperInstance(BaseApp.context).addToRequestQueue(jsonArrayRequest);

        return behaviorSubject;
    }

//    public static BehaviorSubject<Boolean> createHub(Hub hub) {
//        String url = BaseApp.serverUrl + "/hubs/createHub";
//
//        final BehaviorSubject<Boolean> behaviorSubject = BehaviorSubject.create();
//
//        JsonObjectRequest jsonObjectRequest  = new JsonObjectRequest(Request.Method.POST, url, hub.toJson(), new Response.Listener<JSONObject>() {
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
//            public void onResponse(JSONObject response) {
//                behaviorSubject.onNext(true);
//            }
//        }, (Response.ErrorListener) behaviorSubject::onError) {
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("Content-Type", "application/json; charset=UTF-8");
//                params.put("Authorization", "Bearer " + SharedPreferencesUtils.retrieveTokenFromSharedPref());
//                Timber.d("retrieved token is " + SharedPreferencesUtils.retrieveTokenFromSharedPref());
//                return params;
//            }
//
//            @Override
//            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
//                try {
//                    String json = new String(
//                            response.data,
//                            "UTF-8"
//                    );
//                    if (json.length() == 0) {
//                        return Response.success(
//                                null,
//                                HttpHeaderParser.parseCacheHeaders(response)
//                        );
//                    } else {
//                        return super.parseNetworkResponse(response);
//                    }
//                } catch (UnsupportedEncodingException e) {
//                    return Response.error(new ParseError(e));
//                }
//            }
//        };
//
//        RequestQueueHelper.getRequestQueueHelperInstance(BaseApp.context).addToRequestQueue(jsonObjectRequest);
//
//        return behaviorSubject;
//    }


}