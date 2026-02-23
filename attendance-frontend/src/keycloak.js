import Keycloak from "keycloak-js";

// For minikube: run kubectl port-forward svc/keycloak 8080:8080 so browser can reach Keycloak at localhost:8080
const keycloak = new Keycloak({
  url: import.meta.env.VITE_KEYCLOAK_URL ?? "http://localhost:8080",
  realm: "keycloak-demo",
  clientId: "attendance-client",
});

export default keycloak;
