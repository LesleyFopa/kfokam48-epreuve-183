const PALETTE = [
  { bg: "var(--iconbadgebg)", fg: "var(--accent)" },
  { bg: "var(--iconbadgebg2)", fg: "var(--accent2)" },
  { bg: "var(--iconbadgebg3)", fg: "var(--accent4)" },
];

function initiales(nom) {
  const mots = nom.trim().split(/\s+/).slice(0, 2);
  return mots.map((m) => m[0]?.toUpperCase() ?? "").join("");
}

function hash(nom) {
  let h = 0;
  for (let i = 0; i < nom.length; i++) h = (h * 31 + nom.charCodeAt(i)) >>> 0;
  return h;
}

export default function Avatar({ nom }) {
  const colors = PALETTE[hash(nom) % PALETTE.length];
  return (
    <span className="avatar" aria-hidden="true" style={{ background: colors.bg, color: colors.fg }}>
      {initiales(nom)}
    </span>
  );
}
