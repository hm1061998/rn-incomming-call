package com.rnincommingcallexample;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint;
import com.facebook.react.defaults.DefaultReactActivityDelegate;

import android.telephony.TelephonyManager;
import android.telephony.PhoneStateListener;
import android.widget.Toast;

public class MainActivity extends ReactActivity {

  /**
   * Returns the name of the main component registered from JavaScript. This is used to schedule
   * rendering of the component.
   */
  @Override
  protected String getMainComponentName() {
    return "RnIncommingCallExample";
  }

  /**
   * Returns the instance of the {@link ReactActivityDelegate}. Here we use a util class {@link
   * DefaultReactActivityDelegate} which allows you to easily enable Fabric and Concurrent React
   * (aka React 18) with two boolean flags.
   */
  @Override
  protected ReactActivityDelegate createReactActivityDelegate() {
    return new DefaultReactActivityDelegate(
        this,
        getMainComponentName(),
        // If you opted-in for the New Architecture, we enable the Fabric Renderer.
        DefaultNewArchitectureEntryPoint.getFabricEnabled());
  }

    PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
      @Override
      public void onCallStateChanged(int state, String incomingNumber) {
          super.onCallStateChanged(state, incomingNumber);
  
          switch (state) {
              case TelephonyManager.CALL_STATE_IDLE:
                  Toast.makeText(MainActivity.this, "CALL_STATE_IDLE", Toast.LENGTH_SHORT).show();
                  break;
              case TelephonyManager.CALL_STATE_RINGING:
                  Toast.makeText(MainActivity.this, "CALL_STATE_RINGING", Toast.LENGTH_SHORT).show();
                  break;
              case TelephonyManager.CALL_STATE_OFFHOOK:
                  Toast.makeText(MainActivity.this, "CALL_STATE_OFFHOOK", Toast.LENGTH_SHORT).show();
                  break;
          }
      }
  };

  private TelephonyManager mTelephonyManager;
  @Override
  public void onCreate() {
    super.onCreate();
    mTelephonyManager = (TelephonyManager) getSystemService(getApplicationContext().TELEPHONY_SERVICE);
   
  }

  @Override
  protected void onResume() {
      super.onResume();
      mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
  }
  @Override
  protected void onStop() {
      super.onStop();
      mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
  }
}
