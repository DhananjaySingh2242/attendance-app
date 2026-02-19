import { BrowserRouter, Routes, Route } from "react-router-dom";
import ProtectedRoute from "./auth/ProtectedRoute";
import LoginRedirect from "./auth/LoginRedirect";

import UserDashboard from "./pages/UserDashboard";
import AdminDashboard from "./pages/AdminDashboard";
import Unauthorized from "./pages/Unauthorized";

function App({ keycloak }) {
  if (!keycloak) return <div>Loading authentication...</div>;

  return (
    <BrowserRouter>
      <Routes>
        {/* After Keycloak login */}
        <Route path="/" element={<LoginRedirect keycloak={keycloak} />} />

        <Route
          path="/admin"
          element={
            <ProtectedRoute keycloak={keycloak} role="ADMIN">
              <AdminDashboard keycloak={keycloak} />
            </ProtectedRoute>
          }
        />

        <Route
          path="/dashboard"
          element={
            <ProtectedRoute keycloak={keycloak} role="USER">
              <UserDashboard keycloak={keycloak} />
            </ProtectedRoute>
          }
        />

        <Route path="/unauthorized" element={<Unauthorized keycloak={keycloak} />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
