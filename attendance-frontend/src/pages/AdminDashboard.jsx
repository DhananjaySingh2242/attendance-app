import { useEffect, useState } from "react";
import api from "../api/api";
import AuthService from "../auth/AuthService";
import "./AdminDashboard.css";

const AdminDashboard = ({ keycloak }) => {
  const [activePage, setActivePage] = useState(
    localStorage.getItem("ADMIN_PAGE") || "USERS"
  );

  // USERS
  const [users, setUsers] = useState([]);
  const [userPage, setUserPage] = useState(0);
  const [userTotalPages, setUserTotalPages] = useState(1);
  const [searchEmail, setSearchEmail] = useState("");
  const [isSearching, setIsSearching] = useState(false);
  const [isSearchOpen, setIsSearchOpen] = useState(false);

  const [newUser, setNewUser] = useState({
    email: "",
    password: "",
    firstName: "",
    lastName: "",
  });
  const [registerError, setRegisterError] = useState(null);
  const [editUser, setEditUser] = useState(null);
  const [updateError, setUpdateError] = useState(null);

  // Profile modal
  const [selectedUser, setSelectedUser] = useState(null);

  // ATTENDANCE
  const [attendance, setAttendance] = useState([]);
  const [attPage, setAttPage] = useState(0);
  const [attTotalPages, setAttTotalPages] = useState(1);
  const [searchDate, setSearchDate] = useState("");
  const [isDateSearching, setIsDateSearching] = useState(false);
  const [isDateSearchOpen, setIsDateSearchOpen] = useState(false);

  // USERS API 
  const loadUsers = async (page = 0) => {
    try {
      const res = await api.get(`/api/admin/all-users?page=${page}&size=20`);
      const data = res.data || {};
      const content = Array.isArray(data.content) ? data.content : [];
      setUsers(content);
      const pageNum = data.number ?? data.pageable?.pageNumber ?? page;
      setUserPage((typeof pageNum === "number" && !isNaN(pageNum)) ? pageNum : 0);
      setUserTotalPages(Math.max(1, data.totalPages ?? 1));
      setIsSearching(false);
    } catch (err) {
      console.error("Error fetching users:", err);
      setUsers([]);
    }
  };

  const searchUsers = async () => {
    if (!searchEmail.trim()) {
      loadUsers(0);
      return;
    }
    try {
      const res = await api.get(
        `/api/admin/users/search?keyword=${encodeURIComponent(searchEmail)}`
      );
      setUsers(res.data || []);
      setIsSearching(true);
    } catch (err) {
      console.error("Search failed:", err);
      setUsers([]);
    }
  };

  const registerUser = async (e) => {
    e.preventDefault();
    setRegisterError(null);
    try {
      await api.post("/api/admin/register", {
        email: newUser.email,
        password: newUser.password,
        firstName: newUser.firstName || undefined,
        lastName: newUser.lastName || undefined,
        username: newUser.email,
      });
      setNewUser({ email: "", password: "", firstName: "", lastName: "" });
      loadUsers(0);
    } catch (err) {
      const data = err.response?.data;
      if (data?.message && (data?.details || data?.errorCode)) {
        setRegisterError({
          message: data.message,
          details: data.details,
        });
      } else {
        setRegisterError({
          message: typeof data === "string" ? data : data?.message || "Registration failed",
        });
      }
    }
  };

  const deleteUser = async (id) => {
    await api.delete(`/api/admin/delete/${id}`);
    setSelectedUser(null);
    loadUsers(userPage);
  };

  const updateUser = async (e) => {
    e.preventDefault();
    setUpdateError(null);
    try {
      await api.patch(`/api/admin/update/${editUser.id}`, {
        name: editUser.name,
        password: editUser.password,
      });
      setEditUser(null);
      setSelectedUser(null);
      setUpdateError(null);
      loadUsers(userPage);
    } catch (err) {
      const data = err.response?.data;
      if (data?.message && (data?.details || data?.errorCode)) {
        setUpdateError({
          message: data.message,
          details: data.details,
        });
      } else {
        setUpdateError({
          message: typeof data === "string" ? data : data?.message || "Update failed",
        });
      }
    }
  };

  // ATTENDANCE API 
  const loadAttendance = async (page = 0) => {
    try {
      const res = await api.get(`/api/admin/all-attendance?page=${page}&size=10`);
      setAttendance(res.data?.content || []);
      setAttPage(res.data?.pageable?.pageNumber || 0);
      setAttTotalPages(res.data?.totalPages || 1);
      setIsDateSearching(false);
    } catch (err) {
      console.error("Error fetching attendance:", err);
      setAttendance([]);
    }
  };

  const searchAttendanceByDate = async (page = 0) => {
    if (!searchDate) {
      loadAttendance(0);
      return;
    }
    try {
      const res = await api.get(
        `/api/admin/attendance/search?date=${encodeURIComponent(searchDate)}`
      );
      setAttendance(res.data?.content || res.data || []);
      setIsDateSearching(true);
    } catch (err) {
      console.error("Attendance search failed:", err);
      setAttendance([]);
    }
  };

  useEffect(() => {
    localStorage.setItem("ADMIN_PAGE", activePage);
    if (activePage === "USERS") loadUsers(0);
    if (activePage === "ATTENDANCE") loadAttendance(0);
  }, [activePage]);

  return (
    <div className="admin-page">
      <div className="admin-card">
        <div className="admin-content">
          <h1>Admin Dashboard</h1>

          {/* PAGE SWITCH */}
          <div className="page-switch">
            <button
              className={`btn ${activePage === "USERS" ? "btn-primary" : "btn-secondary"}`}
              onClick={() => setActivePage("USERS")}
            >
              Users
            </button>
            <button
              className={`btn ${activePage === "ATTENDANCE" ? "btn-primary" : "btn-secondary"}`}
              onClick={() => setActivePage("ATTENDANCE")}
            >
              Attendance
            </button>
          </div>

        {/* USERS PAGE  */}
          {activePage === "USERS" && (
            <>
              <h3>Create User</h3>
              <form className="admin-form" onSubmit={registerUser}>
                <input
                  type="email"
                  placeholder="Email *"
                  value={newUser.email}
                  onChange={(e) => setNewUser({ ...newUser, email: e.target.value })}
                  required
                />
                <input
                  type="password"
                  placeholder="Password (min 8 characters) *"
                  value={newUser.password}
                  onChange={(e) => setNewUser({ ...newUser, password: e.target.value })}
                  minLength={8}
                  required
                />
                <input
                  placeholder="First name"
                  value={newUser.firstName}
                  onChange={(e) => setNewUser({ ...newUser, firstName: e.target.value })}
                />
                <input
                  placeholder="Last name"
                  value={newUser.lastName}
                  onChange={(e) => setNewUser({ ...newUser, lastName: e.target.value })}
                />
                {registerError && (
                  <div className="form-error">
                    <p>{registerError.message}</p>
                    {registerError.details && (
                      <ul>
                        {Object.entries(registerError.details).map(([field, msg]) => (
                          <li key={field}>{field}: {msg}</li>
                        ))}
                      </ul>
                    )}
                  </div>
                )}
                <button className="btn btn-primary">Register</button>
              </form>

              {/* SEARCH USERS */}
              <div className="search-box">
                <button className="search-icon-btn" onClick={() => setIsSearchOpen(!isSearchOpen)}>
                  🔎
                </button>
                {isSearchOpen && (
                  <>
                    <input
                      placeholder="Search by name or email"
                      value={searchEmail}
                      onChange={(e) => setSearchEmail(e.target.value)}
                      onKeyDown={(e) => e.key === "Enter" && searchUsers()}
                    />
                    <button className="btn btn-primary" onClick={searchUsers}>
                      Search
                    </button>
                    <button
                      className="btn btn-secondary"
                      onClick={() => {
                        setSearchEmail("");
                        loadUsers(0);
                        setIsSearchOpen(false);
                      }}
                    >
                      Clear
                    </button>
                  </>
                )}
              </div>

              {/* USERS TABLE */}
              <table className="admin-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Role</th>
                  </tr>
                </thead>
                <tbody>
                  {users.length ? (
                    users.map((u) => (
                      <tr key={u.id ?? u.keycloakId} onClick={() => setSelectedUser(u)} style={{ cursor: "pointer" }}>
                        <td>{u.id}</td>
                        <td>{u.name ?? ([u.firstName, u.lastName].filter(Boolean).join(" ") || "—")}</td>
                        <td>{u.email}</td>
                        <td>{Array.isArray(u.role) ? u.role.join(", ") : u.role}</td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan="4" style={{ textAlign: "center" }}>
                        No users found
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>

              {!isSearching && (
                <div className="pagination">
                  <button
                    className="btn btn-secondary"
                    disabled={userPage === 0}
                    onClick={() => loadUsers(userPage - 1)}
                  >
                    Previous
                  </button>
                  <span>
                    Page {userPage + 1} of {userTotalPages}
                  </span>
                  <button
                    className="btn btn-secondary"
                    disabled={userPage + 1 >= userTotalPages}
                    onClick={() => loadUsers(userPage + 1)}
                  >
                    Next
                  </button>
                </div>
              )}
            </>
          )}

          {/* ================= ATTENDANCE PAGE ================= */}
          {activePage === "ATTENDANCE" && (
            <>
              <h3>Attendance Records</h3>

              {/* SEARCH BY DATE */}
              <div className="search-container">
                <button
                  className="search-icon-btn"
                  onClick={() => setIsDateSearchOpen(!isDateSearchOpen)}
                >
                  🔍
                </button>

                {isDateSearchOpen && (
                  <>
                    <input
                      type="date"
                      value={searchDate}
                      onChange={(e) => setSearchDate(e.target.value)}
                      className="search-input"
                    />
                    <button
                      className="btn btn-primary"
                      onClick={() => searchAttendanceByDate(0)}
                    >
                      Submit
                    </button>
                    <button
                      className="btn btn-secondary"
                      onClick={() => {
                        setSearchDate("");
                        loadAttendance(0);
                        setIsDateSearching(false);
                        setIsDateSearchOpen(false);
                      }}
                    >
                      Clear
                    </button>
                  </>
                )}
              </div>

              {/* ATTENDANCE TABLE */}
              <table className="admin-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Email</th>
                    <th>Date</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {attendance.length ? (
                    attendance.map((a) => (
                      <tr key={`${a.keycloakId ?? a.email}-${a.date}`}>
                        <td>{a.keycloakId ?? a.email}</td>
                        <td>{a.email}</td>
                        <td>{new Date(a.date).toLocaleDateString()}</td>
                        <td
                          className={
                            a.status === "PRESENT"
                              ? "status-present"
                              : "status-absent"
                          }
                        >
                          {a.status}
                        </td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan="4" style={{ textAlign: "center" }}>
                        No attendance records found
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>

              {/* ATTENDANCE PAGINATION */}
              {!isDateSearching && (
                <div className="pagination">
                  <button
                    className="btn btn-secondary"
                    disabled={attPage === 0}
                    onClick={() => loadAttendance(attPage - 1)}
                  >
                    Previous
                  </button>
                  <span>
                    Page {attPage + 1} of {attTotalPages}
                  </span>
                  <button
                    className="btn btn-secondary"
                    disabled={attPage + 1 >= attTotalPages}
                    onClick={() => loadAttendance(attPage + 1)}
                  >
                    Next
                  </button>
                </div>
              )}
            </>
          )}
        </div>
      </div>

      {/* LOGOUT BUTTON */}
         <button
        className="btn logout-btn"
        onClick={() => AuthService.logout(keycloak)}
      >
        Logout
      </button>

      {/* ================= PROFILE MODAL ================= */}
      {selectedUser && (
        <div className="profile-modal" onClick={() => setSelectedUser(null)}>
          <div className="profile-card" onClick={(e) => e.stopPropagation()}>
            <img
              src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTq6Pf2w2WEjOMMLauB41tFNcCC72_U_j3lvw&s"
              alt="Profile"
              className="profile-photo"
            />
            <h3>{selectedUser.name}</h3>
            <p><strong>ID:</strong> {selectedUser.id}</p>
            <p><strong>Email:</strong> {selectedUser.email}</p>
            <p><strong>Role:</strong> {selectedUser.role}</p>

            {editUser && editUser.id === selectedUser.id ? (
              <form className="admin-form" onSubmit={updateUser}>
                <input
                  placeholder="Name *"
                  value={editUser.name ?? ""}
                  onChange={(e) => setEditUser({ ...editUser, name: e.target.value })}
                  required
                />
                <input
                  type="password"
                  placeholder="Password (min 4 characters) *"
                  value={editUser.password ?? ""}
                  onChange={(e) => setEditUser({ ...editUser, password: e.target.value })}
                  minLength={4}
                  required
                />
                {updateError && (
                  <div className="form-error">
                    <p>{updateError.message}</p>
                    {updateError.details && (
                      <ul>
                        {Object.entries(updateError.details).map(([field, msg]) => (
                          <li key={field}>{field}: {msg}</li>
                        ))}
                      </ul>
                    )}
                  </div>
                )}
                <div style={{ display: "flex", gap: "10px" }}>
                  <button className="btn btn-primary">Update</button>
                  <button type="button" className="btn btn-secondary" onClick={() => { setEditUser(null); setUpdateError(null); }}>Cancel</button>
                </div>
              </form>
            ) : (
              <div style={{ display: "flex", gap: "10px", marginTop: "10px", justifyContent: "center" }}>
                <button className="btn btn-secondary" onClick={() => setEditUser(selectedUser)}>Edit</button>
                <button className="btn btn-danger" onClick={() => deleteUser(selectedUser.id)}>Delete</button>
                <button className="btn btn-secondary" onClick={() => setSelectedUser(null)}>Close</button>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminDashboard;