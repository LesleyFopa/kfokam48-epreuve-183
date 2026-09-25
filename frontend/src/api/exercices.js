import { apiGet, apiPost, apiPut } from "./client";

export const deposerExercice = (sessionId, etudiantId, lien) =>
  apiPost("/api/exercices", { sessionId, etudiantId, lien });

export const consulterExercices = (etudiantId) => apiGet(`/api/exercices?etudiantId=${etudiantId}`);

export const remplacerLienExercice = (exerciceId, lien) => apiPut(`/api/exercices/${exerciceId}`, { lien });
