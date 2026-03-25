import React from "react";
import openWasher from "../images/washer-open.png";
import closedWasher from "../images/washer-closed.png";
import defectWasher from "../images/washer-defect.png";

export default function WasherCard({ washer, user, onReserve }) {

    const getWasherImage = () => {
        switch (washer.status) {
            case "Available":   return openWasher;
            case "Out of Order": return defectWasher;
            default:            return closedWasher;
        }
    };

    return (
        <div className="card">
            <div className="card-header">
                <span className="washer-name">{washer.name}</span>
                <span className={`status-pill ${washer.status.replace(" ", "-")}`}>
                    {washer.status}
                </span>
            </div>

            <div className="card-body">
                {washer.status === "Out of Order" && (
                    <p className="action-text gray">⚠️ Washer needs inspection</p>
                )}

                {washer.status === "Available" && (
                    <p className="action-text">✅ Select a time slot to reserve</p>
                )}

                <img
                    src={getWasherImage()}
                    alt="Washing machine"
                    className="washer-image"
                />
            </div>

            <div className="card-footer">
                <button
                    className="start-btn"
                    disabled={washer.status !== "Available" || !user}
                    onClick={() => onReserve(washer)}
                >
                    {!user ? "Login to Reserve" : "Reserve"}
                </button>
            </div>
        </div>
    );
}