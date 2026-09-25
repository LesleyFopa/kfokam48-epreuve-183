const BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

/**
 * Point d'entrée unique pour tous les appels API (F3 : pas de fetch dispersé).
 * Lève une ApiError avec { code, message } tels que renvoyés par le backend
 * dans tous les cas d'erreur, pour un traitement uniforme par les écrans.
 */
export class ApiError extends Error {
  constructor(code, message, status) {
    super(message);
    this.code = code;
    this.status = status;
  }
}

async function request(path, options = {}) {
  const response = await fetch(`${BASE_URL}${path}`, {
    headers: { "Content-Type": "application/json" },
    ...options,
  });

  if (response.status === 204) {
    return null;
  }

  const body = await response.json().catch(() => null);

  if (!response.ok) {
    const code = body?.code ?? "ERREUR_INCONNUE";
    const message = body?.message ?? "Une erreur est survenue.";
    throw new ApiError(code, message, response.status);
  }

  return body;
}

export const apiGet = (path) => request(path, { method: "GET" });
export const apiPost = (path, data) => request(path, { method: "POST", body: JSON.stringify(data) });
export const apiPut = (path, data) => request(path, { method: "PUT", body: JSON.stringify(data) });
export const apiPatch = (path) => request(path, { method: "PATCH" });
