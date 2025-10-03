import 'react-native';

declare module 'react-native' {
  interface TurboModule {}
  interface TurboModuleRegistryStatic {
    get<T>(name: string): T | null;
    getEnforcing<T>(name: string): T;
  }

  const TurboModuleRegistry: TurboModuleRegistryStatic;
}
