import AuthService from "../auth/AuthService";

const Unauthorized = ({ keycloak }) => {
  return (
    <div style={{ padding: "2rem", textAlign: "center", maxWidth: "400px", margin: "2rem auto" }}>
      <h2>🚫 Unauthorized</h2>
      <p>You do not have permission to access this page. You may need a role (USER or ADMIN) assigned in Keycloak.</p>
      <button
        className="btn btn-primary"
        style={{ marginTop: "1rem" }}
        onClick={() => AuthService.logout(keycloak)}
      >
        Back to Login
      </button>
    </div>
  );
};

export default Unauthorized;
