import { type RemoteConfig, fetchAndActivate, getRemoteConfig, getValue } from "firebase/remote-config";
import { FEATURE_FLAG_DEFAULTS } from "@/config/constants";
import { getFirebaseApp } from "./firebase";

let _remoteConfig: RemoteConfig | undefined;

function getRemoteConfigInstance(): RemoteConfig | null {
  if (typeof window === "undefined") return null;
  if (!_remoteConfig) {
    _remoteConfig = getRemoteConfig(getFirebaseApp());
    _remoteConfig.defaultConfig = FEATURE_FLAG_DEFAULTS;
    _remoteConfig.settings.minimumFetchIntervalMillis = 5 * 60 * 1000;
  }
  return _remoteConfig;
}

export async function activateRemoteConfig(): Promise<void> {
  const rc = getRemoteConfigInstance();
  if (rc) await fetchAndActivate(rc);
}

export function getFlagValue(key: string): string {
  const rc = getRemoteConfigInstance();
  if (!rc) return String(FEATURE_FLAG_DEFAULTS[key as keyof typeof FEATURE_FLAG_DEFAULTS] ?? "");
  return getValue(rc, key).asString();
}

export function getFlagBoolean(key: string): boolean {
  const rc = getRemoteConfigInstance();
  if (!rc) return Boolean(FEATURE_FLAG_DEFAULTS[key as keyof typeof FEATURE_FLAG_DEFAULTS]);
  return getValue(rc, key).asBoolean();
}
