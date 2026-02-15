import Keycloak from "keycloak-js";

const keycloak = new Keycloak({
  url: "http://localhost:8080",
  realm: "keycloak-demo",
  clientId: "attendance-client",
});

export default keycloak;
