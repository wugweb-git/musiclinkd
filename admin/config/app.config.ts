import { env } from "./environment";

export const appConfig = {
  name: "MusicLinkd Operations",
  shortName: "MusicLinkd Ops",
  supportEmail: env.supportEmail,
  urls: env.urls,
  /** Firebase project this app talks to — single project for now, see Build_And_Environment_Guide.md. */
  firebaseProjectId: "musiclinkd-7ae30",
} as const;
