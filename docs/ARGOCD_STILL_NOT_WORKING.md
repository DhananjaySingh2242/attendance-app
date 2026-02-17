# Argo CD still not working – full reset

If the UI won’t open, sync fails, or repo-server is in Error, try this order.

## 1. One-shot: open UI (script)

From the **attendanceApp** project root:

```bash
chmod +x scripts/argocd-open.sh
./scripts/argocd-open.sh
```

This will:

- Check cluster and Argo CD
- Use port **9443** (avoids 8443 conflict)
- Restart repo-server and start port-forward

Then open **https://localhost:9443** in the browser and accept the certificate.

---

## 2. Manual steps (if script fails)

**A) Start cluster**

```bash
minikube start
```

**B) Free the port and port-forward**

```bash
# If 8443 is in use, kill it or use 9443
lsof -i :8443
kill <PID>   # or use 9443 below

kubectl port-forward svc/argocd-server -n argocd 9443:443
```

Open **https://localhost:9443**.

**C) Repo-server in Error (copyutil init)**

See why the init container fails:

```bash
kubectl logs -n argocd -l app.kubernetes.io/name=argocd-repo-server -c copyutil --tail=30
```

- If you see **"Text file busy"**: the copyutil init container is failing. Delete the pod and retry; if it keeps failing, reinstall Argo CD with the **Helm chart** (it has the fix) or use an older stable manifest.
- If you see **timeout / network**: from inside Minikube, GitHub can be slow. Increase timeout:

  ```bash
  kubectl patch configmap argocd-cmd-params-cm -n argocd --type merge -p '{"data":{"reposerver.git.request.timeout":"60s"}}'
  kubectl delete pod -n argocd -l app.kubernetes.io/name=argocd-repo-server
  ```

**D) Get admin password**

```bash
kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d && echo
```

Use username **admin** and that password in the UI.

---

## 3. Reinstall Argo CD (last resort)

If repo-server never becomes Ready (copyutil keeps failing):

```bash
# Remove existing install
kubectl delete -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml --ignore-not-found --timeout=60s
kubectl delete namespace argocd --ignore-not-found --timeout=60s

# Reinstall
kubectl create namespace argocd
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml

# Wait for pods
kubectl get pods -n argocd -w
# When argocd-server is 1/1 Running, Ctrl+C then:

kubectl port-forward svc/argocd-server -n argocd 9443:443
```

Then open **https://localhost:9443** and re-apply your Application:

```bash
kubectl apply -f argocd/application.yaml
```

---

## 4. Sync without UI (CLI)

If the UI still has CORS or sync issues, sync from the CLI:

```bash
argocd login localhost:9443 --insecure
argocd app sync attendance-app
```

(Get the admin password from the secret in step 2D.)
