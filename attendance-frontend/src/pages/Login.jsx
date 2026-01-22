import api from "../api/api";
import { parseJwt } from "../auth/AuthService";
import { useNavigate } from "react-router-dom";
import "./Login.css";

const Login = () => {
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();

    const email = e.target.email.value;
    const password = e.target.password.value;

    const res = await api.post("/api/auth/login", { email, password });

    localStorage.setItem("token", res.data.token);

    const role = parseJwt(res.data.token).role;

    if (role === "ROLE_ADMIN" || role === "ADMIN") {
      navigate("/admin");
    } else {
      navigate("/user");
    }
  };

    return (
    <div className="login-container">
      <form className="login-card" onSubmit={handleLogin}>
        <h2>Welcome</h2>
        <p className="subtitle">Login to your account</p>

        <input
          type="email"
          name="email"
          placeholder="Email address"
          required
        />

        <input
          type="password"
          name="password"
          placeholder="Password"
          required
        />

        <button type="submit">Login</button>
      </form>
    </div>
  );
};

export default Login;
