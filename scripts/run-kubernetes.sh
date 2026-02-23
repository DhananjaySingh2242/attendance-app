#!/usr/bin/env bash
# Run Attendance App on Kubernetes (Minikube, kind, or any cluster)
# Usage: ./scripts/run-kubernetes.sh [--build-image]
#   --build-image: build JAR, Docker image, and load into cluster (for Minikube/kind)

set -e
cd "$(dirname "$0")/.."

CLUSTER="${CLUSTER:-minikube}"   # minikube, kind, or "" for external
BUILD_IMAGE=false
for arg in "$@"; do
  [ "$arg" = "--build-image" ] && BUILD_IMAGE=true
done

echo "==> Attendance App - Kubernetes Deploy"
echo ""

# 1. Check kubectl and cluster
if ! command -v kubectl &>/dev/null; then
  echo "ERROR: kubectl not found. Install kubectl first."
  exit 1
fi

if ! kubectl cluster-info &>/dev/null; then
  echo "ERROR: Cannot connect to cluster. Start your cluster first:"
  echo "  Minikube: minikube start"
  echo "  Kind:     kind create cluster"
  exit 1
fi

echo "Cluster connected."
echo ""

# 2. Create secrets (required for MySQL and app)
echo "==> Creating secrets (if not exist)..."
kubectl create secret generic db-secret \
  --from-literal=mysql-password=Ansh@123 \
  --dry-run=client -o yaml | kubectl apply -f - 2>/dev/null || true

kubectl create secret generic keycloak-secret \
  --from-literal=admin-password=admin \
  --from-literal=client-secret=CHANGE_ME \
  --dry-run=client -o yaml | kubectl apply -f - 2>/dev/null || true

echo "Secrets ready."
echo ""

# 3. Build and load image (optional; image includes frontend + backend)
if [ "$BUILD_IMAGE" = true ]; then
  echo "==> Building Docker image (frontend + backend)..."
  docker build -t dhananjaysingh2242/attendance-app:1.0 .
  
  if [ -n "$(kubectl config current-context 2>/dev/null | grep -i minikube)" ]; then
    echo "==> Loading image into Minikube..."
    minikube image load dhananjaysingh2242/attendance-app:1.0
  elif [ -n "$(kubectl config current-context 2>/dev/null | grep -i kind)" ]; then
    echo "==> Loading image into Kind..."
    kind load docker-image dhananjaysingh2242/attendance-app:1.0
  else
    echo "==> Pushing image to Docker Hub (cluster may pull from there)..."
    docker push dhananjaysingh2242/attendance-app:1.0 || echo "  (Push failed - use image load for local clusters)"
  fi
  echo ""
fi

# 4. Deploy dependencies first
echo "==> Deploying MySQL, MongoDB, Redis, RabbitMQ, Keycloak..."
kubectl apply -f k8s/mysql.yaml
kubectl apply -f k8s/mongodb.yaml
kubectl apply -f k8s/redis.yaml
kubectl apply -f k8s/rabbitmq.yaml
kubectl apply -f k8s/keycloak.yaml

echo ""
echo "==> Waiting for dependencies to be ready (up to 2 min)..."
kubectl wait --for=condition=ready pod -l app=mysql --timeout=120s 2>/dev/null || true
kubectl wait --for=condition=ready pod -l app=mongodb --timeout=60s 2>/dev/null || true
kubectl wait --for=condition=ready pod -l app=keycloak --timeout=120s 2>/dev/null || true

echo ""
echo "==> Deploying attendance-app..."
kubectl apply -f k8s/app-deployment.yaml
kubectl apply -f k8s/app-service.yaml

echo ""
echo "==> Done! Pods starting..."
kubectl get pods -l app=attendance-app
echo ""
echo "Wait 1-2 min for the app to become Ready. Check: kubectl get pods"
echo ""
echo "--- How to access (full app: UI + API from one URL) ---"
echo "  minikube service attendance-app"
echo "  (Opens the app in browser. For Keycloak login, in another terminal run: kubectl port-forward svc/keycloak 8080:8080)"
echo ""
echo "  Or:  minikube service attendance-app --url   # print URL only"
echo "  Or:  kubectl port-forward svc/attendance-app 8081:8081   # then http://localhost:8081"
echo ""
echo "--- Keycloak setup (first time) ---"
echo "  kubectl port-forward svc/keycloak 8080:8080"
echo "  Open http://localhost:8080 -> create realm 'keycloak-demo', clients (see docs/KEYCLOAK_SETUP.md)"
echo "  Update keycloak-secret with real client secret:"
echo "    kubectl create secret generic keycloak-secret --from-literal=admin-password=admin --from-literal=client-secret=YOUR_REAL_SECRET --dry-run=client -o yaml | kubectl apply -f -"
echo "    kubectl rollout restart deployment/attendance-app"
echo ""
