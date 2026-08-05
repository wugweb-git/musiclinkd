/**
 * Reads every environment variable this app needs, in one place.
 *
 * Static-export caveat: `output: 'export'` means there is no server at request
 * time, so every `NEXT_PUBLIC_*` value here is baked into the JS bundle at
 * `npm run build` time, not read fresh per-request. Changing `.env.local`
 * requires a rebuild + redeploy, not just a page reload.
 *
 * Deliberately does NOT throw when a required value is missing — Next still
 * prerenders every page once during `next build` even for a fully static
 * export, and a throw here would crash the build itself rather than fail
 * visibly in the running app. Missing config instead surfaces as
 * `isFirebaseConfigured === false`, which `app/layout.tsx` uses to render a
 * "Firebase isn't configured" screen instead of silently trying (and failing)
 * to connect with placeholder credentials.
 */

const PLACEHOLDER = "__NOT_CONFIGURED__";

function readEnv(value: string | undefined): string {
  return value && value.length > 0 ? value : PLACEHOLDER;
}

const firebase = {
  apiKey: readEnv(process.env.NEXT_PUBLIC_FIREBASE_API_KEY),
  authDomain: readEnv(process.env.NEXT_PUBLIC_FIREBASE_AUTH_DOMAIN),
  projectId: readEnv(process.env.NEXT_PUBLIC_FIREBASE_PROJECT_ID),
  storageBucket: readEnv(process.env.NEXT_PUBLIC_FIREBASE_STORAGE_BUCKET),
  messagingSenderId: readEnv(process.env.NEXT_PUBLIC_FIREBASE_MESSAGING_SENDER_ID),
  appId: readEnv(process.env.NEXT_PUBLIC_FIREBASE_APP_ID),
  // Optional — Analytics is feature-detected and skipped if absent, no placeholder needed.
  measurementId: process.env.NEXT_PUBLIC_FIREBASE_MEASUREMENT_ID,
};

export const isFirebaseConfigured = [
  firebase.apiKey,
  firebase.authDomain,
  firebase.projectId,
  firebase.storageBucket,
  firebase.messagingSenderId,
  firebase.appId,
].every((v) => v !== PLACEHOLDER);

export const env = {
  firebase,
  // Optional — App Check stays inert (see services/app-check.ts) until this is set.
  appCheckSiteKey: process.env.NEXT_PUBLIC_APP_CHECK_SITE_KEY,
  urls: {
    website: process.env.NEXT_PUBLIC_WEBSITE_URL ?? "https://musiclinkd.com",
    admin: process.env.NEXT_PUBLIC_ADMIN_URL ?? "https://admin.musiclinkd.com",
    app: process.env.NEXT_PUBLIC_APP_URL ?? "https://app.musiclinkd.com",
  },
  supportEmail: process.env.NEXT_PUBLIC_SUPPORT_EMAIL ?? "hello@musiclinkd.com",
};
