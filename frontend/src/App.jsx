import { Navigate, Route, Routes } from "react-router-dom";
import TopBar from "./components/TopBar";
import FormateurScreen from "./screens/formateur/FormateurScreen";
import EtudiantScreen from "./screens/etudiant/EtudiantScreen";
import RelecteurScreen from "./screens/relecteur/RelecteurScreen";
import { useApiStatus } from "./hooks/useApiStatus";

export default function App() {
  const offline = useApiStatus();

  return (
    <div className="app">
      <TopBar />
      {offline && (
        <div className="banner error" role="alert" style={{ marginTop: -6 }}>
          <span aria-hidden="true">⚠</span>
          <span>Impossible de joindre le serveur. Vérifie que le backend est démarré.</span>
        </div>
      )}
      <Routes>
        <Route path="/" element={<Navigate to="/formateur" replace />} />
        <Route path="/formateur" element={<FormateurScreen />} />
        <Route path="/etudiant" element={<EtudiantScreen />} />
        <Route path="/relecteur" element={<RelecteurScreen />} />
      </Routes>
    </div>
  );
}
