declare module 'react-native' {
  export interface TurboModule {}
  interface TurboModuleRegistryStatic {
    get<T>(name: string): T | null;
    getEnforcing<T>(name: string): T;
  }

  export const TurboModuleRegistry: TurboModuleRegistryStatic;
  export const NativeModules: Record<string, unknown>;
}
