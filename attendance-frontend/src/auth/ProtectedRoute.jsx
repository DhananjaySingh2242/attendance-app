import { Navigate } from "react-router-dom";
import { getRole } from "./AuthService";

const ProtectedRoute = ({ role, children }) => {
  const userRole = getRole();

  if (!userRole) {
    return <Navigate to="/login" />;
  }

  // allow ADMIN to access USER routes if needed
  if (role === "ROLE_USER" && userRole === "ROLE_ADMIN") {
    return children;
  }

  return userRole === role ? children : <Navigate to="/login" />;
};

export default ProtectedRoute;
