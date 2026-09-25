import { apiGet, apiPost } from "./client";

export const getRelecturesAssignees = (relecteurId) => apiGet(`/api/relectures?relecteurId=${relecteurId}`);

export const noterRelecture = (relectureId, note, commentaire) =>
  apiPost(`/api/relectures/${relectureId}`, { note, commentaire });
