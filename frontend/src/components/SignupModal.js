import React, { useState } from "react";
import { register } from "../api/studentApi";

export default function SignupModal({ isOpen, onClose }) {
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [message, setMessage] = useState("");
    const [isError, setIsError] = useState(false);

    if (!isOpen) return null;

    const getPasswordStrength = (pwd) => {
        if (pwd.length === 0) return null;
        let score = 0;
        if (pwd.length >= 8) score++;
        if (/[A-Z]/.test(pwd)) score++;
        if (/[0-9]/.test(pwd)) score++;
        if (/[^A-Za-z0-9]/.test(pwd)) score++;
        if (score <= 1) return { label: "Weak", color: "#e74c3c" };
        if (score === 2) return { label: "Fair", color: "#e67e22" };
        if (score === 3) return { label: "Good", color: "#f1c40f" };
        return { label: "Strong", color: "#2ecc71" };
    };

    const strength = getPasswordStrength(password);

    const handleSubmit = () => {
        if (password.length < 8) {
            setMessage("Password must be at least 8 characters.");
            setIsError(true);
            return;
        }
        register({ vorname: firstName, nachname: lastName, email, password })
            .then(() => {
                setMessage("Sign up successful!");
                setIsError(false);
                setFirstName("");
                setLastName("");
                setEmail("");
                setPassword("");
            })
            .catch(() => {
                setMessage("Sign up failed. Please try again.");
                setIsError(true);
            });
    };

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal" onClick={e => e.stopPropagation()}>
                <h2>Sign Up</h2>

                <input
                    type="text"
                    placeholder="First Name"
                    className="modal-input"
                    value={firstName}
                    onChange={e => setFirstName(e.target.value)}
                />
                <input
                    type="text"
                    placeholder="Last Name"
                    className="modal-input"
                    value={lastName}
                    onChange={e => setLastName(e.target.value)}
                />
                <input
                    type="email"
                    placeholder="Email"
                    className="modal-input"
                    value={email}
                    onChange={e => setEmail(e.target.value)}
                />
                <input
                    type="password"
                    placeholder="Password (min. 8 characters)"
                    className="modal-input"
                    value={password}
                    onChange={e => setPassword(e.target.value)}
                />
                {strength && (
                    <p style={{ margin: "4px 0 8px", fontSize: "13px", color: strength.color }}>
                        Password strength: {strength.label}
                    </p>
                )}

                <button className="modal-btn" onClick={handleSubmit}>
                    Sign Up
                </button>

                {message && (
                    <p className={`modal-message ${isError ? "error" : "success"}`}>
                        {message}
                    </p>
                )}

                <button className="modal-close" onClick={onClose}>Cancel</button>
            </div>
        </div>
    );
}