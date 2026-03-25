import React, { useState } from "react";
import API_BASE, { HEADERS } from "../config";

export default function LoginModal({ isOpen, onClose, onLogin }) {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    if (!isOpen) return null;

    const handleLogin = () => {
        fetch(`${API_BASE}/students/login`, {
            method: "POST",
            headers: HEADERS,
            body: JSON.stringify({ email, password })
        })
            .then(res => {
                if (!res.ok) {
                    alert("Invalid email or password");
                    throw new Error("Invalid credentials");
                }
                return res.json();
            })
            .then(user => {
                if (!user) {
                    alert("Invalid email or password");
                    return;
                }
                console.log("Logged in:", user);
                onLogin(user);
                onClose();
            })
            .catch(err => console.error("Login error:", err));
    };

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal" onClick={e => e.stopPropagation()}>
                <h2>Login</h2>

                <input
                    type="email"
                    placeholder="Email"
                    className="modal-input"
                    value={email}
                    onChange={e => setEmail(e.target.value)}
                />
                <input
                    type="password"
                    placeholder="Password"
                    className="modal-input"
                    value={password}
                    onChange={e => setPassword(e.target.value)}
                />

                <button className="modal-btn" onClick={handleLogin}>Login</button>
                <button className="modal-close" onClick={onClose}>Cancel</button>
            </div>
        </div>
    );
}