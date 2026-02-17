# Fix: ErrImagePull for attendance-app

The error means the image `dhananjaysingh/attendance-app:1.0` is not available to the cluster (repository doesn't exist on Docker Hub or is private).

## Option 1: Build and push the image to Docker Hub (recommended for shared/cluster use)

1. **Build the JAR** (from project root):
   ```bash
   ./mvnw clean package -DskipTests
   ```

2. **Build the Docker image** (use your actual Docker Hub username):
   ```bash
   docker build -t dhananjaysingh/attendance-app:1.0 .
   ```
   If your Docker Hub username is `dhananjaysingh2242`, use:
   ```bash
   docker build -t dhananjaysingh2242/attendance-app:1.0 .
   ```

3. **Log in to Docker Hub**:
   ```bash
   docker login
   ```

4. **Push the image**:
   ```bash
   docker push dhananjaysingh/attendance-app:1.0
   ```
   (or `docker push dhananjaysingh2242/attendance-app:1.0` if you used that tag)

5. **Make sure the image name matches in your manifests**:
   - `k8s/app-deployment.yaml` uses: `dhananjaysingh/attendance-app:1.0`
   - `helm/attendance-app/values.yaml` uses: `dhananjaysingh2242/attendance-app`
   Use the same repository name everywhere (see below to align them).

6. **If the repo is private**: create a Kubernetes image pull secret and add it to the deployment (see Option 3).

---

## Option 2: Use a locally built image (Minikube / Kind)

If you run the cluster locally and don't need to pull from a registry:

**Minikube:**
```bash
 eval $(minikube docker-env)
 ./mvnw clean package -DskipTests
 docker build -t dhananjaysingh/attendance-app:1.0 .
```
Then set `imagePullPolicy: Never` in the deployment so Kubernetes uses the local image.

**Kind:**
```bash
 ./mvnw clean package -DskipTests
 docker build -t dhananjaysingh/attendance-app:1.0 .
 kind load docker-image dhananjaysingh/attendance-app:1.0 --name <your-cluster-name>
```
And use `imagePullPolicy: Never` in the deployment.

---

## Option 3: Private registry / Docker Hub login from Kubernetes

If the image is in a private Docker Hub repo:

1. Create a registry secret:
   ```bash
   kubectl create secret docker-registry regcred \
     --docker-server=https://index.docker.io/v1/ \
     --docker-username=<your-username> \
     --docker-password=<your-password-or-token> \
     --docker-email=<your-email>
   ```

2. Add to your deployment pod spec (e.g. in `k8s/app-deployment.yaml`):
   ```yaml
   spec:
     imagePullSecrets:
       - name: regcred
     containers:
       - name: attendance-app
         image: dhananjaysingh/attendance-app:1.0
         ...
   ```

---

## Aligning image names in this repo

- **k8s/app-deployment.yaml** uses: `dhananjaysingh/attendance-app:1.0`
- **helm/attendance-app/values.yaml** uses: `dhananjaysingh2242/attendance-app`

Use one Docker Hub username and the same image name in both places so build/push and deployments stay in sync.

---

## What to do after the image is available

Once the image is pushed (or loaded locally), deploy the app and its dependencies in this order.

### 1. Create the database secret

The app and MySQL both expect a secret named `db-secret` with key `mysql-password`. The MySQL init script in `k8s/mysql.yaml` creates the `attendance` user with password `Ansh@123`, so use that value:

```bash
kubectl create secret generic db-secret --from-literal=mysql-password='Ansh@123'
```

(If you use a different password, change it in `k8s/mysql.yaml` in the ConfigMap `mysql-init` → `init-attendance-user.sql` so it matches.)

### 2. Deploy MySQL (and its PVC/ConfigMap)

```bash
kubectl apply -f k8s/mysql.yaml
```

Wait until the MySQL pod is Running:

```bash
kubectl get pods -l app=mysql -w
```
(Ctrl+C when Ready.)

### 3. Deploy the attendance app and service

```bash
kubectl apply -f k8s/app-deployment.yaml
kubectl apply -f k8s/app-service.yaml
```

### 4. Check that everything is running

```bash
kubectl get pods
kubectl get svc
```

The app pod should be **Running** and the service `attendance-app` should show NodePort **30007**.

### 5. Access the app

- **From your machine:**  
  If using Minikube: `minikube service attendance-app --url`  
  Or open: `http://<node-ip>:30007` (e.g. `http://localhost:30007` if your cluster exposes the node on localhost).

- **From inside the cluster:**  
  `http://attendance-app:8081`

### 6. Optional: other dependencies

If the app uses Keycloak, Redis, RabbitMQ, or MongoDB, apply those too before or with the app:

```bash
kubectl apply -f k8s/keycloak.yaml
kubectl apply -f k8s/redis.yaml
kubectl apply -f k8s/rabbitmq.yaml
kubectl apply -f k8s/mongodb.yaml
```

Then configure the app (env or ConfigMap) with the correct service URLs (e.g. `keycloak`, `redis`, `rabbitmq`, `mongodb`).
