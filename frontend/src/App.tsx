import { Navigate, Route, Routes } from "react-router-dom";
import { Layout } from "./components/Layout";
import { AttendancePage } from "./pages/AttendancePage";
import { MetricsPage } from "./pages/MetricsPage";

export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route index element={<AttendancePage />} />
        <Route path="indicadores" element={<MetricsPage />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Route>
    </Routes>
  );
}
