# Argo CD – run this to get it working

If nothing else worked, do this **in order** in your terminal.

---

## Step 1: Start cluster

```bash
minikube start
```

Wait until it says it’s ready.

---

## Step 2: Reinstall Argo CD with Helm (fixes repo-server)

This replaces the broken manifest install with the Helm install (no copyutil bug).

```bash
cd /Users/dhananjaysingh/Downloads/attendanceApp
chmod +x scripts/argocd-reinstall-helm.sh
./scripts/argocd-reinstall-helm.sh
```

If you don’t have Helm:

```bash
brew install helm
```

Then run the script again.

---

## Step 3: Port-forward and open UI

In a terminal (leave it open):

```bash
kubectl port-forward svc/argocd-server -n argocd 9443:443
```

In the browser open: **https://localhost:9443**  
Accept the certificate warning.

---

## Step 4: Login

Username: **admin**

Password (run in another terminal):

```bash
kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d && echo
```

Copy the output and paste it as the password in the UI.

---

## Step 5: Deploy app dependencies (MySQL, MongoDB, Keycloak)

The attendance-app needs these services in the **default** namespace or it will crash (BackOff):

```bash
cd /Users/dhananjaysingh/Downloads/attendanceApp
chmod +x scripts/deploy-cluster-deps.sh
./scripts/deploy-cluster-deps.sh
```

Wait until MongoDB and Keycloak pods are **Running**: `kubectl get pods -n default`

## Step 6: Add your app again

From the project root:

```bash
kubectl apply -f argocd/application.yaml
```

In the Argo CD UI you should see **attendance-app**. Open it and click **Sync**.

---

## If the script fails

- **“helm: command not found”** → Install Helm: `brew install helm`
- **“minikube host is not running”** → Run `minikube start`
- **Port 9443 in use** → Run `lsof -i :9443` and `kill <PID>`, or use another port: `kubectl port-forward svc/argocd-server -n argocd 8444:443` and open https://localhost:8444
