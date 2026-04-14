import type { TurboModule } from 'react-native';
export interface Spec extends TurboModule {
    scan(): Promise<void>;
    startScanner(): Promise<string>;
}
declare const _default: Spec;
export default _default;
