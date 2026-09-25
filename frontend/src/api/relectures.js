import { apiGet, apiPost, apiPut } from "./client";

export const getRelecturesAssignees = (relecteurId) => apiGet(`/api/relectures?relecteurId=${relecteurId}`);

export const noterRelecture = (relectureId, note, commentaire) =>
  apiPost(`/api/relectures/${relectureId}`, { note, commentaire });

export const corrigerRelecture = (relectureId, note, commentaire) =>
  apiPut(`/api/relectures/${relectureId}`, { note, commentaire });
