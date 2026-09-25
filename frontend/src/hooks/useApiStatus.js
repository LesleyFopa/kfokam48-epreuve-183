import { useEffect, useState } from "react";

/**
 * Suit la joignabilité du backend via les événements émis par api/client.js,
 * pour afficher un seul bandeau global plutôt qu'un "Failed to fetch" par carte.
 */
export function useApiStatus() {
  const [offline, setOffline] = useState(false);

  useEffect(() => {
    const onOffline = () => setOffline(true);
    const onOnline = () => setOffline(false);
    window.addEventListener("kfokam48:api-offline", onOffline);
    window.addEventListener("kfokam48:api-online", onOnline);
    return () => {
      window.removeEventListener("kfokam48:api-offline", onOffline);
      window.removeEventListener("kfokam48:api-online", onOnline);
    };
  }, []);

  return offline;
}
