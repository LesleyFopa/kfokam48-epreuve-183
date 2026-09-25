import { NavLink } from "react-router-dom";
import { useTheme } from "../hooks/useTheme";

export default function TopBar() {
  const { theme, toggle } = useTheme();

  return (
    <div className="topbar">
      <div className="brandrow">
        <span className="brandmark">K48</span>
        <span className="brandname">KFOKAM48</span>
      </div>
      <nav className="pillnav">
        <NavLink to="/formateur" className={({ isActive }) => (isActive ? "active" : "")}>
          Formateur
        </NavLink>
        <NavLink to="/etudiant" className={({ isActive }) => (isActive ? "active" : "")}>
          Étudiant
        </NavLink>
        <NavLink to="/relecteur" className={({ isActive }) => (isActive ? "active" : "")}>
          Relecteur
        </NavLink>
      </nav>
      <button
        type="button"
        className="iconbtn"
        onClick={toggle}
        aria-label={theme === "dark" ? "Passer en mode clair" : "Passer en mode sombre"}
        title={theme === "dark" ? "Mode clair" : "Mode sombre"}
      >
        {theme === "dark" ? "☀" : "☾"}
      </button>
    </div>
  );
}
