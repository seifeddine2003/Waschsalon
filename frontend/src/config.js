/**
 * Frontend Configuration File
 * 
 * Defines global configuration variables, Stripe public key, and utility header functions
 * used across API requests in the application.
 */

// Determine the API base URL depending on whether the app is running in production or development
const API_BASE = process.env.NODE_ENV === "production"
    ? "https://waschsalon.onrender.com"
    : "http://localhost:8080";

// Stripe Public Key for processing payments in test mode
export const STRIPE_PUBLIC_KEY = "pk_test_51THMdi2OS2GoTyNAhCgTArqf0qQWs2OUQC3I84CNwBvJlNnwXIXY4Jqxyu2nvdmu70ddhtRKRCEJU1g1y2YTaeK600MYEbKiNJ";

/**
 * Helper function to generate standard HTTP headers for API requests.
 * Automatically retrieves and attaches the JWT authorization token from localStorage if available,
 * and includes headers for content type and ngrok warning bypass.
 */
export const getHeaders = () => {
    const token = localStorage.getItem("token");
    return {
        "Content-Type": "application/json",
        "ngrok-skip-browser-warning": "true",
        ...(token ? { "Authorization": `Bearer ${token}` } : {})
    };
};

export default API_BASE;
