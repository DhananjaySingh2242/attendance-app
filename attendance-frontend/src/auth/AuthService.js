export const getToken = () => localStorage.getItem("token");

export const parseJwt = (token) => {
  const base64 = token.split(".")[1];
  return JSON.parse(atob(base64));
};

export const getRole = () => {
  const token = getToken();
  if (!token) return null;
  return parseJwt(token).role;
};

export const logout = () => {
  localStorage.removeItem("token");
  window.location.href = "/login";
};
