import { NavLink, Outlet } from "react-router-dom";

export function Layout() {
  return (
    <div className="app-shell">
      <header className="top-nav">
        <div className="brand">
          Fila<span>Zero</span>
        </div>
        <nav className="nav-links">
          <NavLink to="/" end className={({ isActive }) => (isActive ? "active" : "")}>
            Atendimento
          </NavLink>
          <NavLink to="/indicadores" className={({ isActive }) => (isActive ? "active" : "")}>
            Indicadores
          </NavLink>
        </nav>
      </header>
      <Outlet />
    </div>
  );
}
