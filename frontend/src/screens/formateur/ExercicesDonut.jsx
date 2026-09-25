import { useMemo } from "react";
import { Doughnut } from "react-chartjs-2";
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from "chart.js";
import { useThemeColors } from "./useThemeColors";

ChartJS.register(ArcElement, Tooltip, Legend);

/**
 * Répartition dérivée des lignes déjà renvoyées par GET /api/tableau : aucune
 * moyenne n'est recalculée (F3), on ne fait que regrouper des valeurs fournies.
 */
export default function ExercicesDonut({ tableau }) {
  const colors = useThemeColors();

  const counts = useMemo(() => {
    let relu = 0;
    let enAttente = 0;
    let nonDepose = 0;
    for (const l of tableau) {
      if (l.exercicesDeposes === 0) nonDepose += 1;
      else if (l.moyenne != null) relu += 1;
      else enAttente += 1;
    }
    return { relu, enAttente, nonDepose };
  }, [tableau]);

  const data = useMemo(
    () => ({
      labels: ["Relu", "En attente", "Non déposé"],
      datasets: [
        {
          data: [counts.relu, counts.enAttente, counts.nonDepose],
          backgroundColor: [colors.accent, colors.danger, colors.grid],
          borderWidth: 0,
        },
      ],
    }),
    [counts, colors],
  );

  const options = useMemo(
    () => ({
      responsive: true,
      maintainAspectRatio: false,
      cutout: "68%",
      plugins: { legend: { position: "bottom", labels: { color: colors.tick, boxWidth: 10, font: { size: 11 } } } },
    }),
    [colors],
  );

  if (tableau.length === 0) {
    return <p className="muted">Pas encore de données.</p>;
  }

  return (
    <div style={{ height: 180 }}>
      <Doughnut data={data} options={options} />
    </div>
  );
}
