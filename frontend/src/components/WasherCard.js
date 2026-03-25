import React, { useState } from "react";
import openWasher from "../images/washer-open.png";
import closedWasher from "../images/washer-closed.png";
import defectWasher from "../images/washer-defect.png";
import ReservationModal from "./ReservationModal";

export default function WasherCard({ washer, user }) {
    const [modalOpen, setModalOpen] = useState(false);

    const getWasherImage = () => {
        switch (washer.status) {
            case "Available":    return openWasher;
            case "In Use":       return closedWasher;
            case "Out of Order": return defectWasher;
            default:             return closedWasher;
        }
    };

    const handleStart = () => {
        if (!user) {
            alert("Please log in to make a reservation.");
            return;
        }
        setModalOpen(true);
    };

    return (
        <>
            <div className="card">
                <div className="card-header">
                    <span className="washer-name">{washer.name}</span>
                    <span className={`status-pill ${washer.status.replace(" ", "-")}`}>
                        {washer.status}
                    </span>
                </div>

                <div className="card-body">
                    {washer.status === "In Use" && (
                        <div>
                            <p>⏳ Time remaining: {washer.timeRemaining} min</p>
                            {Array.isArray(washer.users) && washer.users.length > 0 && (
                                <p>👤 Users: {washer.users.join(", ")}</p>
                            )}
                        </div>
                    )}
                    {washer.status === "Available" && washer.isOpen && (
                        <p className="action-text">✅ Start washing now</p>
                    )}
                    {washer.status === "Out of Order" && (
                        <p className="action-text gray">⚠️ Washer needs inspection</p>
                    )}
                    <img src={getWasherImage()} alt="Washing machine" className="washer-image" />
                </div>

                <div className="card-footer">
                    <button
                        className="start-btn"
                        disabled={washer.status !== "Available"}
                        onClick={handleStart}
                    >
                        {user ? "Reserve" : "Login to Reserve"}
                    </button>
                </div>
            </div>

            {/* Modal is outside the card so card hover doesn't affect it */}
            <ReservationModal
                isOpen={modalOpen}
                onClose={() => setModalOpen(false)}
                washer={washer}
                user={user}
            />
        </>
    );
}