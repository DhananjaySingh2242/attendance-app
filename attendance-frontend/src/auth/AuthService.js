const AuthService = {
  login: (keycloak) => {
    if (!keycloak) return;
    keycloak.login();
  },

  logout: (keycloak) => {
    if (!keycloak) return;

    keycloak.logout({
      redirectUri: window.location.origin,
    });
  },

  isAuthenticated: (keycloak) => {
    return Boolean(keycloak?.authenticated);
  },

  hasRole: (keycloak, role) => {
    const roles = keycloak?.tokenParsed?.realm_access?.roles || [];
    const target = (role || "").toUpperCase();
    return roles.some((r) => String(r).toUpperCase() === target);
  },

  isAdmin: (keycloak) => {
    return AuthService.hasRole(keycloak, "ADMIN");
  },

  isUser: (keycloak) => {
    return AuthService.hasRole(keycloak, "USER");
  },
};

export default AuthService;
