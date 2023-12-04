package com.rnincommingcall;

import androidx.annotation.NonNull;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;

@ReactModule(name = RnIncommingCallModule.NAME)
public class RnIncommingCallModule extends ReactContextBaseJavaModule {
  public static final String NAME = "RnIncommingCall";
  private final int DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE = 1222;
    private Promise mPromise;
    private final String error  = "Permission was not granted";

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
  @NonNull
  public String getName() {
    return NAME;
  }


  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  public void multiply(double a, double b, Promise promise) {
    promise.resolve(a * b);
  }

   @ReactMethod
    public void askForDispalayOverOtherAppsPermission(Promise promise) {
        mPromise = promise;
        if (!Settings.canDrawOverlays(reactContext)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + reactContext.getPackageName()));
            reactContext.startActivityForResult(intent, DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE, null);
        } else {
            promise.resolve(true);
        }
    }
}
