# Keycloak setup: attendance-client (frontend) + attendance-admin-client (backend)

## Overview

| Client                    | Use case              | Used by        |
|---------------------------|------------------------|----------------|
| **attendance-client**     | User login (OIDC)      | Frontend (React) |
| **attendance-admin-client** | Create/update/delete users (Admin API) | Backend (Spring Boot) |

Realm: **keycloak-demo**

---

## 1. Realm

- Create realm **keycloak-demo** in Keycloak admin (http://localhost:8080 → Create realm).

---

## 2. attendance-client (frontend)

Used by the browser for login. Users sign in here and get a token; the backend validates that token.

**In Keycloak admin → Clients → attendance-client:**

| Setting | Value |
|--------|--------|
| Client ID | `attendance-client` |
| Client type | OpenID Connect |
| Client authentication | **OFF** (public client for frontend) |
| Authorization | OFF |
| Standard flow | **ON** (Authorization Code) |
| Direct access grants | Optional (e.g. ON if you use Resource Owner Password) |
| Valid redirect URIs | `http://localhost:5173/*` |
| Valid post logout redirect URIs | `http://localhost:5173/*` |
| Web origins | `http://localhost:5173` |

**Root URL (optional):** `http://localhost:5173`

**Important:** The redirect URI in Keycloak must **exactly** match what the frontend sends (e.g. `http://localhost:5173/`). Use `http://localhost:5173/*` so both `http://localhost:5173` and `http://localhost:5173/` are allowed.  
If you get **400 Bad Request** on login, check this first.

---

## 3. attendance-admin-client (backend)

Used by the Spring Boot app to call Keycloak Admin API (create/update/delete users). Uses **client_credentials** (no user login).

**In Keycloak admin → Clients → attendance-admin-client:**

| Setting | Value |
|--------|--------|
| Client ID | `attendance-admin-client` |
| Client type | OpenID Connect |
| Client authentication | **ON** (confidential) |
| Valid redirect URIs | Not required for client_credentials (can leave blank or set a dummy) |
| Service accounts roles | Enable so this client can be granted realm roles |

**After saving:**

1. Open the **Service account roles** tab (or **Roles**).
2. Assign a role that has **manage-users** (e.g. `realm-management` → **manage-users**, or a custom admin role with user management).
   - In Keycloak 24: **Client roles** → select `realm-management` → assign **manage-users** (and **view-users** if needed) to the service account.

**Get the client secret:**

1. **Credentials** tab.
2. Copy the **Secret** value. Use this as `KEYCLOAK_ADMIN_CLIENT_SECRET` (or in Helm/values).

---

## 4. Backend configuration

The backend needs:

- **JWT validation** (for API requests): tokens are issued by realm **keycloak-demo** for **attendance-client** when users log in on the frontend. Backend only validates the issuer (realm).
- **Admin API** (KeycloakUserService): uses **attendance-admin-client** + secret to get a token and call Keycloak to create/update/delete users.

**Environment variables (or application.yml):**

```properties
# JWT (issuer of tokens from frontend login)
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8080/realms/keycloak-demo

# Admin API (create/update/delete users)
keycloak.auth-server-url=http://localhost:8080
keycloak.realm=keycloak-demo
keycloak.admin.client-id=attendance-admin-client
keycloak.admin.client-secret=<paste secret from Keycloak>
```

**When running in Kubernetes** (backend in cluster, Keycloak in cluster):

- Use `http://keycloak:8080` instead of `http://localhost:8080` for auth-server-url and issuer-uri.
- Set `KEYCLOAK_ADMIN_CLIENT_SECRET` via a Secret (do not put in Git).

---

## 5. Frontend configuration

Already in `attendance-frontend/src/keycloak.js`:

```js
const keycloak = new Keycloak({
  url: "http://localhost:8080",
  realm: "keycloak-demo",
  clientId: "attendance-client",
});
```

No change needed unless you use another realm or client for the frontend.

---

## 6. Fix 400 Bad Request on login

1. **Valid redirect URIs** for **attendance-client** must include the exact redirect_uri the frontend sends. Add:
   - `http://localhost:5173/*`
   - or `http://localhost:5173` and `http://localhost:5173/`
2. **Standard flow** must be **ON** for **attendance-client**.
3. Clear browser cache/cookies for localhost:5173 and localhost:8080 and try again.

---

## 7. Roles for frontend (USER / ADMIN)

So the frontend can show Admin vs User dashboard:

1. **Realm roles** in **keycloak-demo**: create **USER** and **ADMIN** (or **ROLE_USER** and **ROLE_ADMIN** if your app expects that).
2. **Users** → create users → **Role mapping** → assign **USER** or **ADMIN**.
3. Your backend (Spring Security) likely expects roles with prefix `ROLE_` (e.g. `ROLE_ADMIN`). In Keycloak you can either:
   - Create realm roles **ROLE_ADMIN** and **ROLE_USER**, or
   - Use a **Client role** on **attendance-client** named **ROLE_ADMIN** / **ROLE_USER** and assign them to users.

Then the frontend’s `AuthService.isAdmin()` / `isUser()` and the backend’s `hasRole("ADMIN")` will align.
