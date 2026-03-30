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

    const handleSubmit = () => {
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
                    placeholder="Password"
                    className="modal-input"
                    value={password}
                    onChange={e => setPassword(e.target.value)}
                />

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