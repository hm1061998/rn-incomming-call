package com.rnincommingcall;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.react.HeadlessJsTaskService;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.jstasks.HeadlessJsTaskConfig;
import com.facebook.react.jstasks.HeadlessJsTaskRetryPolicy;
import com.facebook.react.jstasks.LinearCountingRetryPolicy;

import javax.annotation.Nullable;

public class RNIncommingCallService extends HeadlessJsTaskService {
  @Override
  protected @Nullable
  HeadlessJsTaskConfig getTaskConfig(Intent intent) {
    Bundle extras = intent.getExtras();

    HeadlessJsTaskRetryPolicy retryPolicy = new LinearCountingRetryPolicy(
      5, // Max number of retry attempts
      500 // Delay between each retry attempt
    );

    return new HeadlessJsTaskConfig(
      "RNIncommingCall",
      Arguments.fromBundle(extras),
      5000,
      false,
      retryPolicy
    );
  }
}
