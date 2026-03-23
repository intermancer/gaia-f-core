/**
 * Base URL for all API requests.
 *
 * Using a relative path ensures requests are resolved against the current
 * host, allowing the Nginx reverse proxy to forward them to the backend
 * without any hardcoded host or port in the frontend code.
 */
export const API_BASE = '/gaia-f';
