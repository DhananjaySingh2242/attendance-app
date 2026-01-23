import { useEffect, useState } from "react";
import api from "../api/api";
import { logout } from "../auth/AuthService";
import "./AdminDashboard.css";

const AdminDashboard = () => {
  // ================= TAB STATE =================
  const [activePage, setActivePage] = useState(
    localStorage.getItem("ADMIN_PAGE") || "USERS"
  );

  useEffect(() => {
    localStorage.setItem("ADMIN_PAGE", activePage);
  }, [activePage]);

  // ================= USERS STATE =================
  const [users, setUsers] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const [newUser, setNewUser] = useState({
    name: "",
    email: "",
    password: "",
    role: "ROLE_USER",
  });

  const [editUser, setEditUser] = useState(null);

  // ================= ATTENDANCE STATE =================
  const [attendance, setAttendance] = useState([]);
  const [attendancePage, setAttendancePage] = useState(0);
  const [attendanceTotalPages, setAttendanceTotalPages] = useState(0);

  // ================= USERS API =================
  const loadUsers = async (pageNumber = 0) => {
    try {
      const res = await api.get(
        `/api/admin/all-users?page=${pageNumber}&size=10`
      );

      setUsers(res.data?.content || []);
      setPage(res.data?.pageable?.pageNumber || 0);
      setTotalPages(res.data?.totalPages || 1);
    } catch (err) {
      console.error("Error fetching users:", err);
      setUsers([]);
    }
  };

  // ================= ATTENDANCE API =================
  const loadAttendance = async (pageNumber = 0) => {
    try {
      const res = await api.get(
        `/api/admin/all-attendance?page=${pageNumber}&size=10`
      );

      setAttendance(res.data?.content || []);
      setAttendancePage(res.data?.pageable?.pageNumber || 0);
      setAttendanceTotalPages(res.data?.totalPages || 1);
    } catch (err) {
      console.error("Error fetching attendance:", err);
      setAttendance([]);
    }
  };

  // ================= INITIAL LOAD =================
  useEffect(() => {
    loadUsers();
  }, []);

  // Load attendance only when Attendance tab opens
  useEffect(() => {
    if (activePage === "ATTENDANCE") {
      loadAttendance(0);
    }
  }, [activePage]);

  // ================= USER ACTIONS =================
  const registerUser = async (e) => {
    e.preventDefault();
    await api.post("/api/admin/register", newUser);
    setNewUser({ name: "", email: "", password: "", role: "ROLE_USER" });
    loadUsers(page);
  };

  const deleteUser = async (id) => {
    await api.delete(`/api/admin/delete/${id}`);
    loadUsers(page);
  };

  const updateUser = async (e) => {
    e.preventDefault();
    await api.patch(`/api/admin/update/${editUser.id}`, {
      name: editUser.name,
      password: editUser.password,
    });
    setEditUser(null);
    loadUsers(page);
  };

  // ================= UI =================
  return (
    <div className="admin-page">
      <div className="admin-card">
        <h1>Hello Admin</h1>

        {/* ================= PAGE SWITCH ================= */}
        <div style={{ display: "flex", gap: "10px", marginBottom: "20px" }}>
          <button
            className={`btn ${
              activePage === "USERS" ? "btn-primary" : "btn-secondary"
            }`}
            onClick={() => setActivePage("USERS")}
          >
            Users
          </button>

          <button
            className={`btn ${
              activePage === "ATTENDANCE" ? "btn-primary" : "btn-secondary"
            }`}
            onClick={() => setActivePage("ATTENDANCE")}
          >
            Attendance
          </button>
        </div>

        {/* ================= USERS PAGE ================= */}
        {activePage === "USERS" && (
          <>
            <h3>Create User</h3>
            <form className="admin-form" onSubmit={registerUser}>
              <input
                placeholder="Name"
                value={newUser.name}
                onChange={(e) =>
                  setNewUser({ ...newUser, name: e.target.value })
                }
                required
              />
              <input
                placeholder="Email"
                value={newUser.email}
                onChange={(e) =>
                  setNewUser({ ...newUser, email: e.target.value })
                }
                required
              />
              <input
                placeholder="Password"
                type="password"
                value={newUser.password}
                onChange={(e) =>
                  setNewUser({ ...newUser, password: e.target.value })
                }
                required
              />
              <button className="btn btn-primary">Register</button>
            </form>

            <div className="divider" />

            <h3>Users</h3>
            <table className="admin-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Name</th>
                  <th>Email</th>
                  <th>Role</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {users.length > 0 ? (
                  users.map((u) => (
                    <tr key={u.id}>
                      <td>{u.id}</td>
                      <td>{u.name}</td>
                      <td>{u.email}</td>
                      <td>{u.role}</td>
                      <td className="table-actions">
                        <button
                          className="btn btn-secondary"
                          onClick={() => setEditUser(u)}
                        >
                          Edit
                        </button>
                        <button
                          className="btn btn-danger"
                          onClick={() => deleteUser(u.id)}
                        >
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
            <div
              style={{
                marginTop: "20px",
                display: "flex",
                justifyContent: "center",
                gap: "10px",
              }}
            >
              <button
                className="btn btn-secondary"
                disabled={page === 0}
                onClick={() => loadUsers(page - 1)}
              >
                Previous
              </button>
              <span>
                Page {page + 1} of {totalPages}
              </span>
              <button
                className="btn btn-secondary"
                disabled={page + 1 >= totalPages}
                onClick={() => loadUsers(page + 1)}
              >
                Next
              </button>
            </div>
          </>
        )}

        {/* ================= ATTENDANCE PAGE ================= */}
        {activePage === "ATTENDANCE" && (
          <>
            <h3>Attendance Details</h3>

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
                {attendance.length > 0 ? (
                  attendance.map((a) => (
                    <tr key={`${a.userId}-${a.date}`}>
                      <td data-label="ID">{a.userId}</td>
                      <td data-label="Email">{a.email}</td>
                      <td data-label="Date">
                        {new Date(a.date).toLocaleDateString()}
                      </td>
                      <td
                        data-label="Status"
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
            <div
              style={{
                marginTop: "20px",
                display: "flex",
                justifyContent: "center",
                gap: "10px",
              }}
            >
              <button
                className="btn btn-secondary"
                disabled={attendancePage === 0}
                onClick={() => loadAttendance(attendancePage - 1)}
              >
                Previous
              </button>

              <span>
                Page {attendancePage + 1} of {attendanceTotalPages}
              </span>

              <button
                className="btn btn-secondary"
                disabled={attendancePage + 1 >= attendanceTotalPages}
                onClick={() => loadAttendance(attendancePage + 1)}
              >
                Next
              </button>
            </div>
          </>
        )}

        <button className="btn logout-btn" onClick={logout}>
          Logout
        </button>
      </div>
    </div>
  );
};

export default AdminDashboard;
