import { useCallback, useEffect, useMemo, useState } from "react";
import ErrorBanner from "../../components/ErrorBanner";
import SuccessBanner from "../../components/SuccessBanner";
import Spinner from "../../components/Spinner";
import { PROMOTION_ID } from "../../config";
import { getEtudiants } from "../../api/promotions";
import { ouvrirSession, getSessionActive, ajouterPresenceManuelle, cloturerSession } from "../../api/sessions";
import { getTableau } from "../../api/tableau";
import { ApiError } from "../../api/client";
import PresencesChart from "./PresencesChart";
import ExercicesDonut from "./ExercicesDonut";

export default function FormateurScreen() {
  const [etudiants, setEtudiants] = useState([]);

  const [titre, setTitre] = useState("");
  const [session, setSession] = useState(null);
  const [sessionLookupDone, setSessionLookupDone] = useState(false);
  const [sessionState, setSessionState] = useState({ loading: false, error: null });

  const [presenceEtudiantId, setPresenceEtudiantId] = useState("");
  const [presenceState, setPresenceState] = useState({ loading: false, error: null, success: null });

  const [clotureState, setClotureState] = useState({ loading: false, error: null });

  const [tableau, setTableau] = useState([]);
  const [tableauLoading, setTableauLoading] = useState(true);
  const [tableauError, setTableauError] = useState(null);

  useEffect(() => {
    getEtudiants(PROMOTION_ID).then(setEtudiants).catch(() => {});
  }, []);

  // EF1/EF8 : retrouve la session déjà ouverte au chargement, plutôt que de
  // dépendre d'un état local perdu au moindre changement d'onglet ou d'appareil.
  useEffect(() => {
    getSessionActive(PROMOTION_ID)
      .then(setSession)
      .catch((err) => {
        if (!(err instanceof ApiError && err.status === 404)) {
          setSessionState({ loading: false, error: err });
        }
      })
      .finally(() => setSessionLookupDone(true));
  }, []);

  const rafraichirTableau = useCallback(() => {
    setTableauLoading(true);
    getTableau(PROMOTION_ID)
      .then(setTableau)
      .catch(setTableauError)
      .finally(() => setTableauLoading(false));
  }, []);

  useEffect(() => {
    rafraichirTableau();
  }, [rafraichirTableau]);

  const handleOuvrir = async (e) => {
    e.preventDefault();
    setSessionState({ loading: true, error: null });
    try {
      const s = await ouvrirSession(titre.trim(), PROMOTION_ID);
      setSession(s);
      setTitre("");
      setSessionState({ loading: false, error: null });
    } catch (err) {
      setSessionState({ loading: false, error: err });
    }
  };

  const handlePresenceManuelle = async (e) => {
    e.preventDefault();
    setPresenceState({ loading: true, error: null, success: null });
    try {
      await ajouterPresenceManuelle(session.id, Number(presenceEtudiantId));
      setPresenceState({ loading: false, error: null, success: "Présence ajoutée." });
      setPresenceEtudiantId("");
      rafraichirTableau();
    } catch (err) {
      setPresenceState({ loading: false, error: err, success: null });
    }
  };

  const handleCloturer = async () => {
    setClotureState({ loading: true, error: null });
    try {
      await cloturerSession(session.id);
      setSession(null);
      setClotureState({ loading: false, error: null });
      rafraichirTableau();
    } catch (err) {
      setClotureState({ loading: false, error: err });
    }
  };

  const totaux = useMemo(
    () =>
      tableau.reduce(
        (acc, l) => ({
          presences: acc.presences + l.presences,
          exercicesDeposes: acc.exercicesDeposes + l.exercicesDeposes,
          relecturesEnAttente: acc.relecturesEnAttente + l.relecturesEnAttente,
        }),
        { presences: 0, exercicesDeposes: 0, relecturesEnAttente: 0 },
      ),
    [tableau],
  );

  return (
    <div>
      <div className="grid2">
        <div className="card">
          <div className="cardlabel">{session ? "Session ouverte" : "Ouvrir une session"}</div>
          {!sessionLookupDone ? (
            <Spinner />
          ) : session ? (
            <div>
              <p style={{ margin: "0 0 4px", fontSize: 22, fontWeight: 600 }}>{session.code}</p>
              <p className="muted" style={{ margin: "0 0 12px" }}>
                Session #{session.id} · expire à {new Date(session.expirationAt).toLocaleTimeString("fr-FR")}
              </p>
              <ErrorBanner error={clotureState.error} />
              <button type="button" className="btn danger" onClick={handleCloturer} disabled={clotureState.loading}>
                {clotureState.loading ? <Spinner /> : "Clôturer la session"}
              </button>
            </div>
          ) : (
            <form onSubmit={handleOuvrir}>
              <ErrorBanner error={sessionState.error} />
              <div className="field">
                <label htmlFor="titre">Titre de la session</label>
                <input
                  id="titre"
                  value={titre}
                  onChange={(e) => setTitre(e.target.value)}
                  placeholder="ex. Spring Boot — Contrôleurs REST"
                  required
                />
              </div>
              <button type="submit" className="btn" disabled={sessionState.loading}>
                {sessionState.loading ? <Spinner /> : "Ouvrir et générer le code"}
              </button>
            </form>
          )}
        </div>

        <div className="card">
          <div className="cardlabel">Ajouter une présence manuellement</div>
          {!session ? (
            <p className="muted">Ouvre d'abord une session.</p>
          ) : (
            <form onSubmit={handlePresenceManuelle}>
              <ErrorBanner error={presenceState.error} />
              <SuccessBanner message={presenceState.success} />
              <div className="field">
                <label htmlFor="presenceEtudiant">Étudiant</label>
                <select
                  id="presenceEtudiant"
                  value={presenceEtudiantId}
                  onChange={(e) => setPresenceEtudiantId(e.target.value)}
                  required
                >
                  <option value="">— Choisir —</option>
                  {etudiants.map((e) => (
                    <option key={e.id} value={e.id}>
                      {e.nom}
                    </option>
                  ))}
                </select>
              </div>
              <button type="submit" className="btn secondary" disabled={presenceState.loading}>
                {presenceState.loading ? <Spinner /> : "Ajouter"}
              </button>
            </form>
          )}
        </div>
      </div>

      <div className="grid3">
        <div className="card">
          <div className="cardlabel">Présences (total)</div>
          <div style={{ fontSize: 28, fontWeight: 600 }}>{totaux.presences}</div>
        </div>
        <div className="card">
          <div className="cardlabel">Exercices déposés</div>
          <div style={{ fontSize: 28, fontWeight: 600 }}>{totaux.exercicesDeposes}</div>
        </div>
        <div className="card">
          <div className="cardlabel">Relectures en attente</div>
          <div style={{ fontSize: 28, fontWeight: 600 }}>{totaux.relecturesEnAttente}</div>
        </div>
      </div>

      <div className="grid2">
        <div className="card">
          <div className="cardlabel">Présences par étudiant</div>
          <PresencesChart tableau={tableau} />
        </div>
        <div className="card">
          <div className="cardlabel">État des exercices</div>
          <ExercicesDonut tableau={tableau} />
        </div>
      </div>

      <div className="card">
        <div className="cardtop">
          <div className="cardlabel" style={{ marginBottom: 0 }}>
            Tableau de bord — promotion
          </div>
          <button type="button" className="btn secondary" onClick={rafraichirTableau}>
            Actualiser
          </button>
        </div>
        <ErrorBanner error={tableauError} />
        {tableauLoading ? (
          <Spinner />
        ) : (
          <table>
            <thead>
              <tr>
                <th>Étudiant</th>
                <th>Présences</th>
                <th>Exercices déposés</th>
                <th>Moyenne</th>
                <th>Relectures dues</th>
              </tr>
            </thead>
            <tbody>
              {tableau.map((l) => (
                <tr key={l.etudiantId}>
                  <td>{l.nom}</td>
                  <td>{l.presences}</td>
                  <td>{l.exercicesDeposes}</td>
                  <td>{l.moyenne ?? "—"}</td>
                  <td>
                    {l.relecturesEnAttente > 0 ? (
                      <span className="tag" style={{ background: "var(--danger-bg)", color: "var(--danger)" }}>
                        {l.relecturesEnAttente}
                      </span>
                    ) : (
                      "0"
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
