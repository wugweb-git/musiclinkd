/**
 * Admin SDK bootstrap for local seed scripts. Mirrors
 * backend/functions/src/admin.ts's getAdminApp() pattern, but this runs
 * outside any Google Cloud environment (a developer's machine), so there's
 * no metadata-server ADC available. Instead of requiring a separate
 * `gcloud auth application-default login` (gcloud isn't installed on every
 * machine that has the Firebase CLI), this reuses the OAuth refresh token
 * the developer already has from `firebase login` — same account, same
 * granted consent — reshaped into the exact JSON format
 * `gcloud auth application-default login` itself would have produced, so
 * firebase-admin's normal applicationDefault() picks it up unmodified.
 */
import { existsSync, mkdtempSync, readFileSync, rmSync, writeFileSync } from "node:fs";
import { homedir, tmpdir } from "node:os";
import { join } from "node:path";
import { type App, applicationDefault, getApps, initializeApp } from "firebase-admin/app";

const PROJECT_ID = "musiclinkd-7ae30";

/**
 * The Firebase CLI's own OAuth "installed app" client — public by design
 * (it's embedded in every firebase-tools install and has no meaningful
 * secrecy; the actual security boundary is the refresh token, which never
 * leaves this machine). Sourced from firebase-tools' api.ts.
 */
const FIREBASE_CLI_OAUTH_CLIENT = {
  client_id: "563584335869-fgrhgmd47bqnekij5i8b5pr03ho849e6.apps.googleusercontent.com",
  client_secret: "j9iVZfS8kkCEFUPaAeJV0sAi",
};

function gcloudAdcAvailable(): boolean {
  if (process.env.GOOGLE_APPLICATION_CREDENTIALS) return true;
  const wellKnown = join(homedir(), ".config", "gcloud", "application_default_credentials.json");
  return existsSync(wellKnown);
}

/**
 * Bridges `firebase login`'s stored refresh token into a throwaway ADC file
 * so firebase-admin's applicationDefault() can use it. Returns a cleanup
 * function; caller must invoke it once the app is initialized (the
 * credential's refresh_token is cached in-process, so the file is only
 * needed for the instant applicationDefault() reads it).
 */
function bridgeFirebaseCliCredentials(): () => void {
  const configstorePath = join(homedir(), ".config", "configstore", "firebase-tools.json");
  if (!existsSync(configstorePath)) {
    throw new Error(
      `No Application Default Credentials found, and no Firebase CLI login at ${configstorePath}. ` +
        "Run `firebase login` first, or set GOOGLE_APPLICATION_CREDENTIALS to a service account key.",
    );
  }

  const raw = JSON.parse(readFileSync(configstorePath, "utf8"));
  const refreshToken = raw?.tokens?.refresh_token;
  if (!refreshToken) {
    throw new Error(`Found ${configstorePath} but it has no tokens.refresh_token — run \`firebase login\` again.`);
  }

  const adcJson = {
    type: "authorized_user",
    client_id: FIREBASE_CLI_OAUTH_CLIENT.client_id,
    client_secret: FIREBASE_CLI_OAUTH_CLIENT.client_secret,
    refresh_token: refreshToken,
  };

  const dir = mkdtempSync(join(tmpdir(), "musiclinkd-seed-adc-"));
  const filePath = join(dir, "adc.json");
  writeFileSync(filePath, JSON.stringify(adcJson), { mode: 0o600 });
  process.env.GOOGLE_APPLICATION_CREDENTIALS = filePath;

  return () => {
    try {
      rmSync(dir, { recursive: true, force: true });
    } catch {
      // best-effort cleanup of a tmpdir file; not worth failing the run over
    }
  };
}

let _app: App | undefined;

export function getAdminApp(): App {
  if (_app) return _app;
  if (getApps().length > 0) {
    _app = getApps()[0]!;
    return _app;
  }

  const usingBridge = !gcloudAdcAvailable();
  if (usingBridge) {
    // applicationDefault() reads GOOGLE_APPLICATION_CREDENTIALS lazily, on
    // the first real API call — not at initializeApp() time — so the temp
    // file must survive for the life of the process, not just this call.
    const cleanup = bridgeFirebaseCliCredentials();
    process.once("exit", cleanup);
  }

  _app = initializeApp({
    credential: applicationDefault(),
    projectId: PROJECT_ID,
  });

  return _app;
}
