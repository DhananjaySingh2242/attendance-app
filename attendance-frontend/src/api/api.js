import axios from "axios";
import keycloak from "../keycloak";

// Empty baseURL when served from same host (e.g. minikube service); set VITE_API_BASE_URL for local dev proxy
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? "",
});

api.interceptors.request.use(
  async (config) => {
    if (keycloak?.authenticated) {
      await keycloak.updateToken(30);

      config.headers.Authorization = `Bearer ${keycloak.token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export default api;
