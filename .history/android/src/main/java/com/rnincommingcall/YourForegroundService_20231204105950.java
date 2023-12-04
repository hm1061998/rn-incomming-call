package com.rnincommingcall;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.content.Context;

import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import com.rnincommingcall.R;

import androidx.core.app.NotificationCompat;

public class YourForegroundService extends Service {
    private static final String CHANNEL_ID = "ForegroundServiceChannel";

    static View view = null;
    static WindowManager wm;
    static LayoutInflater inflater;
    static WindowManager.LayoutParams params;
    private static TextView txtIncomingnumber;
    static boolean processingAction = false;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();

        Intent notificationIntent = new Intent(getApplicationContext(), getMainActivityClass(getApplicationContext()));
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) ? (PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT) : PendingIntent.FLAG_UPDATE_CURRENT);

        int appIcon = getApplicationInfo().icon;
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText("Service is running...")
                .setSmallIcon(appIcon)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_HIGH)
                .build();

        startForeground(1, notification);


         params = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, 350,
                    Build.VERSION.SDK_INT < Build.VERSION_CODES.O ?
                    WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY :
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.activity_main,null);
        wm.addView(view, params);

       String phoneNumber =  intent.getStringExtra("IncomingNumber");

        txtIncomingnumber = (TextView)view.findViewById(R.id.txtIncomingnumber);
        txtIncomingnumber.setText("You have Incoming Call from " + phoneNumber);

        // TODO: Add your foreground tasks here

        return START_NOT_STICKY;
    }

     @Override
    public void onDestroy() {
        super.onDestroy();
        if(view != null) {
           wm.removeViewImmediate(view);
                   
        }
       stopForeground(true);
        // Các công việc khác khi dịch vụ bị hủy
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ForegroundServiceChannel";
            String description = "Channel for Foreground Service";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            Context context = getApplicationContext();

            NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

     private Class getMainActivityClass(Context context) {
        String packageName = context.getPackageName();
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent == null || launchIntent.getComponent() == null) {
            Log.e("NotificationHelper", "Failed to get launch intent or component");
            return null;
        }
        try {
            return Class.forName(launchIntent.getComponent().getClassName());
        } catch (ClassNotFoundException e) {
            Log.e("NotificationHelper", "Failed to get main activity class");
            return null;
        }
    }
}
