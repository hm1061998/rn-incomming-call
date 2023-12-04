package com.rnincommingcall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.telephony.PhoneStateListener;
import android.app.ActivityManager;
import java.util.*;
import com.rnincommingcall.RNIncommingCallService;
import com.facebook.react.HeadlessJsTaskService;
import android.net.Uri;
import android.os.Build;
import android.util.Log;




public final class RNIncommingCallReciever extends BroadcastReceiver {

    static String IncomingNumber;
    static Intent serviceIntent;

     public final void onReceive(Context context, Intent intent) {
      if (serviceIntent == null) {
        serviceIntent = new Intent(context, YourForegroundService.class);
        // serviceIntent = new Intent(context, IncomingCallService.class);
     
      }
         
       
          Log.i("CallReceiverBroadcast", "onReciever");
        if (isAppOnForeground((context)) == 0) {
            Boolean incomingCall = false;
            Intent recIntent = new Intent(context, RNIncommingCallService.class);


          //  Intent imcomingIntent = new Intent(context, IncomingCallService.class);
         
            if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
                recIntent.putExtra("action", "phone_state");
                String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                    
                    
                    recIntent.putExtra("state", phoneState);
                if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {

                  Log.i("CallReceiverBroadcast", "EXTRA_STATE_RINGING");


                    String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

                      Log.i("CallReceiverBroadcast", "phoneNumber: " + phoneNumber);

                    IncomingNumber = phoneNumber;

                    // if(phoneNumber!=null){
                    //   serviceIntent.putExtra("IncomingNumber",phoneNumber);
                    // //  context.startService(imcomingIntent);

                    //   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    //   // Bắt đầu Foreground Service
                    //       context.startForegroundService(serviceIntent);
                    //   } else {
                    //       // Bắt đầu dịch vụ trước API level 26
                    //       context.startService(serviceIntent);
                    //   }
                    // }

                      serviceIntent.putExtra("IncomingNumber",phoneNumber);
                    //  context.startService(imcomingIntent);

                      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                      // Bắt đầu Foreground Service
                          context.startForegroundService(serviceIntent);
                      } else {
                          // Bắt đầu dịch vụ trước API level 26
                          context.startService(serviceIntent);
                      }
                      
                   

                    incomingCall = true;
                
                    recIntent.putExtra("incoming_call", true);
                    recIntent.putExtra("number", phoneNumber);

                

                } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {

                  Log.i("CallReceiverBroadcast", "EXTRA_STATE_OFFHOOK");

                    if (incomingCall) {
                        incomingCall = false;
                    }
                  
                    recIntent.putExtra("incoming_call", false);
                } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {

                  Log.i("CallReceiverBroadcast", "EXTRA_STATE_IDLE");

                    if (incomingCall) {
                        incomingCall = false;
                    }
                 
                    recIntent.putExtra("incoming_call", false);
                    // context.stopService(imcomingIntent);
                    context.stopService(serviceIntent);

                  
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
