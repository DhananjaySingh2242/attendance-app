import { useEffect, useState } from "react";
import { FiSearch, FiX } from "react-icons/fi";
import api from "../api/api";
import { logout } from "../auth/AuthService";
import "./AdminDashboard.css";

const AdminDashboard = () => {
  // ================= TAB STATE =================
  const [activePage, setActivePage] = useState(
    localStorage.getItem("ADMIN_PAGE") || "USERS"
  );

  // ================= USERS STATE =================
  const [users, setUsers] = useState([]);
  const [userPage, setUserPage] = useState(0);
  const [userTotalPages, setUserTotalPages] = useState(1);
  const [searchEmail, setSearchEmail] = useState("");
  const [isSearching, setIsSearching] = useState(false);
  const [showSearch, setShowSearch] = useState(false);

  const [newUser, setNewUser] = useState({
    name: "",
    email: "",
    password: "",
  });

  const [editUser, setEditUser] = useState(null);

  // ================= ATTENDANCE STATE =================
  const [attendance, setAttendance] = useState([]);
  const [attPage, setAttPage] = useState(0);
  const [attTotalPages, setAttTotalPages] = useState(1);

  // ================= USERS API =================
  const loadUsers = async (page = 0) => {
    try {
      const res = await api.get(`/api/admin/all-users?page=${page}&size=10`);
      setUsers(res.data?.content || []);
      setUserPage(res.data?.pageable?.pageNumber || 0);
      setUserTotalPages(res.data?.totalPages || 1);
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

      setUsers(res.data || []); // backend returns List<UserResponse>
      setIsSearching(true);
    } catch (err) {
      console.error("Search failed:", err);
      setUsers([]);
    }
  };

  const registerUser = async (e) => {
    e.preventDefault();
    try {
      await api.post("/api/admin/register", newUser);
      setNewUser({ name: "", email: "", password: "" });
      loadUsers(userPage);
    } catch (err) {
      console.error("User registration failed", err);
    }
  };

  const deleteUser = async (id) => {
    await api.delete(`/api/admin/delete/${id}`);
    loadUsers(userPage);
  };

  const updateUser = async (e) => {
    e.preventDefault();
    try {
      await api.patch(`/api/admin/update/${editUser.id}`, {
        name: editUser.name,
        password: editUser.password || null,
      });
      setEditUser(null);
      loadUsers(userPage);
    } catch (err) {
      console.error("Update failed", err);
    }
  };

  // ================= ATTENDANCE API =================
  const loadAttendance = async (page = 0) => {
    try {
      const res = await api.get(`/api/admin/all-attendance?page=${page}&size=10`);
      setAttendance(res.data?.content || []);
      setAttPage(res.data?.pageable?.pageNumber || 0);
      setAttTotalPages(res.data?.totalPages || 1);
    } catch (err) {
      console.error("Error fetching attendance:", err);
      setAttendance([]);
    }
  };

  // ================= LOAD DATA =================
  useEffect(() => {
    localStorage.setItem("ADMIN_PAGE", activePage);
    if (activePage === "USERS") loadUsers(0);
    if (activePage === "ATTENDANCE") loadAttendance(0);
  }, [activePage]);

  // ================= RENDER =================
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

          {/* ================= USERS ================= */}
          {activePage === "USERS" && (
            <>
              <h3>Create User</h3>
              <form className="admin-form" onSubmit={registerUser}>
                <input
                  placeholder="Name"
                  value={newUser.name}
                  onChange={(e) => setNewUser({ ...newUser, name: e.target.value })}
                  required
                />
                <input
                  placeholder="Email"
                  value={newUser.email}
                  onChange={(e) => setNewUser({ ...newUser, email: e.target.value })}
                  required
                />
                <input
                  type="password"
                  placeholder="Password"
                  value={newUser.password}
                  onChange={(e) => setNewUser({ ...newUser, password: e.target.value })}
                  required
                />
                <button className="btn btn-primary">Register</button>
              </form>

              {editUser && (
                <>
                  <div className="divider" />
                  <h3>Edit User</h3>
                  <form className="admin-form" onSubmit={updateUser}>
                    <input
                      value={editUser.name}
                      onChange={(e) => setEditUser({ ...editUser, name: e.target.value })}
                      required
                    />
                    <input
                      type="password"
                      placeholder="New Password"
                      value={editUser.password || ""}
                      onChange={(e) => setEditUser({ ...editUser, password: e.target.value })}
                    />
                    <div style={{ display: "flex", gap: "10px" }}>
                      <button className="btn btn-primary">Update</button>
                      <button
                        type="button"
                        className="btn btn-secondary"
                        onClick={() => setEditUser(null)}
                      >
                        Cancel
                      </button>
                    </div>
                  </form>
                </>
              )}

              <div className="divider" />

              {/* SEARCH ICON + SEARCH BOX */}
              <h3>Users List</h3>
              <div className="search-container">
                {!showSearch && (
                  <button
                    className="search-icon-btn"
                    onClick={() => setShowSearch(true)}
                    title="Search users"
                  >
                    <FiSearch size={20} />
                  </button>
                )}

                {showSearch && (
                  <div className="search-box">
                    <input
                      autoFocus
                      placeholder="Search by name or email"
                      value={searchEmail}
                      onChange={(e) => setSearchEmail(e.target.value)}
                      onKeyDown={(e) => e.key === "Enter" && searchUsers()}
                    />
                    <button className="btn btn-primary" onClick={searchUsers}>
                      Search
                    </button>
                    <button
                      className="search-close-btn"
                      onClick={() => {
                        setShowSearch(false);
                        setSearchEmail("");
                        loadUsers(0);
                      }}
                    >
                      <FiX size={18} />
                    </button>
                  </div>
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
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {users.length ? (
                    users.map((u) => (
                      <tr key={u.id}>
                        <td>{u.id}</td>
                        <td>{u.name}</td>
                        <td>{u.email}</td>
                        <td>{u.role}</td>
                        <td>
                          <button className="btn btn-secondary" onClick={() => setEditUser(u)}>
                            Edit
                          </button>
                          <button className="btn btn-danger" onClick={() => deleteUser(u.id)}>
                            Delete
                          </button>
                        </td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan="5" style={{ textAlign: "center" }}>
                        No users found
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>

              {/* USERS PAGINATION */}
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

          {/* ================= ATTENDANCE ================= */}
          {activePage === "ATTENDANCE" && (
            <>
              <h3>Attendance Records</h3>
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
                      <tr key={`${a.userId}-${a.date}`}>
                        <td>{a.userId}</td>
                        <td>{a.email}</td>
                        <td>{new Date(a.date).toLocaleDateString()}</td>
                        <td className={a.status === "PRESENT" ? "status-present" : "status-absent"}>
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
            </>
          )}
        </div>
      </div>

      <button className="btn logout-btn" onClick={logout}>
        Logout
      </button>
    </div>
  );
};

export default AdminDashboard;
