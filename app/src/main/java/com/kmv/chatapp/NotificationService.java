package com.kmv.chatapp;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kmv.chatapp.Model.User;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

class NotificationService extends FirebaseMessagingService {

    User user = new User();
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }
    private  void UpdateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        Map<String,Object> map =new HashMap<>();
        map.put("token",token);
        reference.updateChildren(map);
    }
}
