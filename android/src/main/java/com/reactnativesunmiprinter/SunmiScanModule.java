package com.reactnativesunmiprinter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.module.annotations.ReactModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

@ReactModule(name = SunmiScanModule.NAME)
public class SunmiScanModule extends NativeSunmiScanModuleSpec {
  public static final String NAME = "SunmiScanModule";
  private static ReactApplicationContext reactContext;
  private static final int START_SCAN = 0x0000;
  private static final int START_SCANNER = 0x0001;
  private static final String E_ACTIVITY_DOES_NOT_EXIST = "E_ACTIVITY_DOES_NOT_EXIST";
  private static final String E_FAILED_TO_SHOW_SCAN = "E_FAILED_TO_SHOW_SCAN";
  private static final String ACTION_DATA_CODE_RECEIVED = "com.sunmi.scanner.ACTION_DATA_CODE_RECEIVED";
  private static final String DATA = "data";
  private static final String SOURCE = "source_byte";
  private Promise mPickerPromise;
  private Promise mScannerPromise;

  private BroadcastReceiver receiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      if (ACTION_DATA_CODE_RECEIVED.equals(action)) {
        String code = intent.getStringExtra(DATA);
        byte[] arr = intent.getByteArrayExtra(SOURCE);
        if (code != null && !code.isEmpty()) {
          sendEvent(code);
        }
      }
    }
  };

  private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {
    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
      if (requestCode == START_SCANNER) {
        if (resultCode == Activity.RESULT_OK && intent != null) {
          Bundle bundle = intent.getExtras();
          if (bundle != null) {
            ArrayList<HashMap<String, String>> result =
              (ArrayList<HashMap<String, String>>) bundle.getSerializable("data");
            if (result != null && !result.isEmpty()) {
              String scannedValue = result.get(0).get("VALUE");
              if (mScannerPromise != null) {
                mScannerPromise.resolve(scannedValue != null ? scannedValue : "");
              }
            } else if (mScannerPromise != null) {
              mScannerPromise.resolve("");
            }
          } else if (mScannerPromise != null) {
            mScannerPromise.resolve("");
          }
        } else {
          if (mScannerPromise != null) {
            mScannerPromise.reject("ScanFailed", "Scanning failed or canceled");
          }
        }
        mScannerPromise = null;
        return;
      }

      // Original scan() flow — emit events
      if (intent != null) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
          ArrayList<HashMap<String, String>> result =
            (ArrayList<HashMap<String, String>>) bundle.getSerializable("data");
          if (null != result) {
            Iterator<HashMap<String, String>> it = result.iterator();
            while (it.hasNext()) {
              HashMap hashMap = it.next();
              sendEvent(hashMap.get("VALUE").toString());
            }
          }
        }
      }
    }
  };

  public SunmiScanModule(ReactApplicationContext context) {
    super(context);
    reactContext = context;
    reactContext.addActivityEventListener(mActivityEventListener);
    registerReceiver();
  }

  @Override
  public String getName() {
    return NAME;
  }

  @ReactMethod
  public void scan(final Promise promise) {
    Activity currentActivity = getCurrentActivity();
    if (currentActivity == null) {
      promise.reject(E_ACTIVITY_DOES_NOT_EXIST, "Activity doesn't exist");
      return;
    }
    mPickerPromise = promise;
    try {
      Intent intent = new Intent("com.sunmi.scan");
      intent.setPackage("com.sunmi.sunmiqrcodescanner");
      intent.putExtra("PLAY_SOUND", true);
      currentActivity.startActivityForResult(intent, START_SCAN);
    } catch (Exception e) {
      mPickerPromise.reject("E_FAILED_TO_SHOW_SCAN", e);
      mPickerPromise = null;
    }
  }

  @ReactMethod
  public void startScanner(final Promise promise) {
    Activity currentActivity = getCurrentActivity();
    if (currentActivity == null) {
      promise.reject(E_ACTIVITY_DOES_NOT_EXIST, "Activity doesn't exist");
      return;
    }
    mScannerPromise = promise;
    try {
      Intent intent = new Intent("com.sunmi.scanner.qrscanner");
      intent.putExtra("PLAY_SOUND", true);
      intent.putExtra("PLAY_VIBRATE", false);
      intent.putExtra("IDENTIFY_MORE_CODE", false);
      intent.putExtra("IS_SHOW_SETTING", true);
      intent.putExtra("IS_SHOW_ALBUM", true);
      intent.putExtra("IDENTIFY_INVERSE", true);
      intent.putExtra("IS_EAN_8_ENABLE", true);
      intent.putExtra("IS_UPC_E_ENABLE", true);
      intent.putExtra("IS_ISBN_10_ENABLE", false);
      intent.putExtra("IS_CODE_11_ENABLE", true);
      intent.putExtra("IS_UPC_A_ENABLE", true);
      intent.putExtra("IS_EAN_13_ENABLE", true);
      intent.putExtra("IS_ISBN_13_ENABLE", true);
      intent.putExtra("IS_INTERLEAVED_2_OF_5_ENABLE", true);
      intent.putExtra("IS_CODE_128_ENABLE", true);
      intent.putExtra("IS_CODABAR_ENABLE", true);
      intent.putExtra("IS_CODE_39_ENABLE", true);
      intent.putExtra("IS_CODE_93_ENABLE", true);
      intent.putExtra("IS_DATABAR_ENABLE", true);
      intent.putExtra("IS_DATABAR_EXP_ENABLE", true);
      intent.putExtra("IS_Micro_PDF417_ENABLE", true);
      intent.putExtra("IS_MicroQR_ENABLE", true);
      intent.putExtra("IS_OPEN_LIGHT", true);
      intent.putExtra("SCAN_MODE", false);
      intent.putExtra("IS_QR_CODE_ENABLE", true);
      intent.putExtra("IS_PDF417_ENABLE", true);
      intent.putExtra("IS_DATA_MATRIX_ENABLE", true);
      intent.putExtra("IS_AZTEC_ENABLE", true);
      intent.putExtra("IS_Hanxin_ENABLE", false);
      currentActivity.startActivityForResult(intent, START_SCANNER);
    } catch (Exception e) {
      mScannerPromise.reject(E_FAILED_TO_SHOW_SCAN, e);
      mScannerPromise = null;
    }
  }

  private void registerReceiver() {
    IntentFilter filter = new IntentFilter();
    filter.addAction(ACTION_DATA_CODE_RECEIVED);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      reactContext.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED);
    } else {
      reactContext.registerReceiver(receiver, filter);
    }
  }

  @Override
  public void onCatalystInstanceDestroy() {
    super.onCatalystInstanceDestroy();
    reactContext.removeActivityEventListener(mActivityEventListener);
    try {
      reactContext.unregisterReceiver(receiver);
    } catch (IllegalArgumentException ignored) {
      // Receiver already unregistered
    }
  }

  private static void sendEvent(String msg) {
    reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("onScanSuccess", msg);
  }
}
