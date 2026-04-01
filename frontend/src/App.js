import React, { useEffect, useState } from "react";
import "./App.css";
import API_BASE, { HEADERS } from "./config";
import WasherCard from "./components/WasherCard";
import LoginModal from "./components/LoginModal";
import SignupModal from "./components/SignupModal";
import ReservationModal from "./components/ReservationModal";
import BalanceModal from "./components/BalanceModal";

function App() {
    const [washers, setWashers] = useState([]);
    const [user, setUser] = useState(null);
    const [reservingWasher, setReservingWasher] = useState(null);
    const [loginOpen, setLoginOpen] = useState(false);
    const [signupOpen, setSignupOpen] = useState(false);
    const [balanceOpen, setBalanceOpen] = useState(false);

    useEffect(() => {
        fetch(`${API_BASE}/washmachines/all`, { headers: HEADERS })
            .then(res => res.json())
            .then(data => setWashers(data))
            .catch(err => console.error("Could not load machines:", err));
    }, []);

    const handleBalanceUpdate = (newBalance) => {
        setUser(prev => ({ ...prev, balance: newBalance }));
    };

    return (
        <div className="dashboard-container">
            <nav className="navbar">
                <div className="logo">🧺 laundryweb</div>
                <div className="nav-links">
                    {user ? (
                        <>
                            <span className="welcome">
                                Welcome, {user.vorname} ·{" "}
                                <button className="balance-btn" onClick={() => setBalanceOpen(true)}>
                                    💳 €{user.balance.toFixed(2)}
                                </button>
                            </span>
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

            <BalanceModal
                isOpen={balanceOpen}
                user={user}
                onClose={() => setBalanceOpen(false)}
                onBalanceUpdate={handleBalanceUpdate}
            />
            <LoginModal
                isOpen={loginOpen}
                onClose={() => setLoginOpen(false)}
                onLogin={setUser}
            />
            <SignupModal
                isOpen={signupOpen}
                onClose={() => setSignupOpen(false)}
            />
            <ReservationModal
                isOpen={!!reservingWasher}
                washer={reservingWasher}
                user={user}
                onClose={() => setReservingWasher(null)}
                onBalanceUpdate={handleBalanceUpdate}
            />

            <header className="hero">
                <p className="subtitle">MANAGE YOUR TIME EFFICIENTLY</p>
                <h1>Pick a machine, start washing!</h1>
                <p className="description">
                    Select an available machine and start washing easily.
                </p>
            </header>

            <div className="grid">
                {washers.map(washer => (
                    <WasherCard
                        key={washer.id}
                        washer={washer}
                        user={user}
                        onReserve={setReservingWasher}
                    />
                ))}
            </div>
        </div>
    );
}

export default App;