import { apiPost } from "./client";

export const marquerPresence = (code, etudiantId) => apiPost("/api/presences", { code, etudiantId });
