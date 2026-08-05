import { FEATURE_FLAGS, FEATURE_FLAG_DEFAULTS } from "./constants";

export type FeatureFlagKey = (typeof FEATURE_FLAGS)[keyof typeof FEATURE_FLAGS];

export { FEATURE_FLAGS, FEATURE_FLAG_DEFAULTS };
