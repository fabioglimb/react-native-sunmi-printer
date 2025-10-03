package com.reactnativesunmiprinter;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.TurboReactPackage;
import com.facebook.react.module.model.ReactModuleInfo;
import com.facebook.react.module.model.ReactModuleInfoProvider;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Method;

public class SunmiPrinterPackage extends TurboReactPackage {
  @NonNull
  @Override
  public List<NativeModule> createNativeModules(@NonNull ReactApplicationContext reactContext) {
    return Arrays.asList(
      new SunmiPrinterModule(reactContext),
      new SunmiScanModule(reactContext)
    );
  }

  @NonNull
  @Override
  public List<ViewManager> createViewManagers(@NonNull ReactApplicationContext reactContext) {
    return Collections.emptyList();
  }

  @Override
  public NativeModule getModule(String name, ReactApplicationContext reactContext) {
    if (SunmiPrinterModule.NAME.equals(name)) {
      return new SunmiPrinterModule(reactContext);
    }
    if (SunmiScanModule.NAME.equals(name)) {
      return new SunmiScanModule(reactContext);
    }
    return null;
  }

  @Override
  public ReactModuleInfoProvider getReactModuleInfoProvider() {
    return () -> {
      final Map<String, ReactModuleInfo> reactModuleInfoMap = new HashMap<>();
      final Class<?>[] modules = new Class<?>[] {
        SunmiPrinterModule.class,
        SunmiScanModule.class,
      };

      for (Class<?> moduleClass : modules) {
        ReactModule annotation = moduleClass.getAnnotation(ReactModule.class);
        if (annotation == null) {
          continue;
        }
        reactModuleInfoMap.put(
          annotation.name(),
          new ReactModuleInfo(
            annotation.name(),
            moduleClass.getName(),
            annotation.canOverrideExistingModule(),
            annotation.needsEagerInit(),
            annotation.hasConstants(),
            annotation.isCxxModule(),
            isTurboModule(annotation)
          )
        );
      }

      return reactModuleInfoMap;
    };
  }

  private boolean isTurboModule(ReactModule annotation) {
    try {
      Method method = ReactModule.class.getMethod("isTurboModule");
      return (boolean) method.invoke(annotation);
    } catch (Exception ignored) {
      return false;
    }
  }
}
