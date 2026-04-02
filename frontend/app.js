// ============================================================
// CONFIG
// ============================================================
// In Docker: nginx proxies /api --> backend:8080/api, so use relative path.
// For local dev without Docker: set window.API_BASE = 'http://localhost:8080/api' before this script.
const API = window.API_BASE || '/api';

// Current session state
const session = {
  role: 'client',
  page: 'dashboard',
  clientId: 'cl001',
  consultantId: 'c001',
};