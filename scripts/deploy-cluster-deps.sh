#!/usr/bin/env bash
# Deploy dependencies required by attendance-app in the cluster (default namespace).
# Run from project root: ./scripts/deploy-cluster-deps.sh
#
# For full one-command deploy (deps + app + secrets), use: ./scripts/run-kubernetes.sh
set -e
cd "$(dirname "$0")/.."
echo "==> Deploying MySQL, MongoDB, Keycloak (required)..."
kubectl apply -f k8s/mysql.yaml -n default 2>/dev/null || true
kubectl apply -f k8s/mongodb.yaml -n default
kubectl apply -f k8s/keycloak.yaml -n default
echo "==> Optional: Redis, RabbitMQ..."
kubectl apply -f k8s/redis.yaml -n default 2>/dev/null || true
kubectl apply -f k8s/rabbitmq.yaml -n default 2>/dev/null || true
echo "==> Wait for pods (e.g. mongodb, keycloak) to be Running: kubectl get pods -n default"
echo "    Then sync/restart attendance-app in Argo CD."
