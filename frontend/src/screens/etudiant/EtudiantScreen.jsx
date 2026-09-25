import { useCallback, useEffect, useState } from "react";
import IdentitySelect from "../../components/IdentitySelect";
import ErrorBanner from "../../components/ErrorBanner";
import SuccessBanner from "../../components/SuccessBanner";
import Spinner from "../../components/Spinner";
import { useEtudiantIdentity } from "../../hooks/useEtudiantIdentity";
import { marquerPresence } from "../../api/presences";
import { deposerExercice, consulterExercices, remplacerLienExercice } from "../../api/exercices";
import { isNetworkError } from "../../api/client";

const STATUT_LABEL = {
  DEPOSE: "Déposé",
  EN_ATTENTE: "En attente de relecture",
  PROVISOIRE: "Note provisoire",
  RELU: "Relu",
};

const STATUT_COLOR = {
  DEPOSE: { bg: "var(--iconbadgebg)", fg: "var(--accent)" },
  EN_ATTENTE: { bg: "var(--danger-bg)", fg: "var(--danger)" },
  PROVISOIRE: { bg: "var(--iconbadgebg3)", fg: "var(--accent4)" },
  RELU: { bg: "var(--success-bg)", fg: "var(--success)" },
};

// RG12 : remplaçable tant qu'aucune relecture n'a été rendue.
const STATUTS_REMPLACABLES = new Set(["DEPOSE", "EN_ATTENTE"]);

