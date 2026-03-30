import React, { useState } from "react";
import { login } from "../api/studentApi";

export default function LoginModal({ isOpen, onClose, onLogin }) {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");

    if (!isOpen) return null;

    const handleLogin = () => {
        setError("");
        login(email, password)
            .then(user => {
                onLogin(user);
                onClose();
            })
            .catch(() => setError("Invalid email or password."));
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

                {error && <p className="modal-message error">{error}</p>}

                <button className="modal-close" onClick={onClose}>Cancel</button>
            </div>
        </div>
    );
}