import { apiGet, apiPatch, apiPost } from "./client";

export const ouvrirSession = (titre, promotionId) => apiPost("/api/sessions", { titre, promotionId });

export const getSessionActive = (promotionId) => apiGet(`/api/sessions/active?promotionId=${promotionId}`);

export const ajouterPresenceManuelle = (sessionId, etudiantId) =>
  apiPost(`/api/sessions/${sessionId}/presences`, { etudiantId });

export const cloturerSession = (sessionId) => apiPatch(`/api/sessions/${sessionId}/cloturer`);
