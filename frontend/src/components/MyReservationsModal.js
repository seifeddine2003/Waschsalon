import React, { useEffect, useState } from "react";
import { getMyReservations, cancelReservation } from "../api/studentApi";

export default function MyReservationsModal({ isOpen, user, onClose, onBalanceUpdate }) {
    const [reservations, setReservations] = useState([]);
    const [loading, setLoading] = useState(false);
    const [cancellingId, setCancellingId] = useState(null);
    const [error, setError] = useState("");

    useEffect(() => {
        if (!isOpen || !user) return;
        setLoading(true);
        setError("");
        getMyReservations(user.studentId)
            .then(data => setReservations(data))
            .catch(() => setError("Could not load reservations."))
            .finally(() => setLoading(false));
    }, [isOpen, user]);

    const handleCancel = (reservationId) => {
        setCancellingId(reservationId);
        setError("");
        cancelReservation(reservationId)
            .then(data => {
                setReservations(prev =>
                    prev.map(r =>
                        r.reservationId === reservationId ? { ...r, status: "cancelled" } : r
                    )
                );
                onBalanceUpdate(data.newBalance);
            })
            .catch(e => setError(e.message))
            .finally(() => setCancellingId(null));
    };

    const getDisplayStatus = (r) => {
        if (r.status === "cancelled") return "cancelled";
        const now = new Date().toTimeString().slice(0, 5);
        if (r.endTime <= now) return "finished";
        if (r.startTime <= now) return "washing";
        return "active";
    };

    if (!isOpen) return null;

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal my-reservations-modal" onClick={e => e.stopPropagation()}>
                <h2 className="my-reservations-title">My Reservations</h2>

                {error && <p className="modal-message error">{error}</p>}
                {loading && <p className="my-reservations-loading">Loading...</p>}

                {!loading && reservations.length === 0 && !error && (
                    <p className="my-reservations-empty">You have no reservations yet.</p>
                )}

                {!loading && reservations.length > 0 && (
                    <table className="reservations-table">
                        <thead>
                            <tr>
                                <th>Date</th>
                                <th>Time</th>
                                <th>Wash</th>
                                <th>Price</th>
                                <th>Status</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            {reservations.map(r => (
                                <tr key={r.reservationId}>
                                    <td>{r.date}</td>
                                    <td>{r.startTime}–{r.endTime}</td>
                                    <td>{r.washType || "—"}</td>
                                    <td>€{r.price?.toFixed(2) ?? "0.00"}</td>
                                    <td>
                                        <span className={`reservation-status ${getDisplayStatus(r)}`}>
                                            {getDisplayStatus(r)}
                                        </span>
                                    </td>
                                    <td>
                                        {r.status === "active" && r.startTime > new Date().toTimeString().slice(0, 5) && (
                                            <button
                                                className="reservation-cancel-btn"
                                                onClick={() => handleCancel(r.reservationId)}
                                                disabled={cancellingId === r.reservationId}
                                            >
                                                {cancellingId === r.reservationId ? "..." : "Cancel"}
                                            </button>
                                        )}
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                )}

                <button className="modal-close" onClick={onClose}>Close</button>
            </div>
        </div>
    );
}
