import { useEffect, useState } from "react";
import api from "../api/api";
import { logout } from "../auth/AuthService";
import "./UserDashboard.css";

const UserDashboard = () => {
  const [user, setUser] = useState(null);
  const [message, setMessage] = useState(""); // popup text
  const [messageType, setMessageType] = useState("success"); // "success" or "error"
  const [showMessage, setShowMessage] = useState(false);

  useEffect(() => {
    api.get("/api/auth/me")
      .then(res => setUser(res.data))
      .catch(err => console.error(err));
  }, []);

  const showPopup = (msg, type = "success") => {
    setMessage(msg);
    setMessageType(type); // success or error
    setShowMessage(true);
    setTimeout(() => setShowMessage(false), 3000); // hide after 3s
  };

  const handleCheckIn = async () => {
    try {
      const res = await api.post("/api/attendance/check-in");
      showPopup(res.data, "success"); // green popup
    } catch (err) {
      showPopup(err.response?.data || "Check-In failed", "error"); // red popup
      console.error(err);
    }
  };

  const handleCheckOut = async () => {
    try {
      const res = await api.post("/api/attendance/check-out");
      showPopup(res.data, "success"); // green popup
    } catch (err) {
      showPopup(err.response?.data || "Check-Out failed", "error"); // red popup
      console.error(err);
    }
  };

  return (
    <div className="dashboard-page">
      {showMessage && (
        <div className={`popup-message ${messageType === "error" ? "error" : ""}`}>
          {message}
        </div>
      )}

      <div className="dashboard-card">
        {user && (
          <p>
            Welcome <strong>{user.name}</strong><br />
            <span>{user.email}</span>
          </p>
        )}

        <div className="dashboard-buttons">
          <button className="btn checkin" onClick={handleCheckIn}>Check-In</button>
          <button className="btn checkout" onClick={handleCheckOut}>Check-Out</button>
          <button className="btn logout" onClick={logout}>Logout</button>
        </div>
      </div>
    </div>
  );
};

export default UserDashboard;