export default function EtudiantScreen() {
  const { etudiants, etudiantId, setEtudiantId, loading: loadingEtudiants } = useEtudiantIdentity();

  const [code, setCode] = useState("");
  const [presenceState, setPresenceState] = useState({ loading: false, error: null, success: null });

  const [sessionId, setSessionId] = useState("");
  const [lien, setLien] = useState("");
  const [depotState, setDepotState] = useState({ loading: false, error: null, success: null });

  const [exercices, setExercices] = useState([]);
  const [exercicesError, setExercicesError] = useState(null);
  const [exercicesLoading, setExercicesLoading] = useState(false);

  const [remplacementId, setRemplacementId] = useState(null);
  const [nouveauLien, setNouveauLien] = useState("");
  const [remplacementState, setRemplacementState] = useState({ loading: false, error: null });

  const rafraichirExercices = useCallback(() => {
    if (!etudiantId) return;
    setExercicesLoading(true);
    setExercicesError(null);
    consulterExercices(etudiantId)
      .then(setExercices)
      .catch((err) => {
        if (!isNetworkError(err)) setExercicesError(err);
      })
      .finally(() => setExercicesLoading(false));
  }, [etudiantId]);

  useEffect(() => {
    rafraichirExercices();
  }, [rafraichirExercices]);

  const handleMarquerPresence = async (e) => {
    e.preventDefault();
    if (!etudiantId) {
      setPresenceState({ loading: false, error: { message: "Choisis d'abord ton nom." }, success: null });
      return;
    }
    setPresenceState({ loading: true, error: null, success: null });
    try {
      await marquerPresence(code.trim(), etudiantId);
      setPresenceState({ loading: false, error: null, success: "Présence enregistrée." });
      setCode("");
    } catch (err) {
      setPresenceState({ loading: false, error: err, success: null });
    }
  };

  const handleDeposer = async (e) => {
    e.preventDefault();
    if (!etudiantId) {
      setDepotState({ loading: false, error: { message: "Choisis d'abord ton nom." }, success: null });
      return;
    }
    setDepotState({ loading: true, error: null, success: null });
    try {
      await deposerExercice(Number(sessionId), etudiantId, lien.trim());
      setDepotState({ loading: false, error: null, success: "Exercice déposé." });
      setLien("");
      rafraichirExercices();
    } catch (err) {
      setDepotState({ loading: false, error: err, success: null });
    }
  };

  const handleRemplacerLien = async (exerciceId) => {
    setRemplacementState({ loading: true, error: null });
    try {
      await remplacerLienExercice(exerciceId, nouveauLien.trim());
      setRemplacementState({ loading: false, error: null });
      setRemplacementId(null);
      setNouveauLien("");
      rafraichirExercices();
    } catch (err) {
      setRemplacementState({ loading: false, error: err });
    }
  };

  return (
    <div>
      <div className="pagehead">
        <h1>Espace étudiant</h1>
        <p>Marque ta présence, dépose tes exercices et suis leur relecture.</p>
      </div>

      <IdentitySelect
        etudiants={etudiants}
        etudiantId={etudiantId}
        onChange={setEtudiantId}
        loading={loadingEtudiants}
      />

      <div className="grid2">
        <div className="card">
          <div className="cardlabel">Marquer ma présence</div>
          <ErrorBanner error={presenceState.error} />
          <SuccessBanner message={presenceState.success} />
          <form onSubmit={handleMarquerPresence}>
            <div className="field">
              <label htmlFor="code">Code de session</label>
              <input
                id="code"
                value={code}
                onChange={(e) => setCode(e.target.value)}
                placeholder="ex. 4F82"
                maxLength={10}
                required
              />
            </div>
            <button type="submit" className="btn" disabled={presenceState.loading}>
              {presenceState.loading ? <Spinner /> : "Valider ma présence"}
            </button>
          </form>
        </div>

        <div className="card">
          <div className="cardlabel">Déposer mon exercice</div>
          <p className="muted" style={{ marginTop: -4, marginBottom: 10 }}>
            L'identifiant de session t'est communiqué par le formateur avec le code.
          </p>
          <ErrorBanner error={depotState.error} />
          <SuccessBanner message={depotState.success} />
          <form onSubmit={handleDeposer}>
            <div className="field">
              <label htmlFor="sessionId">Identifiant de session</label>
              <input
                id="sessionId"
                type="number"
                value={sessionId}
                onChange={(e) => setSessionId(e.target.value)}
                required
              />
            </div>
            <div className="field">
              <label htmlFor="lien">Lien de l'exercice</label>
              <input
                id="lien"
                type="url"
                value={lien}
                onChange={(e) => setLien(e.target.value)}
                placeholder="https://github.com/..."
                required
              />
            </div>
            <button type="submit" className="btn" disabled={depotState.loading}>
              {depotState.loading ? <Spinner /> : "Déposer"}
            </button>
          </form>
        </div>
      </div>

      <div className="card">
        <div className="cardtop">
          <div className="cardlabel" style={{ marginBottom: 0 }}>
            Mes exercices
          </div>
          <button type="button" className="btn secondary" onClick={rafraichirExercices} disabled={!etudiantId}>
            Actualiser
          </button>
        </div>
        <ErrorBanner error={exercicesError} />
        <ErrorBanner error={remplacementState.error} />
        {exercicesLoading ? (
          <Spinner />
        ) : !etudiantId ? (
          <p className="muted">Choisis ton nom pour voir tes exercices.</p>
        ) : exercices.length === 0 ? (
          <div className="empty">Aucun exercice déposé pour l'instant.</div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>Lien</th>
                <th>Statut</th>
                <th>Note retenue</th>
                <th>Commentaires reçus</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {exercices.map((ex) => {
                const color = STATUT_COLOR[ex.statut] ?? STATUT_COLOR.DEPOSE;
                const commentaires = (ex.relectures ?? []).filter((r) => r.statut === "RENDUE");
                return (
                  <tr key={ex.id}>
                    <td>
                      <a href={ex.lien} target="_blank" rel="noreferrer">
                        {ex.lien}
                      </a>
                    </td>
                    <td>
                      <span className="tag" style={{ background: color.bg, color: color.fg }}>
                        {STATUT_LABEL[ex.statut] ?? ex.statut}
                      </span>
                    </td>
                    <td>
                      {ex.noteRetenue ?? "—"}
                      {ex.provisoire && (
                        <span className="muted" style={{ display: "block", fontSize: 11 }}>
                          en attente du second avis
                        </span>
                      )}
                    </td>
                    <td>
                      {commentaires.length === 0 ? (
                        "—"
                      ) : (
                        <ul style={{ margin: 0, paddingLeft: 16 }}>
                          {commentaires.map((r, i) => (
                            <li key={i}>
                              {r.note} — {r.commentaire}
                            </li>
                          ))}
                        </ul>
                      )}
                    </td>
                    <td>
                      {STATUTS_REMPLACABLES.has(ex.statut) &&
                        (remplacementId === ex.id ? (
                          <div style={{ display: "flex", gap: 6, alignItems: "center", minWidth: 220 }}>
                            <input
                              type="url"
                              value={nouveauLien}
                              onChange={(e) => setNouveauLien(e.target.value)}
                              placeholder="Nouveau lien"
                              style={{ width: 160 }}
                            />
                            <button
                              type="button"
                              className="btn secondary"
                              disabled={remplacementState.loading || !nouveauLien.trim()}
                              onClick={() => handleRemplacerLien(ex.id)}
                            >
                              {remplacementState.loading ? <Spinner /> : "Valider"}
                            </button>
                            <button
                              type="button"
                              className="btn secondary"
                              onClick={() => {
                                setRemplacementId(null);
                                setNouveauLien("");
                                setRemplacementState({ loading: false, error: null });
                              }}
                            >
                              Annuler
                            </button>
                          </div>
                        ) : (
                          <button
                            type="button"
                            className="btn secondary"
                            onClick={() => {
                              setRemplacementId(ex.id);
                              setNouveauLien(ex.lien);
                            }}
                          >
                            Remplacer le lien
                          </button>
                        ))}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
