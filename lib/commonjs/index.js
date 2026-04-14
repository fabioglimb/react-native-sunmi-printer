"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = exports.SunmiScan = exports.PrinterStyleValue = exports.PrinterStyleKey = exports.AlignValue = void 0;
var _reactNative = require("react-native");
const LINKING_ERROR = "The package 'react-native-sunmi-printer' doesn't seem to be linked.\n" + 'Make sure you have run `pod install` for iOS and re-built the app after installing the package.\n' + 'If you are developing for Android, rebuild the app after installing the package.';
const isTurboModuleEnabled = global.__turboModuleProxy != null;
let turboFallbackCause;
const isAndroid = _reactNative.Platform.OS === 'android';
const getTurboSunmiPrinterModule = () => {
  if (!isTurboModuleEnabled) {
    return null;
  }
  try {
    return require('./NativeSunmiPrinter').default;
  } catch (error) {
    turboFallbackCause = error;
    return null;
  }
};
const getTurboSunmiScanModule = () => {
  if (!isTurboModuleEnabled) {
    return null;
  }
  try {
    return require('./NativeSunmiScanModule').default;
  } catch {
    return null;
  }
};
const SunmiPrinterModule = getTurboSunmiPrinterModule() ?? _reactNative.NativeModules.SunmiPrinter;
const SunmiScanModule = getTurboSunmiScanModule() ?? _reactNative.NativeModules.SunmiScanModule;
const createUnsupportedPrinterModule = () => new Proxy({}, {
  get: (_target, property) => {
    if (property === 'hasPrinter') {
      return async () => false;
    }
    return () => {
      throw new Error(`[SunmiPrinter] ${String(property)} is only available on Android devices.`);
    };
  }
});
const createUnsupportedScanModule = () => ({
  async scan() {
    throw new Error('[SunmiPrinter] scan is only available on Android devices.');
  },
  async startScanner() {
    throw new Error('[SunmiPrinter] startScanner is only available on Android devices.');
  }
});
const effectiveSunmiPrinterModule = SunmiPrinterModule ?? (!isAndroid ? createUnsupportedPrinterModule() : undefined);
const effectiveSunmiScanModule = SunmiScanModule ?? (!isAndroid ? createUnsupportedScanModule() : undefined);
if (__DEV__ && isAndroid && isTurboModuleEnabled && turboFallbackCause) {
  console.warn('[SunmiPrinter] Turbo module was not found, falling back to legacy bridge.', turboFallbackCause);
}
if (!effectiveSunmiPrinterModule) {
  throw new Error(LINKING_ERROR);
}
let PrinterStyleKey = exports.PrinterStyleKey = /*#__PURE__*/function (PrinterStyleKey) {
  // 文本倍宽
  PrinterStyleKey[PrinterStyleKey["ENABLE_DOUBLE_WIDTH"] = 1000] = "ENABLE_DOUBLE_WIDTH";
  // 文本倍高
  PrinterStyleKey[PrinterStyleKey["ENABLE_DOUBLE_HEIGHT"] = 1001] = "ENABLE_DOUBLE_HEIGHT";
  // 文本加粗
  PrinterStyleKey[PrinterStyleKey["ENABLE_BOLD"] = 1002] = "ENABLE_BOLD";
  // 文本下划线
  PrinterStyleKey[PrinterStyleKey["ENABLE_UNDERLINE"] = 1003] = "ENABLE_UNDERLINE";
  // 文本反白
  PrinterStyleKey[PrinterStyleKey["ENABLE_ANTI_WHITE"] = 1004] = "ENABLE_ANTI_WHITE";
  // 文本删除线
  PrinterStyleKey[PrinterStyleKey["ENABLE_STRIKETHROUGH"] = 1005] = "ENABLE_STRIKETHROUGH";
  // 文本斜体
  PrinterStyleKey[PrinterStyleKey["ENABLE_ILALIC"] = 1006] = "ENABLE_ILALIC";
  // 文本倒影
  PrinterStyleKey[PrinterStyleKey["ENABLE_INVERT"] = 1007] = "ENABLE_INVERT";
  // 设置文本左右间距
  PrinterStyleKey[PrinterStyleKey["SET_TEXT_RIGHT_SPACING"] = 2000] = "SET_TEXT_RIGHT_SPACING";
  // 设置相对位置
  PrinterStyleKey[PrinterStyleKey["SET_RELATIVE_POSITION"] = 2001] = "SET_RELATIVE_POSITION";
  // 设置绝对位置
  PrinterStyleKey[PrinterStyleKey["SET_ABSOLUATE_POSITION"] = 2002] = "SET_ABSOLUATE_POSITION";
  // 设置行间距
  PrinterStyleKey[PrinterStyleKey["SET_LINE_SPACING"] = 2003] = "SET_LINE_SPACING";
  // 设置左边距
  PrinterStyleKey[PrinterStyleKey["SET_LEFT_SPACING"] = 2004] = "SET_LEFT_SPACING";
  // 设置删除线的样式
  PrinterStyleKey[PrinterStyleKey["SET_STRIKETHROUGH_STYLE"] = 2005] = "SET_STRIKETHROUGH_STYLE";
  return PrinterStyleKey;
}({});
let PrinterStyleValue = exports.PrinterStyleValue = /*#__PURE__*/function (PrinterStyleValue) {
  PrinterStyleValue[PrinterStyleValue["ENABLE"] = 1] = "ENABLE";
  PrinterStyleValue[PrinterStyleValue["DISABLE"] = 2] = "DISABLE";
  return PrinterStyleValue;
}({});
let AlignValue = exports.AlignValue = /*#__PURE__*/function (AlignValue) {
  AlignValue[AlignValue["LEFT"] = 0] = "LEFT";
  AlignValue[AlignValue["CENTER"] = 1] = "CENTER";
  AlignValue[AlignValue["RIGHT"] = 2] = "RIGHT";
  return AlignValue;
}({});
const SunmiScan = exports.SunmiScan = effectiveSunmiScanModule;
var _default = exports.default = effectiveSunmiPrinterModule;
//# sourceMappingURL=index.js.map