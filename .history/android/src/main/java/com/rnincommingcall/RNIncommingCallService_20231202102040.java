package com.rnincommingcall;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.react.HeadlessJsTaskService;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.jstasks.HeadlessJsTaskConfig;
import com.facebook.react.jstasks.HeadlessJsTaskRetryPolicy;
import com.facebook.react.jstasks.LinearCountingRetryPolicy;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import javax.annotation.Nullable;

public class RNIncommingCallService extends HeadlessJsTaskService {
  private static final String LOG_TAG = MyHeadlessJsTaskService.class.getSimpleName();

   @SuppressLint("WrongConstant")
    public void onCreate() {
      super.onCreate();
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        Context mContext = this.getApplicationContext();
        String CHANNEL_ID = "Background job";

        int appIcon = getApplicationInfo().icon;

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_LOW);
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
        Notification notification =
                new Notification.Builder(mContext, CHANNEL_ID)
                        .setContentTitle("Running background job")
                        .setContentText(mContext.getPackageName())
                        .setSmallIcon(appIcon)
                        .build();
        startForeground(1, notification);
      }
    }

  @Override
  protected @Nullable
  HeadlessJsTaskConfig getTaskConfig(Intent intent) {
    Bundle extras = intent.getExtras();

    HeadlessJsTaskRetryPolicy retryPolicy = new LinearCountingRetryPolicy(
      5, // Max number of retry attempts
      500 // Delay between each retry attempt
    );

    return new HeadlessJsTaskConfig(
      "RNIncommingCall",
      Arguments.fromBundle(extras),
      5000,
      false,
      retryPolicy
    );
  }
}
