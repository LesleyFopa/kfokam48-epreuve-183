import { useEffect, useState } from "react";

function readColors() {
  const style = getComputedStyle(document.documentElement);
  const v = (name) => style.getPropertyValue(name).trim();
  return {
    accent: v("--accent"),
    accent2: v("--accent2"),
    grid: v("--grid"),
    tick: v("--tick"),
    danger: v("--danger"),
  };
}

/** Recalcule les couleurs des graphiques Chart.js quand le thème clair/sombre change. */
export function useThemeColors() {
  const [colors, setColors] = useState(readColors);

  useEffect(() => {
    const observer = new MutationObserver(() => setColors(readColors()));
    observer.observe(document.documentElement, { attributes: true, attributeFilter: ["data-theme"] });
    return () => observer.disconnect();
  }, []);

  return colors;
}
