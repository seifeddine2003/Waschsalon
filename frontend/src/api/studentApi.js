import API_BASE, { HEADERS } from "../config";

export const login = (email, password) =>
    fetch(`${API_BASE}/students/login`, {
        method: "POST",
        headers: HEADERS,
        body: JSON.stringify({ email, password })
    }).then(res => { if (!res.ok) throw new Error(); return res.json(); });

export const register = (data) =>
    fetch(`${API_BASE}/students/register`, {
        method: "POST",
        headers: HEADERS,
        body: JSON.stringify(data)
    }).then(res => { if (!res.ok) throw new Error(); return res.json(); });