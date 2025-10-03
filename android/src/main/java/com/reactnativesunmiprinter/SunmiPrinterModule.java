package com.reactnativesunmiprinter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.module.annotations.ReactModule;
import com.sunmi.peripheral.printer.InnerPrinterCallback;
import com.sunmi.peripheral.printer.InnerPrinterManager;
import com.sunmi.peripheral.printer.InnerResultCallback;
import com.sunmi.peripheral.printer.SunmiPrinterService;

@ReactModule(name = SunmiPrinterModule.NAME)
public class SunmiPrinterModule extends NativeSunmiPrinterSpec {

  public static final String NAME = "SunmiPrinter";

  private static final String TAG = "SunmiPrinter";
  private static final String ERROR_CODE = "E_SUNMI_PRINTER";

  private SunmiPrinterService printerService;

  private final InnerResultCallback innerResultCallback = new InnerResultCallback() {
    @Override
    public void onRunResult(boolean isSuccess) {
      Log.d(TAG, "onRunResult: " + isSuccess);
    }

    @Override
    public void onReturnString(String result) {
      Log.d(TAG, "onReturnString: " + result);
    }

    @Override
    public void onRaiseException(int code, String msg) {
      Log.w(TAG, "onRaiseException: code=" + code + " message=" + msg);
    }

    @Override
    public void onPrintResult(int code, String msg) {
      Log.d(TAG, "onPrintResult: code=" + code + " message=" + msg);
    }
  };

  private final InnerPrinterCallback innerPrinterCallback = new InnerPrinterCallback() {
    @Override
    protected void onConnected(SunmiPrinterService service) {
      printerService = service;
      Log.i(TAG, "Sunmi printer service connected");
    }

    @Override
    protected void onDisconnected() {
      printerService = null;
      Log.w(TAG, "Sunmi printer service disconnected");
    }
  };

  public SunmiPrinterModule(ReactApplicationContext reactContext) {
    super(reactContext);
    bindService();
  }

  private void bindService() {
    try {
      InnerPrinterManager.getInstance().bindService(getReactApplicationContext(), innerPrinterCallback);
    } catch (RemoteException e) {
      Log.e(TAG, "Failed to bind printer service", e);
    }
  }

  private void unbindService() {
    try {
      InnerPrinterManager.getInstance().unBindService(getReactApplicationContext(), innerPrinterCallback);
    } catch (RemoteException e) {
      Log.w(TAG, "Failed to unbind printer service", e);
    }
  }

