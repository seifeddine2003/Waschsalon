import API_BASE, { getHeaders } from "../config";

export const login = (email, password) =>
    fetch(`${API_BASE}/students/login`, {
        method: "POST",
        headers: getHeaders(),
        body: JSON.stringify({ email, password })
    }).then(res => {
        if (!res.ok) throw new Error();
        return res.json();
    }).then(data => {
        localStorage.setItem("token", data.token);
        return data;
    });

export const register = (data) =>
    fetch(`${API_BASE}/students/register`, {
        method: "POST",
        headers: getHeaders(),
        body: JSON.stringify(data)
    }).then(res => { if (!res.ok) throw new Error(); return res.json(); });

export const loadBalance = (studentId, amountEuros) =>
    fetch(`${API_BASE}/students/${studentId}/balance/load`, {
        method: "POST",
        headers: getHeaders(),
        body: JSON.stringify({ amount: amountEuros })
    }).then(res => { if (!res.ok) return res.json().then(e => { throw new Error(e.error); }); return res.json(); });

export const getMyReservations = (studentId) =>
    fetch(`${API_BASE}/reservations/student/${studentId}`, {
        headers: getHeaders()
    }).then(res => { if (!res.ok) throw new Error(); return res.json(); });

export const cancelReservation = (reservationId, studentId) =>
    fetch(`${API_BASE}/reservations/${reservationId}/cancel?studentId=${studentId}`, {
        method: "DELETE",
        headers: getHeaders()
    }).then(res => { if (!res.ok) return res.json().then(e => { throw new Error(e.error || "Cancel failed"); }); return res.json(); });
