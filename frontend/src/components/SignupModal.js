import React, { useState } from "react";
import API_BASE, { HEADERS } from "../config";

export default function SignupModal({ isOpen, onClose }) {
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [message, setMessage] = useState("");
    const [isError, setIsError] = useState(false);

    if (!isOpen) return null;

    const handleSubmit = () => {
        const studentData = {
            vorname: firstName,
            nachname: lastName,
            email: email,
            password: password
        };

        fetch(`${API_BASE}/students/register`, {
            method: "POST",
            headers: HEADERS,
            body: JSON.stringify(studentData),
        })
            .then(res => {
                if (!res.ok) throw new Error("Failed to create student");
                return res.json();
            })
            .then(data => {
                console.log("Student created:", data);
                setMessage("✅ Sign up successful!");
                setIsError(false);
                setFirstName("");
                setLastName("");
                setEmail("");
                setPassword("");
            })
            .catch(err => {
                console.error("Signup error:", err);
                setMessage("❌ Sign up failed. Try again.");
                setIsError(true);
            });
    };

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal" onClick={e => e.stopPropagation()}>
                <h2>Sign Up</h2>

                <input type="text" placeholder="First Name" className="modal-input" value={firstName} onChange={e => setFirstName(e.target.value)} />
                <input type="text" placeholder="Last Name" className="modal-input" value={lastName} onChange={e => setLastName(e.target.value)} />
                <input type="email" placeholder="Email" className="modal-input" value={email} onChange={e => setEmail(e.target.value)} />
                <input type="password" placeholder="Password" className="modal-input" value={password} onChange={e => setPassword(e.target.value)} />

                <button className="modal-btn" onClick={handleSubmit}>Sign Up</button>

                {message && <p className={`modal-message ${isError ? "error" : "success"}`}>{message}</p>}

                <button className="modal-close" onClick={onClose}>Cancel</button>
            </div>
        </div>
    );
}