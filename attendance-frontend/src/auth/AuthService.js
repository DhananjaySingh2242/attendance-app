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
    return (
      keycloak?.tokenParsed?.realm_access?.roles?.includes(role) || false
    );
  },

  isAdmin: (keycloak) => {
    return (
      keycloak?.tokenParsed?.realm_access?.roles?.includes("ADMIN") || false
    );
  },

  isUser: (keycloak) => {
    return (
      keycloak?.tokenParsed?.realm_access?.roles?.includes("USER") || false
    );
  },
};

export default AuthService;
