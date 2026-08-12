import { clsx, type ClassValue } from "clsx"
import { twMerge } from "tailwind-merge"

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

/** hourlyRate is a per-hour INR equivalent; 0 means unset (e.g. a label with no personal booking rate). */
export function formatHourlyRate(hourlyRate: number): string {
  if (!hourlyRate) return "—";
  return `₹${hourlyRate.toLocaleString("en-IN")}/hr`;
}
