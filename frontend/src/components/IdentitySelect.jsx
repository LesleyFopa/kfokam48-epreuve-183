export default function IdentitySelect({ etudiants, etudiantId, onChange, loading }) {
  return (
    <div className="card" style={{ marginBottom: 14 }}>
      <div className="cardlabel">Qui es-tu ?</div>
      {loading ? (
        <p className="muted">Chargement de la liste des étudiants…</p>
      ) : (
        <div className="field" style={{ marginBottom: 0 }}>
          <select value={etudiantId} onChange={(e) => onChange(Number(e.target.value) || "")}>
            <option value="">— Choisis ton nom —</option>
            {etudiants.map((e) => (
              <option key={e.id} value={e.id}>
                {e.nom}
              </option>
            ))}
          </select>
        </div>
      )}
    </div>
  );
}
