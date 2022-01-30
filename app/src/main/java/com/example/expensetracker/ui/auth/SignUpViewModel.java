package com.example.expensetracker.ui.auth;

import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModel;

import com.example.expensetracker.api.UserApi;
import com.example.expensetracker.model.User;
import com.example.expensetracker.utils.AuthenticationFirebase;
import com.example.expensetracker.utils.BaseApp;

import java.util.LinkedList;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import timber.log.Timber;

public class SignUpViewModel extends ViewModel {
    public BehaviorSubject<Boolean> signUpState = BehaviorSubject.create();
    private final LinkedList<Disposable> disposableLinkedList = new LinkedList<>();


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
        User user = new User(fullName, email, phoneNumber, avatarUri);
        disposableLinkedList.add(AuthenticationFirebase.signUpRequest(user, password).subscribe(b -> {
            if (b) {
                disposableLinkedList.add(UserApi.saveUser(user).subscribe( bool -> {
                    signUpState.onNext(true);
                }, err -> {
                    AuthenticationFirebase.deleteUserAuth(email).subscribe(l -> {
                        Timber.d("Firebase User Auth successfully deleted");
                    }, e -> {
                        signUpState.onError(e);
                            });
                    signUpState.onError(err);
                }));
            }
        }, error -> signUpState.onError(error)));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCleared() {
        super.onCleared();
        disposableLinkedList.forEach(Disposable::dispose);
    }
}
