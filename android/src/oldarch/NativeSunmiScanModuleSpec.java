package com.reactnativesunmiprinter;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.turbomodule.core.interfaces.TurboModule;

public abstract class NativeSunmiScanModuleSpec extends ReactContextBaseJavaModule implements TurboModule {
  protected NativeSunmiScanModuleSpec(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  @NonNull
  public abstract String getName();

  public abstract void scan(Promise promise);
}
