package com.rnincommingcall;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyCallback ;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.content.IntentFilter;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;


import java.util.HashMap;
import java.util.Map;


import com.rnincommingcall.RnIncommingUpdateActionModule;


public class RnIncommingCallModule
        extends ReactContextBaseJavaModule
        implements Application.ActivityLifecycleCallbacks,
        CallDetectionPhoneStateListener.PhoneCallStateUpdate {

    public static final String NAME = "RnIncommingCall";

    private boolean wasAppInOffHook = false;
    private boolean wasAppInRinging = false;
    private ReactApplicationContext reactContext;
    private TelephonyManager telephonyManager;
    private RnIncommingUpdateActionModule jsModule = null;
    private CallDetectionPhoneStateListener callDetectionPhoneStateListener;
    private Activity activity = null;

    private static final String TAG = "RnIncommingCallManager";

    private Context appContext;
    private RNIncommingCallReciever callReceiver;

    public RnIncommingCallModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @ReactMethod
    public void startListener() {
        if (activity == null) {
            activity = getCurrentActivity();
            activity.getApplication().registerActivityLifecycleCallbacks(this);
        }

        telephonyManager = (TelephonyManager) this.reactContext.getSystemService(
                Context.TELEPHONY_SERVICE);
        callDetectionPhoneStateListener = new CallDetectionPhoneStateListener(this);
        telephonyManager.listen(callDetectionPhoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);

       
    }

    @ReactMethod
    public void stopListener() {
        telephonyManager.listen(callDetectionPhoneStateListener,
                PhoneStateListener.LISTEN_NONE);
        telephonyManager = null;
        callDetectionPhoneStateListener = null;
    }

    /**
     * @return a map of constants this module exports to JS. Supports JSON types.
     */
    public
    Map<String, Object> getConstants() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("Incoming", "Incoming");
        map.put("Offhook", "Offhook");
        map.put("Disconnected", "Disconnected");
        map.put("Missed", "Missed");
        return map;
    }

    // Activity Lifecycle Methods
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceType) {
      if (appContext == null) {
          appContext = activity.getApplicationContext();
          callReceiver = new RNIncommingCallReciever();
          IntentFilter filter = new IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
          appContext.registerReceiver(callReceiver, filter);
      }
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        // if (callReceiver != null) {
        //     appContext.unregisterReceiver(callReceiver);
        //     callReceiver = null;
        // }
    }

    @Override
    public void phoneCallStateUpdated(int state, String phoneNumber) {
        jsModule = this.reactContext.getJSModule(RnIncommingUpdateActionModule.class);

        switch (state) {
            //Hangup
            case TelephonyManager.CALL_STATE_IDLE:
                if(wasAppInOffHook == true) { // if there was an ongoing call and the call state switches to idle, the call must have gotten disconnected
                    jsModule.callStateUpdated("Disconnected", phoneNumber);
                } else if(wasAppInRinging == true) { // if the phone was ringing but there was no actual ongoing call, it must have gotten missed
                    jsModule.callStateUpdated("Missed", phoneNumber);
                }

                //reset device state
                wasAppInRinging = false;
                wasAppInOffHook = false;
                break;
            //Outgoing
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Device call state: Off-hook. At least one call exists that is dialing, active, or on hold, and no calls are ringing or waiting.
                wasAppInOffHook = true;
                jsModule.callStateUpdated("Offhook", phoneNumber);
                break;
            //Incoming
            case TelephonyManager.CALL_STATE_RINGING:
                // Device call state: Ringing. A new call arrived and is ringing or waiting. In the latter case, another call is already active.
                wasAppInRinging = true;
                jsModule.callStateUpdated("Incoming", phoneNumber);
                break;
        }
    }
}
