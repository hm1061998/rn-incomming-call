package com.rnincommingcall;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.telephony.PhoneNumberUtils;
import android.app.ActivityManager;
import java.io.File;
import java.util.*;
import com.rnincommingcall.RNIncommingCallService;
import com.facebook.react.HeadlessJsTaskService;
import android.net.Uri;
import android.os.Build;

public final class RNIncommingCallReciever extends BroadcastReceiver {

    public final void onReceive(Context context, Intent intent) {
      //  Intent startIntent = context
      //       .getPackageManager()
      //       .getLaunchIntentForPackage(context.getPackageName());
          
            // context.startActivity(startIntent);

             Intent serviceIntent = new Intent(context, YourForegroundService.class);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Bắt đầu Foreground Service
                context.startForegroundService(serviceIntent);
            } else {
                // Bắt đầu dịch vụ trước API level 26
                context.startService(serviceIntent);
            }

        if (isAppOnForeground((context)) == 0) {
            Boolean incomingCall = false;
            Intent recIntent = new Intent(context, RNIncommingCallService.class);
           

            if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
                recIntent.putExtra("action", "phone_state");
                String phoneState = intent.getStringExtra("state");
                if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    // String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    String phoneNumber = PhoneNumberUtils.getNumberFromIntent(intent, context);
                    incomingCall = true;
                    recIntent.putExtra("state", "extra_state_ringing");
                    recIntent.putExtra("incoming_call", true);
                    recIntent.putExtra("number", phoneNumber);
                } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    if (incomingCall) {
                        incomingCall = false;
                    }
                    recIntent.putExtra("state", "extra_state_offhook");
                    recIntent.putExtra("incoming_call", false);
                } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    if (incomingCall) {
                        incomingCall = false;
                    }
                    recIntent.putExtra("state", "extra_state_idle");
                    recIntent.putExtra("incoming_call", false);
                }
            } else {
                recIntent.putExtra("action", "new_outgoing_call");
            }
            
          
            context.startService(recIntent);

            HeadlessJsTaskService.acquireWakeLockNow(context);
        }
    }

    //check weather the App is in Foreground if it is then it will crash.

    private int isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List < ActivityManager.RunningAppProcessInfo > appProcesses =
            activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return 0;
        }

        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess: appProcesses) {
            if (appProcess.importance ==
                ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                appProcess.processName.equals(packageName)) {
                return 1;
            }
        }
        return 0;
    }

   

  
}
