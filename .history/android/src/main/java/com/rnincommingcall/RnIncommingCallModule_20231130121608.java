package com.rnincommingcall;

import androidx.annotation.NonNull;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;

@ReactModule(name = RnIncommingCallModule.NAME)
public class RnIncommingCallModule extends ReactContextBaseJavaModule {
  public static final String NAME = "RnIncommingCall";

  public RnIncommingCallModule(ReactApplicationContext reactContext) {
    super(reactContext);
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
        if (!Settings.canDrawOverlays(this.reactContext)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + this.reactContext.getPackageName()));
            this.reactContext.startActivityForResult(intent, DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE, null);
        } else {
            promise.resolve(true);
        }
    }
}
