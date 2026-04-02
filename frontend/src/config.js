const API_BASE = process.env.NODE_ENV === "production"
    ? "https://waschsalon.onrender.com"
    : "http://localhost:8080";

export const STRIPE_PUBLIC_KEY = "pk_test_51THMdi2OS2GoTyNAhCgTArqf0qQWs2OUQC3I84CNwBvJlNnwXIXY4Jqxyu2nvdmu70ddhtRKRCEJU1g1y2YTaeK600MYEbKiNJ";

export const getHeaders = () => {
    const token = localStorage.getItem("token");
    return {
        "Content-Type": "application/json",
        "ngrok-skip-browser-warning": "true",
        ...(token ? { "Authorization": `Bearer ${token}` } : {})
    };
};

export default API_BASE;
