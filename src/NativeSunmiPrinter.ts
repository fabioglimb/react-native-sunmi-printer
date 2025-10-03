import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  printerInit(): void;
  printerSelfChecking(): void;
  getPrinterSerialNo(): Promise<string>;
  getPrinterVersion(): Promise<string>;
  getServiceVersion(): Promise<string>;
  getPrinterModal(): Promise<string>;
  getPrinterPaper(): Promise<string>;
  getPrintedLength(): void;
  updatePrinterState(): Promise<number>;
  hasPrinter(): Promise<boolean>;
  sendRAWData(data: string): void;
  setPrinterStyle(key: number, value: number): void;
  setAlignment(alignment: number): void;
  setFontName(typeface: string): void;
  setFontSize(fontSize: number): void;
  setFontWeight(isWeight: boolean): void;
  printerText(text: string): void;
  printTextWithFont(text: string, typeface: string, fontSize: number): void;
  printOriginalText(text: string): void;
  printColumnsText(
    texts: ReadonlyArray<string>,
    widths: ReadonlyArray<number>,
    aligns: ReadonlyArray<number>
  ): void;
  printColumnsString(
    texts: ReadonlyArray<string>,
    widths: ReadonlyArray<number>,
    aligns: ReadonlyArray<number>
  ): void;
  printBitmap(encodedString: string, pixelWidth: number): void;
  printBitmapCustom(
    encodedString: string,
    pixelWidth: number,
    type: number
  ): void;
  printBitmapBase64Custom(
    encodedString: string,
    pixelWidth: number,
    type: number
  ): void;
  printBarCode(
    data: string,
    symbology: number,
    height: number,
    width: number,
    textPosition: number
  ): void;
  printQRCode(data: string, modulesize: number, errorlevel: number): void;
  print2DCode(
    data: string,
    symbology: number,
    modulesize: number,
    errorlevel: number
  ): void;
  commitPrint(transactions: ReadonlyArray<Record<string, unknown>>): void;
  enterPrinterBuffer(clear: boolean): void;
  exitPrinterBuffer(commit: boolean): void;
  commitPrinterBuffer(): void;
  commitPrinterBufferWithCallbacka(): void;
  lineWrap(lines: number): void;
  cutPaper(): void;
  openDrawer(): void;
  getDrawerStatus(): Promise<number>;
  getCutPaperTimes(): Promise<number>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('SunmiPrinter');
