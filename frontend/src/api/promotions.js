import { apiGet } from "./client";

export const getEtudiants = (promotionId) => apiGet(`/api/promotions/${promotionId}/etudiants`);
