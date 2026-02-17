# Pod crash / BackOff troubleshooting

When the **attendance-app** pod is in `Back-off restarting failed container`, the container process is exiting. Use these steps to find the cause.

## 1. Get logs from the failed container

From the **previous** (crashed) instance:

```bash
kubectl logs deployment/attendance-app --previous --tail=200
```

Or for a specific pod (replace with your pod name):

```bash
kubectl logs attendance-app-5c9c74ffdb-hrlhn --previous --tail=200
```

If the pod is still starting, stream current logs:

```bash
kubectl logs -f deployment/attendance-app --tail=100
```

## 2. Check pod status and events

```bash
kubectl get pods -l app=attendance-app
kubectl describe pod -l app=attendance-app
```

Look at **Events** at the bottom for `Failed`, `Error`, or `Back-off`.

## 3. Common causes and fixes

| Symptom in logs | Cause | Fix |
|-----------------|--------|-----|
| `Access denied for user 'attendance'@...` or MySQL connection error | Wrong DB password or secret | Ensure secret `db-secret` has key `mysql-password` with the **attendance** user password (e.g. `Ansh@123` from MySQL init). Create/update: `kubectl create secret generic db-secret --from-literal=mysql-password='Ansh@123' --dry-run=client -o yaml \| kubectl apply -f -` |
| `Connection refused` to `mysql:3306` | MySQL not running or not ready | Deploy MySQL first: `kubectl apply -f k8s/mysql.yaml` and wait until the mysql pod is Running. |
| `Connection refused` to `mongodb:27017` or `redis:6379` or `rabbitmq:5672` | Backing service missing | Deploy required services (see `k8s/`) and ensure Services exist: `kubectl get svc mysql mongodb redis rabbitmq keycloak`. |
| `CreateContainerConfigError` | Missing secret | Create `db-secret`: see above. |
| OutOfMemoryError | JVM heap too small | Increase container memory limit or set `JAVA_OPTS` / `-Xmx` in deployment. |

## 4. Ensure deployment and image are up to date

- If you use **plain Kubernetes**: `kubectl apply -f k8s/app-deployment.yaml`
- If you use **Helm**: `helm upgrade attendance-app ./helm/attendance-app -f helm/attendance-app/values.yaml`

Then **rebuild and push** the app image after code/config changes (e.g. Redis/Keycloak config, `application.yml`):

```bash
mvn package -DskipTests
docker build -t dhananjaysingh/attendance-app:1.0 .
# If using kind: kind load docker-image dhananjaysingh/attendance-app:1.0
kubectl rollout restart deployment/attendance-app
```

## 5. Restart and watch

```bash
kubectl rollout restart deployment/attendance-app
kubectl get pods -l app=attendance-app -w
# In another terminal:
kubectl logs -f deployment/attendance-app
```

Once you have the **exact error** from `kubectl logs ... --previous`, you can fix the underlying cause (secret, dependency service, or config).