  @Override
  public void onCatalystInstanceDestroy() {
    super.onCatalystInstanceDestroy();
    unbindService();
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

  private boolean ensureService(@Nullable Promise promise) {
    if (printerService == null) {
      String message = "Sunmi printer service is not connected";
      if (promise != null) {
        promise.reject(ERROR_CODE, message);
      }
      Log.w(TAG, message);
      return false;
    }
    return true;
  }

  private void reject(Promise promise, Throwable throwable) {
    Log.e(TAG, throwable.getMessage(), throwable);
    promise.reject(ERROR_CODE, throwable.getMessage(), throwable);
  }

  /**
   * 初始化打印机，重置打印逻辑程序，但不清空缓存区数据，因此
   * 未完成的打印工作将在重置后继续。
   */
  @ReactMethod
  public void printerInit() {
    if (!ensureService(null)) {
      return;
    }
    try {
      printerService.printerInit(innerResultCallback);
    } catch (RemoteException e) {
      Log.e(TAG, "printerInit failed", e);
    }
  }

  /**
   * 打印自检。
   */
  @ReactMethod
  public void printerSelfChecking() {
    if (!ensureService(null)) {
      return;
    }
    try {
      printerService.printerSelfChecking(innerResultCallback);
    } catch (RemoteException e) {
      Log.e(TAG, "printerSelfChecking failed", e);
    }
  }

  @ReactMethod
  public void getPrinterSerialNo(Promise promise) {
    if (!ensureService(promise)) {
      return;
    }
    try {
      promise.resolve(printerService.getPrinterSerialNo());
    } catch (RemoteException e) {
      reject(promise, e);
    }
  }

  @ReactMethod
  public void getPrinterVersion(Promise promise) {
    if (!ensureService(promise)) {
      return;
    }
    try {
      promise.resolve(printerService.getPrinterVersion());
    } catch (RemoteException e) {
      reject(promise, e);
    }
  }

  @ReactMethod
  public void getPrinterModal(Promise promise) {
    if (!ensureService(promise)) {
      return;
    }
    try {
      promise.resolve(printerService.getPrinterModal());
    } catch (RemoteException e) {
      reject(promise, e);
    }
  }

  @ReactMethod
  public void getPrinterPaper(Promise promise) {
    if (!ensureService(promise)) {
      return;
    }
    try {
      promise.resolve(printerService.getPrinterPaper() == 1 ? "58mm" : "80mm");
    } catch (RemoteException e) {
      reject(promise, e);
    }
  }

  @ReactMethod
  public void updatePrinterState(Promise promise) {
    if (!ensureService(promise)) {
      return;
    }
    try {
      promise.resolve(printerService.updatePrinterState());
    } catch (RemoteException e) {
      reject(promise, e);
    }
  }

  @ReactMethod
  public void getServiceVersion(Promise promise) {
    if (!ensureService(promise)) {
      return;
    }
    try {
      promise.resolve(printerService.getServiceVersion());
    } catch (RemoteException e) {
      reject(promise, e);
    }
  }

  @ReactMethod
  public void getPrintedLength() {
    if (!ensureService(null)) {
      return;
    }
    try {
      printerService.getPrintedLength(innerResultCallback);
    } catch (RemoteException e) {
      Log.e(TAG, "getPrintedLength failed", e);
    }
  }

  @ReactMethod
  public void hasPrinter(Promise promise) {
    promise.resolve(printerService != null);
  }

  @ReactMethod
  public void sendRAWData(String base64Data) {
    if (!ensureService(null)) {
      return;
    }
    try {
      byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
      printerService.sendRAWData(bytes, innerResultCallback);
    } catch (IllegalArgumentException | RemoteException e) {
      Log.e(TAG, "sendRAWData failed", e);
    }
  }

  @ReactMethod
  public void setFontName(String typeface) {
    if (!ensureService(null)) {
      return;
    }
    try {
      printerService.setFontName(typeface, innerResultCallback);
    } catch (RemoteException e) {
      Log.e(TAG, "setFontName failed", e);
    }
  }

  @ReactMethod
  public void setPrinterStyle(int key, int value) {
    if (!ensureService(null)) {
      return;
    }
    try {
      printerService.setPrinterStyle(key, value);
    } catch (RemoteException e) {
      Log.e(TAG, "setPrinterStyle failed", e);
    }
  }

  @ReactMethod
  public void setAlignment(int alignment) {
    if (!ensureService(null)) {
      return;
    }
    try {
      printerService.setAlignment(alignment, innerResultCallback);
    } catch (RemoteException e) {
      Log.e(TAG, "setAlignment failed", e);
    }
  }

  @ReactMethod
  public void setFontSize(double fontSize) {
    if (!ensureService(null)) {
      return;
    }
    try {
      printerService.setFontSize((float) fontSize, innerResultCallback);
    } catch (RemoteException e) {
      Log.e(TAG, "setFontSize failed", e);
    }
  }

  @ReactMethod
  public void setFontWeight(boolean isWeight) {
    if (!ensureService(null)) {
      return;
    }
    try {
      if (isWeight) {
        printerService.sendRAWData(ESCUtil.boldOn(), null);
      } else {
        printerService.sendRAWData(ESCUtil.boldOff(), null);
      }
    } catch (RemoteException e) {
      Log.e(TAG, "setFontWeight failed", e);
    }
  }

  @ReactMethod
  public void printerText(String text) {
    if (!ensureService(null)) {
      return;
    }
    try {
      printerService.printText(text, null);
    } catch (RemoteException e) {
      Log.e(TAG, "printerText failed", e);
    }
  }

  @ReactMethod
  public void printTextWithFont(String text, String typeface, double fontSize) {
    if (!ensureService(null)) {
      return;
    }
    try {
      printerService.printTextWithFont(text, typeface, (float) fontSize, null);
    } catch (RemoteException e) {
      Log.e(TAG, "printTextWithFont failed", e);
    }
  }

  @ReactMethod
  public void printOriginalText(String text) {
    if (!ensureService(null)) {
      return;
    }
    try {
      printerService.printOriginalText(text, null);
    } catch (RemoteException e) {
      Log.e(TAG, "printOriginalText failed", e);
    }
  }

  @ReactMethod
  public void printColumnsText(ReadableArray textsArray, ReadableArray widthsArray, ReadableArray alignsArray) {
    if (!ensureService(null)) {
      return;
    }
    try {
      String[] texts = readableArrayToStringArray(textsArray);
      int[] widths = readableArrayToIntArray(widthsArray);
      int[] aligns = readableArrayToIntArray(alignsArray);
      printerService.printColumnsText(texts, widths, aligns, null);
    } catch (RemoteException e) {
      Log.e(TAG, "printColumnsText failed", e);
    }
  }

  @ReactMethod
  public void printColumnsString(ReadableArray textsArray, ReadableArray widthsArray, ReadableArray alignsArray) {
    if (!ensureService(null)) {
      return;
    }
    try {
      String[] texts = readableArrayToStringArray(textsArray);
      int[] widths = readableArrayToIntArray(widthsArray);
      int[] aligns = readableArrayToIntArray(alignsArray);
      printerService.printColumnsString(texts, widths, aligns, null);
    } catch (RemoteException e) {
      Log.e(TAG, "printColumnsString failed", e);
    }
  }

  @ReactMethod
  public void printBitmap(String encodedString, int pixelWidth) {
    if (!ensureService(null)) {
      return;
    }
    Bitmap bitmap = decodeBitmap(encodedString, pixelWidth);
    if (bitmap == null) {
      Log.w(TAG, "printBitmap: failed to decode image");
      return;
    }
    try {
      printerService.printBitmap(bitmap, innerResultCallback);
    } catch (RemoteException e) {
      Log.e(TAG, "printBitmap failed", e);
    } finally {
      bitmap.recycle();
    }
  }

  @ReactMethod
  public void printBitmapCustom(String encodedString, int pixelWidth, int type) {
    printBitmapBase64Custom(encodedString, pixelWidth, type);
  }

  @ReactMethod
  public void printBitmapBase64Custom(String encodedString, int pixelWidth, int type) {
    if (!ensureService(null)) {
      return;
    }
    Bitmap bitmap = decodeBitmap(encodedString, pixelWidth);
    if (bitmap == null) {
      Log.w(TAG, "printBitmapBase64Custom: failed to decode image");
      return;
    }
    try {
      printerService.printBitmapCustom(bitmap, type, innerResultCallback);
    } catch (RemoteException e) {
      Log.e(TAG, "printBitmapBase64Custom failed", e);
    } finally {
      bitmap.recycle();
    }
  }

  @ReactMethod
  public void printBarCode(String data, int symbology, int height, int width, int textPosition) {
    if (!ensureService(null)) {
      return;
    }
    try {
      printerService.printBarCode(data, symbology, height, width, textPosition, innerResultCallback);
    } catch (RemoteException e) {
      Log.e(TAG, "printBarCode failed", e);
    }
  }

  @ReactMethod
  public void printQRCode(String data, int modulesize, int errorlevel) {
    if (!ensureService(null)) {
      return;
    }
    try {
      printerService.printQRCode(data, modulesize, errorlevel, innerResultCallback);
    } catch (RemoteException e) {
      Log.e(TAG, "printQRCode failed", e);
    }
  }

  @ReactMethod
  public void print2DCode(String data, int symbology, int modulesize, int errorlevel) {
    if (!ensureService(null)) {
      return;
    }
    try {
      printerService.print2DCode(data, symbology, modulesize, errorlevel, innerResultCallback);
    } catch (RemoteException e) {
      Log.e(TAG, "print2DCode failed", e);
    }
  }

  @ReactMethod
  public void commitPrint(ReadableArray transactions) {
    Log.w(TAG, "commitPrint is not supported in the React Native binding");
  }

  @ReactMethod
  public void enterPrinterBuffer(boolean clear) {
    if (!ensureService(null)) {
      return;
    }
    try {
      printerService.enterPrinterBuffer(clear);
    } catch (RemoteException e) {
      Log.e(TAG, "enterPrinterBuffer failed", e);
    }
  }

  @ReactMethod
  public void exitPrinterBuffer(boolean commit) {
    if (!ensureService(null)) {
      return;
    }
    try {
      printerService.exitPrinterBuffer(commit);
    } catch (RemoteException e) {
      Log.e(TAG, "exitPrinterBuffer failed", e);
    }
  }

  @ReactMethod
  public void commitPrinterBuffer() {
    if (!ensureService(null)) {
      return;
    }
    try {
      printerService.commitPrinterBuffer();
    } catch (RemoteException e) {
      Log.e(TAG, "commitPrinterBuffer failed", e);
    }
  }

  @ReactMethod
  public void commitPrinterBufferWithCallbacka() {
    if (!ensureService(null)) {
      return;
    }
    try {
      printerService.commitPrinterBufferWithCallback(innerResultCallback);
    } catch (RemoteException e) {
      Log.e(TAG, "commitPrinterBufferWithCallbacka failed", e);
    }
  }

  @ReactMethod
  public void lineWrap(int lines) {
    if (!ensureService(null)) {
      return;
    }
    try {
      printerService.lineWrap(lines, innerResultCallback);
    } catch (RemoteException e) {
      Log.e(TAG, "lineWrap failed", e);
    }
  }

  @ReactMethod
  public void cutPaper() {
    if (!ensureService(null)) {
      return;
    }
    try {
      printerService.cutPaper(innerResultCallback);
    } catch (RemoteException e) {
      Log.e(TAG, "cutPaper failed", e);
    }
  }

  @ReactMethod
  public void openDrawer() {
    if (!ensureService(null)) {
      return;
    }
    try {
      printerService.openDrawer(innerResultCallback);
    } catch (RemoteException e) {
      Log.e(TAG, "openDrawer failed", e);
    }
  }

  @ReactMethod
  public void getDrawerStatus(Promise promise) {
    if (!ensureService(promise)) {
      return;
    }
    try {
      promise.resolve(printerService.getDrawerStatus());
    } catch (RemoteException e) {
      reject(promise, e);
    }
  }

  @ReactMethod
  public void getCutPaperTimes(Promise promise) {
    if (!ensureService(promise)) {
      return;
    }
    try {
      promise.resolve(printerService.getCutPaperTimes());
    } catch (RemoteException e) {
      reject(promise, e);
    }
  }

  private String[] readableArrayToStringArray(ReadableArray array) {
    String[] result = new String[array.size()];
    for (int i = 0; i < array.size(); i++) {
      result[i] = array.getString(i);
    }
    return result;
  }

  private int[] readableArrayToIntArray(ReadableArray array) {
    int[] result = new int[array.size()];
    for (int i = 0; i < array.size(); i++) {
      result[i] = (int) array.getDouble(i);
    }
    return result;
  }

  @Nullable
  private Bitmap decodeBitmap(String encodedString, int pixelWidth) {
    try {
      String base64 = encodedString;
      int commaIndex = base64.indexOf(',');
      if (commaIndex >= 0) {
        base64 = base64.substring(commaIndex + 1);
      }
      byte[] data = Base64.decode(base64, Base64.DEFAULT);
      Bitmap decoded = BitmapFactory.decodeByteArray(data, 0, data.length);
      if (decoded == null) {
        return null;
      }
      if (pixelWidth <= 0 || decoded.getWidth() == pixelWidth) {
        return decoded;
      }
      int width = decoded.getWidth();
      int height = decoded.getHeight();
      if (width == 0 || height == 0) {
        return decoded;
      }
      int scaledHeight = Math.max(1, (int) (((float) pixelWidth / width) * height));
      Bitmap scaled = Bitmap.createScaledBitmap(decoded, pixelWidth, scaledHeight, false);
      if (scaled != decoded) {
        decoded.recycle();
      }
      return scaled;
    } catch (IllegalArgumentException exception) {
      Log.e(TAG, "decodeBitmap: invalid image data", exception);
      return null;
    }
  }
}
