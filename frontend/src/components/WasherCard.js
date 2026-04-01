import React from "react";
import openWasher from "../images/washer-open.png";
import closedWasher from "../images/washer-closed.png";
import defectWasher from "../images/washer-defect.png";

export default function WasherCard({ washer, user, onReserve }) {
    const isDryer = washer.type === "dryer";

    const getImage = () => {
        if (washer.status === "Available") return openWasher;
        if (washer.status === "Out of Order") return defectWasher;
        return closedWasher;
    };

    const isAvailable = washer.status === "Available";

    const cardClass = washer.status === "Available"
        ? "card card--available"
        : washer.status === "Out of Order"
        ? "card card--out-of-order"
        : "card";

    return (
        <div className={cardClass}>
            <div className="card-header">
                <span className="washer-name">{washer.name}</span>
                <span className={`status-pill ${(washer.status || "").replaceAll(" ", "-")}`}>
                    {washer.status || "Unknown"}
                </span>
            </div>

            <div className="card-body">
                {washer.status === "Out of Order" && (
                    <p className="action-text">⚠️ Washer needs inspection</p>
                )}
                {isAvailable && (
                    <p className="action-text"> Select a time slot to reserve</p>
                )}

                <img src={getImage()} alt="Washing machine" className="washer-image" />
            </div>

            <div className="card-footer">
                <button
                    className="start-btn"
                    disabled={!isAvailable || !user}
                    onClick={() => onReserve(washer)}
                >
                    {washer.status === "Out of Order"
                        ? "Under Maintenance"
                        : user
                        ? isDryer ? "Start Drying" : "Reserve"
                        : `Login to ${isDryer ? "Dry" : "Reserve"}`}
                </button>
            </div>
        </div>
    );
}