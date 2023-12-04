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

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;

import android.graphics.Color;
import android.graphics.PixelFormat;

public final class RNIncommingCallReciever extends BroadcastReceiver {

    static String IncomingNumber;
    static Intent serviceIntent;

            private WindowManager wm;
        private static LinearLayout ly1;
        private WindowManager.LayoutParams params1;


    // @Override
    // public final void onReceive(Context context, Intent intent) {
      
    //   Intent serviceIntent = new Intent(context, YourForegroundService.class);

      
    //    if (isAppOnForeground((context)) == 0) {

    //       TelephonyManager teleMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
    //       PhoneStateListener psl = new PhoneStateListener() {

    //       Intent recIntent = new Intent(context, RNIncommingCallService.class);

    //       @Override
    //       public void onCallStateChanged(int state, String incomingNumber) {
    //           switch (state) {
    //           case TelephonyManager.CALL_STATE_RINGING:
    //               IncomingNumber = incomingNumber;
    //               // Log.i("CallReceiverBroadcast", "Incoming call caught. Caller's number is " + incomingNumber + ".");
    //               // Intent i = new Intent(context, IncomingCallService.class);
    //               // context.startService(i);
    //               recIntent.putExtra("state", "extra_state_ringing");
    //               recIntent.putExtra("number", incomingNumber);

    //               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    //                 // Bắt đầu Foreground Service
    //                 context.startForegroundService(serviceIntent);
    //                 } else {
    //                     // Bắt đầu dịch vụ trước API level 26
    //                     context.startService(serviceIntent);
    //                 }

    //               break;
    //           case TelephonyManager.CALL_STATE_IDLE:
    //               // Log.i("CallReceiverBroadcast", "CALL_STATE_IDLE");                  
    //               // IncomingCallService.clearView(context);
    //               // Call Disconnected

    //               recIntent.putExtra("state", "extra_state_idle");
    //                 recIntent.putExtra("incoming_call", false);

    //                 context.stopService(serviceIntent);
    //               break;
    //           case TelephonyManager.CALL_STATE_OFFHOOK:
    //               // Log.i("CallReceiverBroadcast", "CALL_STATE_OFFHOOK");
    //               // IncomingCallService.clearView(context);

    //               recIntent.putExtra("state", "extra_state_offhook");
    //               recIntent.putExtra("incoming_call", false);
    //               // Call Answer Mode
    //               break;
    //           }

    //           context.startService(recIntent);

    //           HeadlessJsTaskService.acquireWakeLockNow(context);
    //       }
    //       };
    //       teleMgr.listen(psl, PhoneStateListener.LISTEN_CALL_STATE);
    //       teleMgr.listen(psl, PhoneStateListener.LISTEN_NONE);
    //   }
    // }

     public final void onReceive(Context context, Intent intent) {
      if (serviceIntent == null) {
        // serviceIntent = new Intent(context, YourForegroundService.class);
        serviceIntent = new Intent(context, IncomingCallService.class);
     
      }
         
        //  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        //               // Bắt đầu Foreground Service
        //         context.startForegroundService(serviceIntent);
        //     } else {
        //         // Bắt đầu dịch vụ trước API level 26
        //         context.startService(serviceIntent);
        //     }
          Log.i("CallReceiverBroadcast", "onReciever");
        if (isAppOnForeground((context)) == 0) {
            Boolean incomingCall = false;
            Intent recIntent = new Intent(context, RNIncommingCallService.class);
         
            if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
                recIntent.putExtra("action", "phone_state");
                String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                    
                    
                    recIntent.putExtra("state", phoneState);
                if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {

                  Log.i("CallReceiverBroadcast", "EXTRA_STATE_RINGING");


                    String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    // serviceIntent.putExtra("IncomingNumber",phoneNumber);
                    //  context.startService(serviceIntent);
                    incomingCall = true;
                
                    recIntent.putExtra("incoming_call", true);
                    recIntent.putExtra("number", phoneNumber);


                     wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                    params1 = new WindowManager.LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT |
                            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                            PixelFormat.TRANSPARENT);

                    params1.height = 75;
                    params1.width = 512;
                    params1.x = 265; 
                    params1.y = 400;
                    params1.format = PixelFormat.TRANSLUCENT;

                    ly1 = new LinearLayout(context);
                    ly1.setBackgroundColor(Color.BLACK);
                    ly1.setOrientation(LinearLayout.VERTICAL);

                    wm.addView(ly1, params1);
              
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
                    // IncomingCallService.clearView(context);
                    // context.stopService(serviceIntent);

                    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                        if(ly1!=null)
                        {
                            wm.removeView(ly1);
                            ly1 = null;
                        }
                }
            } else {
                recIntent.putExtra("action", "new_outgoing_call");
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                      // Bắt đầu Foreground Service
                context.startForegroundService(recIntent);
            } else {
                // Bắt đầu dịch vụ trước API level 26
                context.startService(recIntent);
            }
         

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
