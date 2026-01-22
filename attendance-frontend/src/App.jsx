import { BrowserRouter, Routes, Route } from "react-router-dom";
import Login from "./pages/Login";
import UserDashboard from "./pages/UserDashboard";
import AdminDashboard from "./pages/AdminDashboard";
import ProtectedRoute from "./auth/ProtectedRoute";
import { Outlet } from "react-router-dom";

function App() {
  return (
   <BrowserRouter>
  <Routes>
    <Route path="/" element={<Login />} />
    <Route path="/login" element={<Login />} />

    <Route path="/user" element={
      <ProtectedRoute role="ROLE_USER">
        <UserDashboard />
      </ProtectedRoute>
    } />

    <Route path="/admin" element={
      <ProtectedRoute role="ROLE_ADMIN">
        <AdminDashboard />
      </ProtectedRoute>
    } />
  </Routes>
</BrowserRouter>

  );
}
export default App;
