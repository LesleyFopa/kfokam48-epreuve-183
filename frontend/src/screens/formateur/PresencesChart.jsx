import { useMemo } from "react";
import { Bar } from "react-chartjs-2";
import { Chart as ChartJS, BarElement, CategoryScale, LinearScale, Tooltip } from "chart.js";
import { useThemeColors } from "./useThemeColors";

ChartJS.register(BarElement, CategoryScale, LinearScale, Tooltip);

export default function PresencesChart({ tableau }) {
  const colors = useThemeColors();

  const data = useMemo(
    () => ({
      labels: tableau.map((l) => l.nom.split(" ")[0]),
      datasets: [
        {
          data: tableau.map((l) => l.presences),
          backgroundColor: colors.accent,
          borderRadius: 6,
          maxBarThickness: 28,
        },
      ],
    }),
    [tableau, colors],
  );

  const options = useMemo(
    () => ({
      responsive: true,
      maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: {
        x: { grid: { display: false }, ticks: { color: colors.tick } },
        y: { beginAtZero: true, ticks: { color: colors.tick, precision: 0 }, grid: { color: colors.grid } },
      },
    }),
    [colors],
  );

  if (tableau.length === 0) {
    return <p className="muted">Pas encore de données.</p>;
  }

  return (
    <div style={{ height: 180 }}>
      <Bar data={data} options={options} />
    </div>
  );
}
