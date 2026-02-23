# Run Attendance App Locally

> **Kubernetes?** See [KUBERNETES.md](KUBERNETES.md) for one-command deploy (`./scripts/run-kubernetes.sh --build-image`).

## Prerequisites

- **Java 21** and **Maven**
- **Node.js** (v18+) and **npm**
- **MySQL** (localhost:3306)
- **MongoDB** (localhost:27017)
- **Redis** (localhost:6379)
- **RabbitMQ** (localhost:5672)
- **Keycloak** (localhost:8080)

---

## 1. Database & services

### MySQL

Create database and user:

```sql
CREATE DATABASE attendance;
CREATE USER 'attendance'@'localhost' IDENTIFIED BY 'Ansh@123';
GRANT ALL PRIVILEGES ON attendance.* TO 'attendance'@'localhost';
FLUSH PRIVILEGES;
```

Or use your own password and set the env var when running the backend: `DB_PASSWORD=yourpassword`.

### Keycloak

1. Start Keycloak (e.g. Docker: `docker run -p 8080:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak start-dev`).
2. Open **http://localhost:8080** and log in (admin / admin).
3. Create a **realm** named `keycloak-demo`.
4. Create **clients** in that realm:
   - **attendance-client** (for frontend login):  
     - Access type: **public**  
     - Valid redirect URIs: `http://localhost:5173/*` (or your frontend URL)  
     - Web origins: `http://localhost:5173` (or `+`)
   - **attendance-admin-client** (for backend admin register):  
     - Access type: **confidential**  
     - Copy the **client secret** from the Credentials tab.
5. Create **realm roles**: `ADMIN`, `USER` (exact names, uppercase).
6. **Set USER as default realm role** (so new users get it automatically):
   - Go to **Realm roles** → **Default roles** (or **Roles** → **Default roles**).
   - Add **USER** to "Realm default roles". This ensures every new user gets the USER role.
7. Create a user and assign roles for testing (or use admin register from the app).

**Role assignment:** If the app’s role assignment fails, set USER as a **default realm role** in Keycloak (Realm settings → Default roles). Ensure Keycloak is started with `KEYCLOAK_ADMIN=admin` and `KEYCLOAK_ADMIN_PASSWORD=admin`.

Set the admin client secret when running the backend (see below).

---

## 2. Backend (Spring Boot)

From the project root:

```bash
# Optional: set DB password if different from Ansh@123
export DB_PASSWORD=Ansh@123

# Required: set Keycloak admin client secret (from attendance-admin-client)
export KEYCLOAK_ADMIN_CLIENT_SECRET=your-secret-here

# Run with local profile (uses localhost for MySQL, Mongo, Redis, RabbitMQ, Keycloak)
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

Or with Maven:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

Backend will be at **http://localhost:8081**.

- Health: http://localhost:8081/api/health  
- API base: http://localhost:8081/api  

---

## 3. Frontend (React + Vite)

In a **separate terminal**:

```bash
cd attendance-frontend
npm install
npm run dev
```

Frontend will be at **http://localhost:5173** (or the URL Vite prints).

- Ensure `attendance-frontend/src/keycloak.js` has `url: "http://localhost:8080"`, realm `keycloak-demo`, clientId `attendance-client`.
- Ensure `attendance-frontend/src/api/api.js` has `baseURL: "http://localhost:8081"`.

---

## 4. Quick check

1. Open **http://localhost:5173** in the browser.
2. You should be redirected to Keycloak login.
3. After login, you are sent to `/admin` (ADMIN) or `/dashboard` (USER).
4. Admin: register users, list users/attendance. User: check-in / check-out.

---

## Optional: Docker for infra only

If you prefer to run only the dependencies in Docker:

```bash
# Example: MySQL, MongoDB, Redis, RabbitMQ
docker run -d -p 3306:3306 -e MYSQL_DATABASE=attendance -e MYSQL_USER=attendance -e MYSQL_PASSWORD=Ansh@123 -e MYSQL_ROOT_PASSWORD=root --name mysql mysql:8
docker run -d -p 27017:27017 --name mongo mongo:7
docker run -d -p 6379:6379 --name redis redis:7-alpine
docker run -d -p 5672:5672 -p 15672:15672 --name rabbitmq rabbitmq:3-management
```

Then run Keycloak and the app as above. Backend still uses **local** profile so it connects to localhost for all services.
