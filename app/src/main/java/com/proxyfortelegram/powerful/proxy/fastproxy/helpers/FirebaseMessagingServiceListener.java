package com.proxyfortelegram.powerful.proxy.fastproxy.helpers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class FirebaseMessagingServiceListener extends FirebaseMessagingService {

    private Context mContext;
    private static String TAG = "FCMListener";
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d("onRemoteMessage", String.valueOf(remoteMessage.getPriority()));
        if (remoteMessage.getData().size() > 0) {
            PreferencesManager pm = PreferencesManager.newInstance(mContext);
            if (remoteMessage.getData().containsKey("backend_url")) {
                pm.setBackendUrl(
                        remoteMessage.getData().get("backend_url")
                );
            }
        }
    }
}
