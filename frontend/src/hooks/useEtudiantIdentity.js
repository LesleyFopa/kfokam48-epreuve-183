import { useEffect, useState } from "react";
import { getEtudiants } from "../api/promotions";
import { PROMOTION_ID } from "../config";

const STORAGE_KEY = "kfokam48-etudiant-id";

/** Q1, EF12 : pas de mot de passe, l'étudiant se choisit dans une liste. */
export function useEtudiantIdentity() {
  const [etudiants, setEtudiants] = useState([]);
  const [etudiantId, setEtudiantIdState] = useState(() => {
    const stored = localStorage.getItem(STORAGE_KEY);
    return stored ? Number(stored) : "";
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    getEtudiants(PROMOTION_ID)
      .then(setEtudiants)
      .catch(setError)
      .finally(() => setLoading(false));
  }, []);

  const setEtudiantId = (id) => {
    setEtudiantIdState(id);
    if (id) localStorage.setItem(STORAGE_KEY, String(id));
  };

  return { etudiants, etudiantId, setEtudiantId, loading, error };
}
