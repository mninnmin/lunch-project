const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL || "").replace(/\/$/, "");

const TOKEN_KEY = "lunch-pick-token";

export function getToken() { return localStorage.getItem(TOKEN_KEY); }
export function saveToken(token) { localStorage.setItem(TOKEN_KEY, token); }
export function clearToken() { localStorage.removeItem(TOKEN_KEY); }

async function api(path, options = {}) {
  const token = getToken();
  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers: { "Content-Type": "application/json", ...(token ? { Authorization: `Bearer ${token}` } : {}), ...options.headers }
  });
  if (!response.ok) {
    const body = await response.json().catch(() => ({}));
    throw new Error(body.error || "요청을 처리하지 못했어요.");
  }
  return response.status === 204 ? null : response.json();
}

export async function requestRecommendation(preferences) {
  const controller = new AbortController();
  const timeout = window.setTimeout(() => controller.abort(), 12000);

  try {
    const response = await fetch(`${API_BASE_URL}/api/recommendations`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(preferences),
      signal: controller.signal
    });
    if (!response.ok) throw new Error("추천 서버가 잠시 쉬고 있어요.");
    return await response.json();
  } finally {
    window.clearTimeout(timeout);
  }
}

export const signup = (form) => api("/api/auth/signup", { method: "POST", body: JSON.stringify(form) });
export const login = (form) => api("/api/auth/login", { method: "POST", body: JSON.stringify(form) });
export const getMe = () => api("/api/auth/me");
export const getFavorites = () => api("/api/favorites");
export const addFavorite = (menuId) => api(`/api/favorites/${menuId}`, { method: "POST" });
export const removeFavorite = (menuId) => api(`/api/favorites/${menuId}`, { method: "DELETE" });
