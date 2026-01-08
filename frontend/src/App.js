import React from "react";
import washerData from "./data/washerData";
import WasherCard from "./components/WasherCard";
import "./App.css";

function App() {
  return (
      <div className="dashboard-container">
        <nav className="navbar">
          <div className="logo">🧺 laundryweb</div>
          <div className="nav-links">
            <button className="sign-in">Sign In</button>
            <button className="sign-up">Sign Up</button>
          </div>
        </nav>

        <header className="hero">
          <p className="subtitle">MANAGE YOUR TIME EFFICIENTLY</p>
          <h1>Pick a machine, start washing!</h1>
          <p className="description">
            Select an available machine and start washing easily.
          </p>
        </header>

        <div className="grid">
          {washerData.map(washer => (
              <WasherCard key={washer.id} washer={washer} />
          ))}
        </div>
      </div>
  );
}

export default App;
