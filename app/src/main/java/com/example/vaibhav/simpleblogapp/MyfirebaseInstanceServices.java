package com.example.vaibhav.simpleblogapp;

import android.content.Intent;
import android.util.Log;

import com.example.vaibhav.simpleblogapp.FCMthings.SharedPrefManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by vaibhav on 23/6/17.
 */

public class MyfirebaseInstanceServices extends FirebaseInstanceIdService {

    public static final String TOKEN_BROADCAST = "myfctokenBroadcast";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("FirabaseTOKEN", " " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        storeToken(refreshedToken);
        getApplicationContext().sendBroadcast(new Intent(TOKEN_BROADCAST));
    }

    private void storeToken(String token) {
        SharedPrefManager.getInstance(getApplicationContext()).storeToken(token);
    }
}
