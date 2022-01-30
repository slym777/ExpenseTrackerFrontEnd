package com.example.expensetracker.utils;

import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.expensetracker.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class AuthenticationFirebase {


    public static BehaviorSubject<String> loginRequest(final String email, final String password) {
        final BehaviorSubject<String> behaviorSubject = BehaviorSubject.create();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();

                        Task<GetTokenResult> tokenResultTask = user.getIdToken(true);
                        tokenResultTask.addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                String token = task1.getResult().getToken();
                                behaviorSubject.onNext(token);
                            } else {
                                behaviorSubject.onError(task.getException());
                            }
                        });
                    } else {
                        behaviorSubject.onError(task.getException());
                    }
                });

        return behaviorSubject;
    }

    public static BehaviorSubject<Boolean> signUpRequest(User user, String password) {
        final BehaviorSubject<Boolean> behaviorSubject = BehaviorSubject.create();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(user.getEmail(), password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
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

    public static BehaviorSubject<Boolean> deleteUserAuth(String email) {
        final BehaviorSubject<Boolean> behaviorSubject = BehaviorSubject.create();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.delete().addOnCompleteListener( task -> {
            if (task.isSuccessful()) {
                behaviorSubject.onNext(true);
            } else {
                Toast.makeText(BaseApp.context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                behaviorSubject.onNext(false);
            }
        });

        return behaviorSubject;
    }
}
