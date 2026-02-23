# Run Attendance App on Kubernetes

One-command deployment for Minikube, Kind, or any Kubernetes cluster.

---

## Prerequisites

- **kubectl** – [Install](https://kubernetes.io/docs/tasks/tools/)
- **Docker** – for building images and Minikube/Kind
- **Minikube** or **Kind** (local), or a cloud cluster (EKS, GKE, AKS)

---

## Quick Start (3 commands)

### 1. Start cluster (if using Minikube)

```bash
minikube start
```

### 2. Deploy everything (including build + load image)

```bash
chmod +x scripts/run-kubernetes.sh
./scripts/run-kubernetes.sh --build-image
```

This will:

- Create required secrets (`db-secret`, `keycloak-secret`)
- Deploy MySQL, MongoDB, Redis, RabbitMQ, Keycloak
- Build the **full app** (frontend + backend in one image) and load into Minikube
- Deploy the attendance-app

### 3. Run the whole project (one command)

```bash
minikube service attendance-app
```

This opens the **full app** (UI + API) in your browser from one URL. The same deployment serves the React frontend and the Spring Boot API.

**For login to work**, Keycloak must be reachable at `http://localhost:8080`. In another terminal run:

```bash
kubectl port-forward svc/keycloak 8080:8080
```

Then refresh the app page and sign in. (In Keycloak, add your minikube app URL to **attendance-client** Valid redirect URIs, e.g. `http://192.168.49.2:30007/*` or the URL that `minikube service attendance-app --url` prints.)

Other ways to access:

```bash
minikube service attendance-app --url   # print URL only
kubectl port-forward svc/attendance-app 8081:8081   # then open http://localhost:8081
```

---

## Without building (image already exists)

If the image `dhananjaysingh2242/attendance-app:1.0` is already on Docker Hub or loaded in the cluster:

```bash
./scripts/run-kubernetes.sh
```

---

## First-time Keycloak setup

1. Port-forward Keycloak:
   ```bash
   kubectl port-forward svc/keycloak 8080:8080
   ```

2. Open **http://localhost:8080** → Admin console (admin / admin)

3. Create realm **keycloak-demo** and clients:
   - **attendance-client** (public) – Valid redirect URIs: `http://localhost:5173/*` (local dev) and your minikube URL, e.g. `http://192.168.49.2:30007/*` (get it from `minikube service attendance-app --url`)
   - **attendance-admin-client** (confidential) – copy the **Secret** from Credentials tab

4. Create roles **ADMIN** and **USER**; set USER as default realm role

5. Update the Keycloak secret with the real client secret:
   ```bash
   kubectl delete secret keycloak-secret
   kubectl create secret generic keycloak-secret \
     --from-literal=admin-password=admin \
     --from-literal=client-secret=YOUR_REAL_CLIENT_SECRET
   kubectl rollout restart deployment/attendance-app
   ```

See [docs/KEYCLOAK_SETUP.md](docs/KEYCLOAK_SETUP.md) for details.

---

## Manual deployment (step by step)

If you prefer to run each step yourself:

```bash
# 1. Create secrets
kubectl create secret generic db-secret --from-literal=mysql-password=Ansh@123
kubectl create secret generic keycloak-secret \
  --from-literal=admin-password=admin \
  --from-literal=client-secret=CHANGE_ME

# 2. Deploy dependencies
kubectl apply -f k8s/mysql.yaml
kubectl apply -f k8s/mongodb.yaml
kubectl apply -f k8s/redis.yaml
kubectl apply -f k8s/rabbitmq.yaml
kubectl apply -f k8s/keycloak.yaml

# 3. Wait for pods
kubectl get pods -w   # Ctrl+C when ready

# 4. Deploy app
kubectl apply -f k8s/app-deployment.yaml
kubectl apply -f k8s/app-service.yaml
```

---

## Using ArgoCD + Helm

1. Install ArgoCD and apply the application:
   ```bash
   kubectl apply -f argocd/application.yaml
   ```
2. Ensure secrets exist (ArgoCD won't create them):
   ```bash
   kubectl create secret generic db-secret --from-literal=mysql-password=Ansh@123
   kubectl create secret generic keycloak-secret --from-literal=admin-password=admin --from-literal=client-secret=CHANGE_ME
   ```
3. Deploy dependencies (MySQL, MongoDB, Redis, RabbitMQ, Keycloak) once:
   ```bash
   ./scripts/run-kubernetes.sh
   ```
4. ArgoCD syncs from `helm/attendance-app` (image, prod env, startup/liveness probes, Keycloak from secret).

### Clean redeploy (delete pods and get a fresh deployment)

To remove the current app deployment and let ArgoCD recreate it from Git (e.g. after fixing Helm values):

```bash
./scripts/argocd-clean-redeploy.sh
```

Then sync in ArgoCD (UI or `argocd app sync attendance-app`). With auto-sync enabled, the deployment is recreated automatically.

---

## Troubleshooting

| Problem | Fix |
|---------|-----|
| `CreateContainerConfigError` | Secret missing. Run: `kubectl create secret generic keycloak-secret --from-literal=admin-password=admin --from-literal=client-secret=CHANGE_ME` |
| `ErrImagePull` | Image not found. Use `--build-image` with `./scripts/run-kubernetes.sh` or push image to Docker Hub. |
| App pod CrashLoopBackOff | Check logs: `kubectl logs deployment/attendance-app --tail=100`. Often MySQL/Keycloak not ready – wait and retry. |
| Can't reach app | Use `minikube service attendance-app --url` or `kubectl port-forward svc/attendance-app 8081:8081` |

See [docs/POD_CRASH_TROUBLESHOOTING.md](docs/POD_CRASH_TROUBLESHOOTING.md) for more.
