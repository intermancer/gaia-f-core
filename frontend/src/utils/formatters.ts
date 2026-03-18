/**
 * Utility functions for formatting data in the UI.
 */

/**
 * Formats an ISO 8601 timestamp to "YYYY-MM-DD HH:MM:SS" format.
 * @param isoString - ISO 8601 date string from server
 * @returns Formatted date string
 */
export function formatDateTime(isoString: string): string {
  const date = new Date(isoString);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}

/**
 * Truncates a UUID to show first 8 and last 6 characters with ellipsis.
 * Example: "550e8400-e29b-41d4-a716-446655440000" -> "550e8400...440000"
 * @param uuid - Full UUID string
 * @returns Truncated UUID string
 */
export function truncateUuid(uuid: string): string {
  if (uuid.length <= 17) return uuid;
  return `${uuid.substring(0, 8)}...${uuid.substring(uuid.length - 6)}`;
}
