package com.rnincommingcall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import android.util.Log;


public class RNIncommingCallReciever extends BroadcastReceiver {
    private static final String TAG = "CallReceiver";

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

            if (state != null && phoneNumber != null) {

              if (TelephonyManager.EXTRA_STATE_RINGING.equals(extraState)) {
                  Log.d(TAG, "Incoming call from: " + phoneNumber);
                  // Gửi thông báo đến React Native
                  CallDetectionPhoneStateListener.sendEvent("Incoming", phoneNumber);
              } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(extraState)) {
                  Log.d(TAG, "Call answered: " + phoneNumber);
                  // Gửi thông báo đến React Native
                  CallDetectionPhoneStateListener.sendEvent("Offhook", phoneNumber);
              } else if (TelephonyManager.EXTRA_STATE_IDLE.equals(extraState)) {
                  Log.d(TAG, "Call ended: " + phoneNumber);
                  // Gửi thông báo đến React Native
                  CallDetectionPhoneStateListener.sendEvent("Disconnected", phoneNumber);
              } else {
                  // Handle other states or unknown values
              }
               
            }
        }
    }
  
}
