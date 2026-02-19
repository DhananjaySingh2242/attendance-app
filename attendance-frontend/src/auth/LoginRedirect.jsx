import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import AuthService from "./AuthService";

const LoginRedirect = ({ keycloak }) => {
  const navigate = useNavigate();

  useEffect(() => {
    if (!keycloak?.authenticated || !keycloak?.tokenParsed) return;

    const roles = keycloak.tokenParsed.realm_access?.roles || [];
    console.log("Logged in roles:", roles);

    if (AuthService.isAdmin(keycloak)) {
      navigate("/admin", { replace: true });
    } else if (AuthService.isUser(keycloak)) {
      navigate("/dashboard", { replace: true });
    } else {
      keycloak.logout({ redirectUri: window.location.origin });
    }
  }, [keycloak, navigate]);

  return <h3>Redirecting...</h3>;
};

export default LoginRedirect;
