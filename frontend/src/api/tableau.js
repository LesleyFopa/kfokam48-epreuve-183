import { apiGet } from "./client";

export const getTableau = (promotionId) => apiGet(`/api/tableau?promotionId=${promotionId}`);
