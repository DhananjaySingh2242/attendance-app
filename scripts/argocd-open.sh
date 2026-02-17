#!/usr/bin/env bash
# Open Argo CD UI: fix port conflict, restart repo-server, port-forward, print URL.
# Run from project root: ./scripts/argocd-open.sh   or   bash scripts/argocd-open.sh

set -e
# Optional steps may fail; we still want to try port-forward
ARGOCD_NAMESPACE="${ARGOCD_NAMESPACE:-argocd}"
LOCAL_PORT="${LOCAL_PORT:-9443}"

echo "==> Checking cluster..."
if ! kubectl cluster-info &>/dev/null; then
  echo "ERROR: Cannot reach cluster. Start Minikube: minikube start"
  exit 1
fi

echo "==> Checking Argo CD pods..."
kubectl get pods -n "$ARGOCD_NAMESPACE" -l app.kubernetes.io/name=argocd-server --no-headers 2>/dev/null || true
if ! kubectl get deployment argocd-server -n "$ARGOCD_NAMESPACE" &>/dev/null; then
  echo "ERROR: Argo CD not found in namespace $ARGOCD_NAMESPACE. Install it first."
  exit 1
fi

echo "==> Freeing port $LOCAL_PORT (kill existing process if any)..."
if command -v lsof &>/dev/null; then
  PID=$(lsof -ti ":$LOCAL_PORT" 2>/dev/null || true)
  if [ -n "$PID" ]; then
    kill "$PID" 2>/dev/null || true
    sleep 1
  fi
fi

echo "==> Increasing repo-server git timeout (helps GitHub timeouts)..."
kubectl patch configmap argocd-cmd-params-cm -n "$ARGOCD_NAMESPACE" --type merge \
  -p '{"data":{"reposerver.git.request.timeout":"60s"}}' 2>/dev/null || true

echo "==> Restarting repo-server (clean start)..."
kubectl delete pod -n "$ARGOCD_NAMESPACE" -l app.kubernetes.io/name=argocd-repo-server --ignore-not-found --timeout=10s 2>/dev/null || true
echo "    Waiting 15s for new pod..."
sleep 15

echo "==> Checking server pod..."
kubectl get pods -n "$ARGOCD_NAMESPACE" -l app.kubernetes.io/name=argocd-server --no-headers 2>/dev/null || true

echo "==> Starting port-forward (leave this terminal open)..."
echo ""
echo "    Open in browser: https://localhost:$LOCAL_PORT"
echo "    Login: admin / password from: kubectl -n $ARGOCD_NAMESPACE get secret argocd-initial-admin-secret -o jsonpath=\"{.data.password}\" | base64 -d && echo"
echo ""
exec kubectl port-forward svc/argocd-server -n "$ARGOCD_NAMESPACE" "$LOCAL_PORT:443"
