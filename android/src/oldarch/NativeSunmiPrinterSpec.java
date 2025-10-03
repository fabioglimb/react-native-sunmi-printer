package com.reactnativesunmiprinter;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.turbomodule.core.interfaces.TurboModule;

public abstract class NativeSunmiPrinterSpec extends ReactContextBaseJavaModule implements TurboModule {
  protected NativeSunmiPrinterSpec(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  @NonNull
  public abstract String getName();

  public abstract void printerInit();

  public abstract void printerSelfChecking();

  public abstract void getPrinterSerialNo(Promise promise);

  public abstract void getPrinterVersion(Promise promise);

  public abstract void getServiceVersion(Promise promise);

  public abstract void getPrinterModal(Promise promise);

  public abstract void getPrinterPaper(Promise promise);

  public abstract void getPrintedLength();

  public abstract void updatePrinterState(Promise promise);

  public abstract void hasPrinter(Promise promise);

  public abstract void sendRAWData(String data);

  public abstract void setPrinterStyle(int key, int value);

  public abstract void setAlignment(int alignment);

  public abstract void setFontName(String typeface);

  public abstract void setFontSize(double fontSize);

  public abstract void setFontWeight(boolean isWeight);

  public abstract void printerText(String text);

  public abstract void printTextWithFont(String text, String typeface, double fontSize);

  public abstract void printOriginalText(String text);

  public abstract void printColumnsText(ReadableArray texts, ReadableArray widths, ReadableArray aligns);

  public abstract void printColumnsString(ReadableArray texts, ReadableArray widths, ReadableArray aligns);

  public abstract void printBitmap(String encodedString, int pixelWidth);

  public abstract void printBitmapCustom(String encodedString, int pixelWidth, int type);

  public abstract void printBitmapBase64Custom(String encodedString, int pixelWidth, int type);

  public abstract void printBarCode(String data, int symbology, int height, int width, int textPosition);

  public abstract void printQRCode(String data, int modulesize, int errorlevel);

  public abstract void print2DCode(String data, int symbology, int modulesize, int errorlevel);

  public abstract void commitPrint(ReadableArray transactions);

  public abstract void enterPrinterBuffer(boolean clear);

  public abstract void exitPrinterBuffer(boolean commit);

  public abstract void commitPrinterBuffer();

  public abstract void commitPrinterBufferWithCallbacka();

  public abstract void lineWrap(int lines);

  public abstract void cutPaper();

  public abstract void openDrawer();

  public abstract void getDrawerStatus(Promise promise);

  public abstract void getCutPaperTimes(Promise promise);
}
