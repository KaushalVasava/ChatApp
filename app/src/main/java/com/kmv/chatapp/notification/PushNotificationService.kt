package com.kmv.chatapp.notification

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService


class PushNotificationService: FirebaseMessagingService() {
    private val TAG = "mFirebaseIIDService"
    private val SUBSCRIBE_TO = "userABC"

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)

        val token = FirebaseInstanceId.getInstance().token

        FirebaseMessaging.getInstance().subscribeToTopic(SUBSCRIBE_TO)
        Log.i(TAG, "onTokenRefresh completed with token: $token")
    }

}