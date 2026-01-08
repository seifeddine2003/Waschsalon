import React from "react";

export default function WasherCard({ washer }) {
    return (
        <div className="card">
            <div className="card-header">
                <span className="washer-name">{washer.name}</span>
                <span className={`status-pill ${washer.type}`}>
          {washer.status}
        </span>
            </div>

            <div className="card-body">
                {washer.type === "busy" && (
                    <div>
                        <p>⏳ Time remaining: {washer.timeRemaining} min</p>
                    </div>
                )}

                {washer.type === "available" && (
                    <p className="action-text">Start washing now</p>
                )}

                {washer.type === "broken" && (
                    <p className="action-text gray">Washer needs inspection</p>
                )}

                {washer.type === "setting-up" && (
                    <p className="action-text blue">Program selection in progress</p>
                )}

                <div className="washer-circle">
                    <div className="inner-drum"></div>
                </div>
            </div>

            <div className="card-footer">
                <button
                    className="start-btn"
                    disabled={washer.type === "broken"}
                >
                    Start
                </button>
            </div>
        </div>
    );
}
