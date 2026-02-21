#!/usr/bin/env bash
# Delete existing attendance-app deployment and pods; let ArgoCD recreate from Git (Helm).
# Run from project root. Requires: kubectl, ArgoCD app "attendance-app" deployed.
# Secrets db-secret and keycloak-secret must exist (see KUBERNETES.md).

set -e
cd "$(dirname "$0")/.."

echo "==> Deleting attendance-app deployment and pods (ArgoCD will recreate from Git)..."
kubectl delete deployment attendance-app --ignore-not-found=true --wait=false 2>/dev/null || true

echo "==> Waiting for pods to terminate..."
sleep 5
kubectl get pods -l app=attendance-app 2>/dev/null || true

echo ""
echo "==> Trigger ArgoCD sync to recreate from helm/attendance-app:"
echo "    argocd app sync attendance-app"
echo "    # or in UI: Applications -> attendance-app -> Sync"
echo ""
echo "Or if ArgoCD has auto-sync, the deployment will reappear in ~1 min."
echo "Ensure secrets exist: kubectl get secret db-secret keycloak-secret"
echo ""
