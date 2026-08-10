import { type App, getApps, initializeApp } from "firebase-admin/app";

let _app: App | undefined;

export function getAdminApp(): App {
  if (!_app) {
    _app = getApps().length > 0 ? getApps()[0]! : initializeApp();
  }
  return _app;
}
