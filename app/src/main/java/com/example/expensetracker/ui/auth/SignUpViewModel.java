package com.example.expensetracker.ui.auth;

import android.net.Uri;

import androidx.lifecycle.ViewModel;

import com.example.expensetracker.utils.AuthenticationFirebase;

import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class SignUpViewModel extends ViewModel {
    public BehaviorSubject<Boolean> signUpState = BehaviorSubject.create();

    public String fullName;
    public String email;
    public String password;
    public String phoneNumber;
    public String avatarUri;

    public void saveUserDetails(String firstName, String lastName){
        this.fullName = firstName + " " + lastName;
    }

    public void saveDownloadUri(Uri uri){
        avatarUri = uri.toString();
    }

    public void signUp(){
        AuthenticationFirebase.signUpRequest(fullName, email, password, phoneNumber, avatarUri).subscribe(b -> {
            if (b) {
                signUpState.onNext(true);
            }
        }, error -> signUpState.onError(error));
    }
}
