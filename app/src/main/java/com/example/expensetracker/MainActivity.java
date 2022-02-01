package com.example.expensetracker;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.expensetracker.api.PushNotificationApi;
import com.example.expensetracker.databinding.ActivityMainBinding;
import com.example.expensetracker.utils.SharedPreferencesUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_trips, R.id.navigation_statistics, R.id.navigation_profile, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        retrieveFCMToken();
    }

    public void retrieveFCMToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener((OnCompleteListener<String>) task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM_TOKEN", "getInstanceId failed", task.getException());
                        return;
                    }

                    // Get new Instance ID token
                    String token = task.getResult();
                    PushNotificationApi.updateNotificationToken(token, SharedPreferencesUtils.getUserId());

                    if (!SharedPreferencesUtils.getNotifToken().equals(token)) {
                        SharedPreferencesUtils.setNotificationToken(token);
                    }

                    // Log and toast
                    String msg = getString(R.string.msg_token_fmt, token);
                    Log.d("FCM_TOKEN updated", msg);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                });

    }

}