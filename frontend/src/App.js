import React, { useEffect, useState } from "react";
import WasherCard from "./components/WasherCard";
import "./App.css";
import LoginModal from "./components/LoginModal";
import SignupModal from "./components/SignupModal";

function App() {
    const [washers, setWashers] = useState([]);
    const [loginOpen, setLoginOpen] = useState(false);
    const [signupOpen, setSignupOpen] = useState(false);
    const [user, setUser] = useState(null); // track logged-in user

    // Fetch all washers from backend
    useEffect(() => {
        fetch("http://localhost:8080/washmachines/all")
            .then((res) => res.json())
            .then((data) => {
                console.log("Washers from backend:", data);
                setWashers(data);
            })
            .catch((err) => console.error("Backend error:", err));
    }, []);

    return (
        <div className="dashboard-container">
            <nav className="navbar">
                <div className="logo">🧺 laundryweb</div>
                <div className="nav-links">
                    {user ? (
                        <>
                            <span className="welcome">Welcome, {user.vorname}</span>
                            <button className="logout" onClick={() => setUser(null)}>
                                Logout
                            </button>
                        </>
                    ) : (
                        <>
                            <button className="sign-up" onClick={() => setSignupOpen(true)}>
                                Sign Up
                            </button>
                            <button className="login" onClick={() => setLoginOpen(true)}>
                                Login
                            </button>
                        </>
                    )}
                </div>
            </nav>

            {/* Login / Signup modals */}
            <LoginModal
                isOpen={loginOpen}
                onClose={() => setLoginOpen(false)}
                onLogin={setUser}
            />

            <SignupModal
                isOpen={signupOpen}
                onClose={() => setSignupOpen(false)}
            />

            <header className="hero">
                <p className="subtitle">MANAGE YOUR TIME EFFICIENTLY</p>
                <h1>Pick a machine, start washing!</h1>
                <p className="description">
                    Select an available machine and start washing easily.
                </p>
            </header>

            <div className="grid">
                {washers.map((washer) => (
                    <WasherCard key={washer.id} washer={washer} user={user} />
                ))}
            </div>
        </div>
    );
}

export default App;
