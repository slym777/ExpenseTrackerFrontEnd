package com.example.expensetracker.ui.auth;

import android.text.TextUtils;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;

import com.example.expensetracker.api.UserApi;
import com.example.expensetracker.model.User;
import com.example.expensetracker.utils.AuthenticationFirebase;
import com.example.expensetracker.utils.BaseApp;
import com.example.expensetracker.utils.SharedPreferencesUtils;

import org.w3c.dom.Text;

import io.reactivex.rxjava3.subjects.BehaviorSubject;
import timber.log.Timber;

public class LoginViewModel extends ViewModel {
    public BehaviorSubject<Boolean> loginState = BehaviorSubject.create();

    public void login(String email, String password) {
        AuthenticationFirebase.loginRequest(email, password).subscribe( token -> {
            if (TextUtils.equals(token, "error")) {
                loginState.onNext(false);
            } else {
                SharedPreferencesUtils.setToken(token, true);

                UserApi.getUserByUserEmail(email, token).subscribe( user -> {
                    SharedPreferencesUtils.setProfileDetails(
                            user.getId(),
                            user.getFullName(),
                            user.getEmail(),
                            user.getPhoneNumber(),
                            user.getAvatarUri(),
                            SharedPreferencesUtils.getIdToken()
                    );
                    loginState.onNext(true);
                }, err -> loginState.onError(new Throwable("Unable to reach login service")));
            }

        }, err -> loginState.onError(err));
    }
}
