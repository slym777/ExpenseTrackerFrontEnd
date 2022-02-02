package com.example.expensetracker.utils;

import android.widget.Toast;

import com.example.expensetracker.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

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
                                behaviorSubject.onNext("error");
                            }
                        });
                    } else {
                        behaviorSubject.onNext("error");
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

    public static BehaviorSubject<Boolean> updatePassword(String currentPassword, String newPassword) {
        final BehaviorSubject<Boolean> behaviorSubject = BehaviorSubject.create();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user.updatePassword(newPassword).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                behaviorSubject.onNext(true);
                            } else {
                                Toast.makeText(BaseApp.context, task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                behaviorSubject.onNext(false);
                            }
                        });
                    } else {
                        Toast.makeText(BaseApp.context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        behaviorSubject.onNext(false);
                    }
                });

        return behaviorSubject;
    }

    public static BehaviorSubject<Boolean> updateEmail(String email) {
        final BehaviorSubject<Boolean> behaviorSubject = BehaviorSubject.create();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.updateEmail(email).addOnCompleteListener(task -> {
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
