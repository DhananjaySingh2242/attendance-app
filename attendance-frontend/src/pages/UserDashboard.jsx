import { useEffect, useState } from "react";
import Calendar from "react-calendar";
import "react-calendar/dist/Calendar.css";
import api from "../api/api";
import { logout } from "../auth/AuthService";
import "./UserDashboard.css";

const UserDashboard = () => {
  const [user, setUser] = useState(null);
  const [attendance, setAttendance] = useState([]);
  const [message, setMessage] = useState("");
  const [messageType, setMessageType] = useState("success");
  const [showMessage, setShowMessage] = useState(false);
  const [showCalendar, setShowCalendar] = useState(false);

  useEffect(() => {
    api.get("/api/auth/me")
      .then(res => setUser(res.data))
      .catch(err => console.error(err));

    api.get("/api/auth/my-attendance")
      .then(res => setAttendance(res.data))
      .catch(err => console.error(err));
  }, []);

  const showPopup = (msg, type = "success") => {
    setMessage(msg);
    setMessageType(type);
    setShowMessage(true);
    setTimeout(() => setShowMessage(false), 3000);
  };

  const handleCheckIn = async () => {
    try {
      const res = await api.post("/api/attendance/check-in");
      showPopup(res.data, "success");
    } catch (err) {
      showPopup(err.response?.data || "Check-In failed", "error");
    }
  };

  const handleCheckOut = async () => {
    try {
      const res = await api.post("/api/attendance/check-out");
      showPopup(res.data, "success");
    } catch (err) {
      showPopup(err.response?.data || "Check-Out failed", "error");
    }
  };

  const formatDate = (date) =>
    date.toLocaleDateString("en-CA"); // yyyy-mm-dd

  const attendanceMap = attendance.reduce((acc, item) => {
    console.log(item.status, "stat")
    acc[item.date] = item.status.slice(0,1);
    return acc;
  }, {});

  return (
    <div className="dashboard-page">
      {showMessage && (
        <div className={`popup-message ${messageType === "error" ? "error" : ""}`}>
          {message}
        </div>
      )}

      <button className="logout-btn" onClick={logout}>Logout</button>

      {/* Calendar Icon */}
      <div
        className="calendar-icon"
        onClick={() => setShowCalendar(!showCalendar)}
        title="View Attendance"
      >
        📅
      </div>

      {showCalendar && (
        <div className="calendar-popup">
          <Calendar
            tileClassName={({ date, view }) => {
              if (view !== "month") return null;
              const status = attendanceMap[formatDate(date)];
              if (status === "P") return "present-day";
              if (status === "H") return "half-day";
              if (status === "A") return "absent-day";
              return null;
            }}
            tileContent={({ date, view }) => {
              if (view !== "month") return null;
              const status = attendanceMap[formatDate(date)];
              if (!status) return null;
              return (
                <div className={`status-label ${status.toLowerCase()}`}>
                  {status}
                </div>
              );
            }}
          />
        </div>
      )}

      <div className="dashboard-card">
        {user && (
          <p>
            Welcome <strong>{user.name} 👋🏻</strong><br />
            <span>{user.email}</span>
          </p>
        )}

        <div className="dashboard-buttons">
          <button className="btn checkin" onClick={handleCheckIn}>Check-In</button>
          <button className="btn checkout" onClick={handleCheckOut}>Check-Out</button>
        </div>
      </div>
    </div>
  );
};

export default UserDashboard;
