package com.example.expensetracker.utils;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.expensetracker.api.UserApi;
import com.example.expensetracker.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.rxjava3.subjects.BehaviorSubject;
import timber.log.Timber;

public class AuthenticationFirebase {


    public static BehaviorSubject<String> loginRequest(final String email, final String password) {
        final BehaviorSubject<String> behaviorSubject = BehaviorSubject.create();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(BaseApp.context, "Authentication succeeded", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = auth.getCurrentUser();

                        Task<GetTokenResult> tokenResultTask = user.getIdToken(true);
                        tokenResultTask.addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                String token = task1.getResult().getToken();
                                behaviorSubject.onNext(token);
                            } else {
                                Toast.makeText(BaseApp.context, task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                behaviorSubject.onNext("error");
                            }
                        });
                    } else {
                        Toast.makeText(BaseApp.context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        behaviorSubject.onNext("error");
                    }
                });

        return behaviorSubject;
    }

    public static BehaviorSubject<Boolean> signUpRequest(final String fullName, final String email,
                                                         final String password, final String phoneNumber,
                                                         final String avatarUri) {
        final BehaviorSubject<Boolean> behaviorSubject = BehaviorSubject.create();

        String url = BaseApp.serverUrl + "/users/saveUser";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fullName", fullName);
            jsonObject.put("email", email);
            jsonObject.put("phoneNumber", phoneNumber);
            jsonObject.put("avatarUri", avatarUri);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                (Response.Listener<JSONObject>) response -> {

                },
                (Response.ErrorListener) error -> {
                    behaviorSubject.onError(error);
                });

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        RequestQueueHelper.getRequestQueueHelperInstance(BaseApp.context).addToRequestQueue(jsonObjectRequest);
                        Toast.makeText(BaseApp.context, "User registered successfully", Toast.LENGTH_SHORT).show();
                        behaviorSubject.onNext(true);
                    } else {
                        Toast.makeText(BaseApp.context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        behaviorSubject.onNext(false);
                    }
                });

        return behaviorSubject;
    }

    public static BehaviorSubject<Boolean> resetPassword(final String email) {
        final BehaviorSubject<Boolean> behaviorSubject = BehaviorSubject.create();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(BaseApp.context, "Email sent!", Toast.LENGTH_SHORT).show();
                        behaviorSubject.onNext(true);
                    } else {
                        Toast.makeText(BaseApp.context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        behaviorSubject.onNext(false);
                    }
                });

        return behaviorSubject;
    }
}
