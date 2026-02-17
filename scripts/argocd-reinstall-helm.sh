#!/usr/bin/env bash
# Reinstall Argo CD using Helm (avoids repo-server copyutil bug from manifest install).
# Run from project root. Requires: minikube running, helm installed.
set -e

NAMESPACE="${ARGOCD_NAMESPACE:-argocd}"
PORT="${LOCAL_PORT:-9443}"

echo "==> 1. Minikube must be running"
minikube status || { echo "Run: minikube start"; exit 1; }

echo ""
echo "==> 2. Remove existing Argo CD (if any)"
kubectl delete application -n "$NAMESPACE" --all --ignore-not-found --timeout=30s 2>/dev/null || true
kubectl delete -n "$NAMESPACE" -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml --ignore-not-found --timeout=60s 2>/dev/null || true
kubectl delete namespace "$NAMESPACE" --ignore-not-found --timeout=60s 2>/dev/null || true
echo "    Waiting for namespace to go away..."
sleep 5

echo ""
echo "==> 3. Add Argo Helm repo and install Argo CD"
helm repo add argo https://argoproj.github.io/argo-helm 2>/dev/null || true
helm repo update
kubectl create namespace "$NAMESPACE"
helm upgrade --install argocd argo/argo-cd -n "$NAMESPACE" --wait --timeout=5m

echo ""
echo "==> 4. Get admin password"
echo "    Run this to show password:"
echo "    kubectl -n $NAMESPACE get secret argocd-initial-admin-secret -o jsonpath=\"{.data.password}\" | base64 -d && echo"
echo ""
echo "==> 5. Start port-forward (leave this terminal open)"
echo "    kubectl port-forward svc/argocd-server -n $NAMESPACE $PORT:443"
echo ""
echo "    Then open: https://localhost:$PORT   (accept cert warning, login: admin)"
echo ""
echo "==> 6. Re-apply your attendance app"
echo "    kubectl apply -f argocd/application.yaml"
echo ""
