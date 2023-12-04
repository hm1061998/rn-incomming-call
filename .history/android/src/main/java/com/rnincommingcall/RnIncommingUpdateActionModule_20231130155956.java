package com.pritesh.calldetection;

import com.facebook.react.bridge.JavaScriptModule;

public interface RnIncommingUpdateActionModule extends JavaScriptModule {
    void callStateUpdated(String state, String phoneNumber);
}
