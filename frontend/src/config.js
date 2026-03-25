const API_BASE = process.env.NODE_ENV === "production"
    ? ""  // in production (built), same host serves everything
    : "http://localhost:8080";  // in development (npm start), use localhost

export const HEADERS = {
    "Content-Type": "application/json",
    "ngrok-skip-browser-warning": "true"
};

export default API_BASE;