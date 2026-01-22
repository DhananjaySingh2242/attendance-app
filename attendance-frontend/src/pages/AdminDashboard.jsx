import { useEffect, useState } from "react";
import api from "../api/api";
import { logout } from "../auth/AuthService";
import "./AdminDashboard.css";

const AdminDashboard = () => {
  const [users, setUsers] = useState([]);
  const [page, setPage] = useState(0); // current page
  const [totalPages, setTotalPages] = useState(0);

  const [newUser, setNewUser] = useState({
    name: "",
    email: "",
    password: "",
    role: "ROLE_USER",
  });

  const [editUser, setEditUser] = useState(null);
  const loadUsers = async (pageNumber = 0) => {
    try {
      const res = await api.get(`/api/admin/all-users?page=${pageNumber}&size=10`);
      console.log("Backend Response:", res.data); // Check what you actually get
      setUsers(res.data.content || []); // content must exist
      setPage(res.data.pageable.pageNumber || 0);
      setTotalPages(res.data.totalPages || 1);
    } catch (err) {
      console.error("Error fetching users:", err);
    }
  };


  useEffect(() => {
    loadUsers();
  }, []);

  // REGISTER USER
  const registerUser = async (e) => {
    e.preventDefault();
    await api.post("/api/admin/register", newUser);
    setNewUser({ name: "", email: "", password: "" });
    loadUsers(page); // reload current page
  };

  // DELETE USER
  const deleteUser = async (id) => {
    await api.delete(`/api/admin/delete/${id}`);
    loadUsers(page); // reload current page
  };

  // UPDATE USER
  const updateUser = async (e) => {
    e.preventDefault();
    await api.patch(`/api/admin/update/${editUser.id}`, {
      name: editUser.name,
      password: editUser.password,
    });
    setEditUser(null);
    loadUsers(page);
  };

  return (
    <div className="admin-page">
      <div className="admin-card">
        <h1>Hello Admin</h1>

        {/* REGISTER FORM */}
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
            placeholder="Password"
            type="password"
            value={newUser.password}
            onChange={(e) =>
              setNewUser({ ...newUser, password: e.target.value })
            }
            required
          />
          <select
            value={newUser.role}
            onChange={(e) => setNewUser({ ...newUser, role: e.target.value })}
          >
            <option value="ROLE_USER">USER</option>
            <option value="ROLE_ADMIN">ADMIN</option>
          </select>
          <button className="btn btn-primary" type="submit">
            Register
          </button>
        </form>

        <div className="divider" />

        {/* USERS TABLE */}
        <h3>All Users</h3>
        <table className="admin-table">
          <thead>
            <tr>
              <th>ID</th><th>Name</th><th>Email</th><th>Role</th><th>Action</th>
            </tr>
          </thead>
          <tbody>
            {users.map((u) => (
              <tr key={u.id}>
                <td>{u.id}</td>
                <td>{u.name}</td>
                <td>{u.email}</td>
                <td>{u.role}</td>
                <td className="table-actions">
                  <button className="btn btn-secondary" onClick={() => setEditUser(u)}>Edit</button>
                  <button className="btn btn-danger" onClick={() => deleteUser(u.id)}>Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        {/* PAGINATION BUTTONS */}
        <div style={{ marginTop: "20px", display: "flex", justifyContent: "center", gap: "10px" }}>
          <button
            className="btn btn-secondary"
            disabled={page === 0}
            onClick={() => loadUsers(page - 1)}
          >
            Previous
          </button>
          <span>Page {page + 1} of {totalPages}</span>
          <button
            className="btn btn-secondary"
            disabled={page + 1 >= totalPages}
            onClick={() => loadUsers(page + 1)}
          >
            Next
          </button>
        </div>

        {/* UPDATE BOX */}
        {editUser && (
          <div className="update-box">
            <h3>Update User</h3>
            <form className="admin-form" onSubmit={updateUser}>
              <input
                value={editUser.name}
                onChange={(e) =>
                  setEditUser({ ...editUser, name: e.target.value })
                }
                required
              />
              <input
                type="password"
                placeholder="Enter new password"
                value={editUser.password || ""}
                onChange={(e) =>
                  setEditUser({ ...editUser, password: e.target.value })
                }
              />

              <button className="btn btn-primary" type="submit">Update</button>
              <button
                type="button"
                className="btn btn-secondary"
                onClick={() => setEditUser(null)}
              >
                Cancel
              </button>
            </form>
          </div>
        )}

        <button className="btn logout-btn" onClick={logout}>Logout</button>
      </div>
    </div>
  );
};

export default AdminDashboard;