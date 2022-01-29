package com.example.expensetracker.api;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.expensetracker.model.Expense;
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

public class ExpenseApi {

    public static BehaviorSubject<List<Expense>> getExpensesByTripId(Long tripId, boolean isGroup) {
        String url = BaseApp.serverUrl + "/expenses/getTripExpenses/" + tripId + "/" + isGroup;

        final BehaviorSubject<List<Expense>> behaviorSubject = BehaviorSubject.create();

        JsonArrayRequest jsonArrayRequest  = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            Gson gson = new Gson();
            List<Expense> expenses = new ArrayList<>();
            try {
                for (int index = 0; index < response.length(); index++) {
                    Expense expense = gson.fromJson(response.getJSONObject(index).toString(), Expense.class);
                    expenses.add(expense);
                }
                behaviorSubject.onNext(expenses);
                Timber.d("Expenses retrieved successfully");

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

}