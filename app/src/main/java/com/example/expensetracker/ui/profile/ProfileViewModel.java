package com.example.expensetracker.ui.profile;

import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.expensetracker.api.UserApi;
import com.example.expensetracker.model.User;
import com.example.expensetracker.utils.AuthenticationFirebase;
import com.example.expensetracker.utils.BaseApp;
import com.example.expensetracker.utils.SharedPreferencesUtils;

import java.util.LinkedList;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import timber.log.Timber;

public class ProfileViewModel extends ViewModel {
    public String fullName;
    public String newEmail;
    public String newPhoneNumber;
    public String newAvatarUri;

    public void buildUser(User user) {
        this.fullName = user.getFullName();
        this.newEmail = user.getEmail();
        this.newPhoneNumber = user.getPhoneNumber();
        this.newAvatarUri = user.getAvatarUri();
    }

    public void setFullName(String firstName, String lastName){
        this.fullName = firstName + " " + lastName;
    }

    public void setNewEmail(String email){
        this.newEmail = email;
    }


    public void setNewPhoneNumber(String phoneNumber) {
        this.newPhoneNumber = phoneNumber;
    }

    public void setNewAvatarUri(Uri uri){
        this.newAvatarUri = uri.toString();
    }

    public void updateProfile() {
        Long id = SharedPreferencesUtils.getUserId();
        User user = new User(id, fullName, newEmail, newPhoneNumber, newAvatarUri);

        if (!TextUtils.isEmpty(newEmail)) {
            AuthenticationFirebase.updateEmail(newEmail).subscribe(b -> {
                if (b) {
                    UserApi.updateUser(user).subscribe( u -> {
                        if (u != null) {
                            SharedPreferencesUtils.setProfileDetails(
                                    user.getId(),
                                    user.getFullName(),
                                    user.getEmail(),
                                    user.getPhoneNumber(),
                                    user.getAvatarUri(),
                                    SharedPreferencesUtils.getIdToken()
                            );

                            Toast.makeText(BaseApp.context, "User updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BaseApp.context, "Could not update user", Toast.LENGTH_SHORT).show();
                        }
                    }, err -> {
                        Toast.makeText(BaseApp.context, "An error occurred while trying to update user", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    Toast.makeText(BaseApp.context, "Could not update user", Toast.LENGTH_SHORT).show();
                }
            }, err -> {
                Toast.makeText(BaseApp.context, "An error occurred while trying to update user", Toast.LENGTH_SHORT).show();
            });
        }
    }
}