package com.example.expensetracker.api;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.expensetracker.model.User;
import com.example.expensetracker.utils.BaseApp;
import com.example.expensetracker.utils.RequestQueueHelper;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.subjects.BehaviorSubject;
import timber.log.Timber;

public class UserApi {
    public static BehaviorSubject<List<User>> getListOfUsers() {
        String url = BaseApp.serverUrl + "/users/allUsers";

        final BehaviorSubject<List<User>> behaviorSubject = BehaviorSubject.create();

        JsonArrayRequest jsonArrayRequest  = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(JSONArray response) {
                Gson gson = new Gson();
                List<User> users = new ArrayList<>();
                try {
                    for (int index = 0; index < response.length(); index++) {
                        User User = gson.fromJson(response.getJSONObject(index).toString(), User.class);
                        users.add(User);
                    }
                    behaviorSubject.onNext(users);
                    Timber.d("Users retrieved successfully");

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                behaviorSubject.onError(error);
            }
        }) {

//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("Content-Type", "application/json; charset=UTF-8");
//                params.put("Authorization", "Bearer " + SharedPreferencesUtils.retrieveTokenFromSharedPref());
//                Timber.d("retrieved token is " + SharedPreferencesUtils.retrieveTokenFromSharedPref());
//                return params;
//            }
        };

        RequestQueueHelper.getRequestQueueHelperInstance(BaseApp.context).addToRequestQueue(jsonArrayRequest);

        return behaviorSubject;
    }
}
