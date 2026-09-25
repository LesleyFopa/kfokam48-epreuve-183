import { Navigate, Route, Routes } from "react-router-dom";
import TopBar from "./components/TopBar";
import FormateurScreen from "./screens/formateur/FormateurScreen";
import EtudiantScreen from "./screens/etudiant/EtudiantScreen";
import RelecteurScreen from "./screens/relecteur/RelecteurScreen";

export default function App() {
  return (
    <div className="app">
      <TopBar />
      <Routes>
        <Route path="/" element={<Navigate to="/formateur" replace />} />
        <Route path="/formateur" element={<FormateurScreen />} />
        <Route path="/etudiant" element={<EtudiantScreen />} />
        <Route path="/relecteur" element={<RelecteurScreen />} />
      </Routes>
    </div>
  );
}
