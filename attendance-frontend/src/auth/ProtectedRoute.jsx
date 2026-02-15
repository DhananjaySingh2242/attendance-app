import { Navigate } from "react-router-dom";
import AuthService from "./AuthService";

const ProtectedRoute = ({ keycloak, role, children }) => {
  if (!keycloak?.authenticated) {
    return <Navigate to="/" replace />;
  }

  if (role && !AuthService.hasRole(keycloak, role)) {
    return <Navigate to="/unauthorized" replace />;
  }

  return children;
};

export default ProtectedRoute;
