package com.rnincommingcall;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyCallback ;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.content.IntentFilter;
import android.provider.Settings;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Promise;


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
    private final int DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE = 1222;

    private Context appContext;
    private RNIncommingCallReciever callReceiver;
    private Promise mPromise;

    private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {
      @Override
      public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
          super.onActivityResult(activity, requestCode, resultCode, data);
          if (requestCode == DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE) {
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                  if (Settings.canDrawOverlays(activity.getApplicationContext())) {
                      // Permission Granted by Overlay
                      mPromise.resolve(true);
                  }
                  else {
                      mPromise.reject(new Throwable(error));
                  }
              } else {
                  mPromise.resolve(true);
              }

          }
      }


    };

    public RnIncommingCallModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        this.reactContext.addActivityEventListener(mActivityEventListener);
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

    @ReactMethod
    public void askForDispalayOverOtherAppsPermission(Promise promise) {
        mPromise = promise;
        if (!Settings.canDrawOverlays(this.reactContext)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + this.reactContext.getPackageName()));
            this.reactContext.startActivityForResult(intent, DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE, null);
        } else {
            promise.resolve(true);
        }
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
