import React from "react";
import openWasher from "../images/washer-open.png";
import closedWasher from "../images/washer-closed.png";


export default function WasherCard({ washer }) {

    const getWasherImage = () => {
        switch (washer.status) {
            case "Available":
                return openWasher;
            case "In Use":
                return closedWasher;
            default:
                return closedWasher;
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
                    <p className="action-text gray">
                        ⚠️ Washer needs inspection
                    </p>
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
                    disabled={washer.status !== "Available"}
                >
                    Start
                </button>
            </div>
        </div>
    );
}
