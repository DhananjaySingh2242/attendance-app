# Argo CD: "Request has been terminated" / CORS when syncing

When the Argo CD UI shows **"Unable to sync: Request has been terminated"** with causes like *Origin is not allowed by Access-Control-Allow-Origin* or *network is offline*, the browser is blocking the sync request from the UI to the Argo CD API (same-origin or CORS).

## Quick fix (copy-paste)

Run this, then **use only one tab** with **http://localhost:8080** to open Argo CD and sync:

```bash
# Enable HTTP mode and restart Argo CD server
kubectl patch configmap argocd-cmd-params-cm -n argocd --type merge -p '{"data":{"server.insecure":"true"}}'
kubectl rollout restart deployment argocd-server -n argocd

# Wait ~30 seconds for the new pod, then port-forward (leave this running)
kubectl port-forward svc/argocd-server -n argocd 8080:80
```

Open **http://localhost:8080** in the browser (not https, not a different port). Log in and click Sync. Keep the port-forward terminal open while you use the UI.

## 1. Use one URL for both UI and API (recommended)

The UI and the API must be reached at the **same origin** (same scheme + host + port). If you open the UI at `http://localhost:8080` but the UI is configured to call the API at another URL, or you use different port-forwards for UI vs API, requests can fail or be blocked.

**Port-forward (single endpoint):**

```bash
# Argo CD server (UI + API together)
kubectl port-forward svc/argocd-server -n argocd 8443:443
```

Then open **https://localhost:8443** in the browser (accept the self-signed cert). Use the same URL for both viewing the UI and for API calls — no CORS.

**If you prefer HTTP (no TLS):** run the server in insecure mode (see below), then:

```bash
kubectl port-forward svc/argocd-server -n argocd 8080:80
```

Open **http://localhost:8080**. Again, one origin for UI and API.

## 2. Run Argo CD server in insecure mode (e.g. local / Minikube)

When you use **HTTP** and a single port-forward, CORS issues often disappear. Enable insecure mode so the server listens on HTTP inside the cluster:

**Option A – Patch the ConfigMap once:**

```bash
kubectl patch configmap argocd-cmd-params-cm -n argocd --type merge -p '{"data":{"server.insecure":"true"}}'
```

Then restart the Argo CD server so it picks up the change:

```bash
kubectl rollout restart deployment argocd-server -n argocd
```

After the rollout, port-forward to the **HTTP** port (often 80 when insecure):

```bash
kubectl port-forward svc/argocd-server -n argocd 8080:80
```

Open **http://localhost:8080** and sync again.

## 3. Sync from the CLI instead of the UI

If the UI keeps failing, you can sync from your machine using the Argo CD CLI (no browser CORS):

```bash
argocd app sync attendance-app
```

(Install the CLI and run `argocd login` against your cluster/port-forward first.)

## 4. Timeouts / page unload

"Request has been terminated" can also mean the request **timed out** or the **page was closed** before the sync finished. Try:

- Syncing again and leaving the tab open until the operation completes.
- Checking Argo CD server and repo-server logs for errors:  
  `kubectl logs -n argocd -l app.kubernetes.io/name=argocd-server --tail=50`
