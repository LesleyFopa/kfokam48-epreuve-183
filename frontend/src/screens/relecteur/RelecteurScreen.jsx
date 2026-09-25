import { useCallback, useEffect, useState } from "react";
import IdentitySelect from "../../components/IdentitySelect";
import ErrorBanner from "../../components/ErrorBanner";
import SuccessBanner from "../../components/SuccessBanner";
import Spinner from "../../components/Spinner";
import { useEtudiantIdentity } from "../../hooks/useEtudiantIdentity";
import { getRelecturesAssignees, noterRelecture, corrigerRelecture } from "../../api/relectures";
import { isNetworkError } from "../../api/client";

export default function RelecteurScreen() {
  const { etudiants, etudiantId, setEtudiantId, loading: loadingEtudiants } = useEtudiantIdentity();

  const [relectures, setRelectures] = useState([]);
  const [listLoading, setListLoading] = useState(false);
  const [listError, setListError] = useState(null);

  const [selectedId, setSelectedId] = useState("");
  const [note, setNote] = useState("");
  const [commentaire, setCommentaire] = useState("");
  const [formState, setFormState] = useState({ loading: false, error: null, success: null });

  const rafraichir = useCallback(() => {
    if (!etudiantId) return;
    setListLoading(true);
    setListError(null);
    getRelecturesAssignees(etudiantId)
      .then(setRelectures)
      .catch((err) => {
        if (!isNetworkError(err)) setListError(err);
      })
      .finally(() => setListLoading(false));
  }, [etudiantId]);

  useEffect(() => {
    rafraichir();
    setSelectedId("");
  }, [rafraichir]);

  const relectureSelectionnee = relectures.find((r) => r.id === selectedId);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setFormState({ loading: true, error: null, success: null });
    try {
      const dejaRendue = relectureSelectionnee?.statut === "RENDUE";
      if (dejaRendue) {
        await corrigerRelecture(selectedId, Number(note), commentaire.trim());
      } else {
        await noterRelecture(selectedId, Number(note), commentaire.trim());
      }
      setFormState({
        loading: false,
        error: null,
        success: dejaRendue ? "Note corrigée." : "Relecture envoyée.",
      });
      setNote("");
      setCommentaire("");
      setSelectedId("");
      rafraichir();
    } catch (err) {
      setFormState({ loading: false, error: err, success: null });
    }
  };

  return (
    <div>
      <div className="pagehead">
        <h1>Espace relecteur</h1>
        <p>Consulte les exercices qui te sont assignés et note ceux en attente.</p>
      </div>

      <IdentitySelect
        etudiants={etudiants}
        etudiantId={etudiantId}
        onChange={setEtudiantId}
        loading={loadingEtudiants}
      />

      <div className="card" style={{ marginBottom: 14 }}>
        <div className="cardtop">
          <div className="cardlabel" style={{ marginBottom: 0 }}>
            Mes relectures assignées
          </div>
          <button type="button" className="btn secondary" onClick={rafraichir} disabled={!etudiantId}>
            Actualiser
          </button>
        </div>
        <ErrorBanner error={listError} />
        {listLoading ? (
          <Spinner />
        ) : !etudiantId ? (
          <p className="muted">Choisis ton nom pour voir tes relectures.</p>
        ) : relectures.length === 0 ? (
          <div className="empty">Aucune relecture assignée pour l'instant.</div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>Exercice</th>
                <th>Statut</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {relectures.map((r) => (
                <tr key={r.id}>
                  <td>
                    <a href={r.lienExercice} target="_blank" rel="noreferrer">
                      {r.lienExercice}
                    </a>
                  </td>
                  <td>
                    <span
                      className="tag"
                      style={{
                        background: r.statut === "RENDUE" ? "var(--success-bg)" : "var(--danger-bg)",
                        color: r.statut === "RENDUE" ? "var(--success)" : "var(--danger)",
                      }}
                    >
                      {r.statut === "RENDUE" ? "Rendue" : "À faire"}
                    </span>
                  </td>
                  <td>
                    <button type="button" className="btn secondary" onClick={() => setSelectedId(r.id)}>
                      {r.statut === "RENDUE" ? "Corriger" : "Relire"}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {relectureSelectionnee && (
        <div className="card">
          <div className="cardlabel">
            {relectureSelectionnee.statut === "RENDUE" ? "Corriger la note" : "Noter l'exercice"}
          </div>
          <p className="muted" style={{ marginTop: -4, marginBottom: 10 }}>
            {relectureSelectionnee.lienExercice}
          </p>
          <ErrorBanner error={formState.error} />
          <SuccessBanner message={formState.success} />
          <form onSubmit={handleSubmit}>
            <div className="field">
              <label htmlFor="note">Note (0 à 20, entier)</label>
              <input
                id="note"
                type="number"
                min={0}
                max={20}
                step={1}
                value={note}
                onChange={(e) => setNote(e.target.value)}
                required
              />
            </div>
            <div className="field">
              <label htmlFor="commentaire">Commentaire</label>
              <textarea
                id="commentaire"
                rows={3}
                value={commentaire}
                onChange={(e) => setCommentaire(e.target.value)}
                required
              />
            </div>
            <div style={{ display: "flex", gap: 8 }}>
              <button type="submit" className="btn" disabled={formState.loading}>
                {formState.loading ? (
                  <Spinner />
                ) : relectureSelectionnee.statut === "RENDUE" ? (
                  "Corriger la note"
                ) : (
                  "Envoyer la relecture"
                )}
              </button>
              <button type="button" className="btn secondary" onClick={() => setSelectedId("")}>
                Annuler
              </button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
}
