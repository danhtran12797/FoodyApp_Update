package com.danhtran12797.thd.foodyapp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.zing.zalo.zalosdk.oauth.ZaloSDKApplication;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ZaloSDKApplication.wrap(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_DEFAULT));
        }
    }
}
