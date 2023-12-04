package com.rnincommingcallexample;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.app.ActivityManager;
import java.io.File;
import java.util.*;
import com.rnincommingcallexample.RNIncommingCallService;
import com.facebook.react.HeadlessJsTaskService;
import android.widget.Toast;

public final class RNIncommingCallReciever extends BroadcastReceiver {

    private TelephonyManager mTelephonyManager;
    public static boolean isListening = false;
 
    @Override
    public void onReceive(final Context context, Intent intent) {
 
        mTelephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
 
        PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
 
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        Toast.makeText(context, "CALL_STATE_IDLE", Toast.LENGTH_SHORT).show();
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        Toast.makeText(context, "CALL_STATE_RINGING", Toast.LENGTH_SHORT).show();
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        Toast.makeText(context, "CALL_STATE_OFFHOOK", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
 
        if(!isListening) {
            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
            isListening = true;
        }
    }
  
}