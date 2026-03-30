const API_BASE = process.env.NODE_ENV === "production"
    ? "https://waschsalon.onrender.com"
    : "http://localhost:8080";

export const HEADERS = {
    "Content-Type": "application/json",
    "ngrok-skip-browser-warning": "true"
};

export default API_BASE;