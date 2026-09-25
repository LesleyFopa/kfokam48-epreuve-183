import { useCallback, useEffect, useState } from "react";

const STORAGE_KEY = "kfokam48-theme";

function systemPrefersDark() {
  return window.matchMedia?.("(prefers-color-scheme: dark)").matches ?? false;
}

/**
 * Suit prefers-color-scheme par défaut ; un choix manuel (bouton) est
 * mémorisé en localStorage et prime sur la préférence système.
 */
export function useTheme() {
  const [manual, setManual] = useState(() => localStorage.getItem(STORAGE_KEY));
  const [resolved, setResolved] = useState(() => manual ?? (systemPrefersDark() ? "dark" : "light"));

  useEffect(() => {
    if (manual) {
      setResolved(manual);
      return undefined;
    }
    setResolved(systemPrefersDark() ? "dark" : "light");
    const media = window.matchMedia("(prefers-color-scheme: dark)");
    const onChange = (e) => setResolved(e.matches ? "dark" : "light");
    media.addEventListener("change", onChange);
    return () => media.removeEventListener("change", onChange);
  }, [manual]);

  useEffect(() => {
    document.documentElement.setAttribute("data-theme", resolved);
  }, [resolved]);

  const toggle = useCallback(() => {
    const next = resolved === "dark" ? "light" : "dark";
    localStorage.setItem(STORAGE_KEY, next);
    setManual(next);
  }, [resolved]);

  return { theme: resolved, toggle };
}
