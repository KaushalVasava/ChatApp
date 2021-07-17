package com.kmv.chatapp.Network;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class Network extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}
