import React, { useEffect, useState } from "react";
import API_BASE, { HEADERS } from "../config";

const WASH_TYPES = [
    { label: "Quick Wash",  duration: 30, price: 2.50 },
    { label: "Normal Wash", duration: 60, price: 4.00 },
    { label: "Heavy Duty",  duration: 90, price: 6.00 },
    { label: "Delicate",    duration: 45, price: 3.50 },
];

const DRYER_TYPES = [
    { label: "Quick Dry",  duration: 30, price: 2.00 },
    { label: "Normal Dry", duration: 60, price: 3.50 },
    { label: "Heavy Dry",  duration: 90, price: 5.00 },
];

function addMinutes(timeStr, mins) {
    const [h, m] = timeStr.split(":").map(Number);
    const total = h * 60 + m + mins;
    const hh = String(Math.floor(total / 60) % 24).padStart(2, "0");
    const mm = String(total % 60).padStart(2, "0");
    return `${hh}:${mm}`;
}

export default function ReservationModal({ isOpen, onClose, washer, user, onBalanceUpdate }) {
    const isDryer = washer?.type === "dryer";
    const machineTypes = isDryer ? DRYER_TYPES : WASH_TYPES;
    const [slots, setSlots] = useState([]);
    const [selectedSlot, setSelectedSlot] = useState(null);
    const [washType, setWashType] = useState(null);
    const [message, setMessage] = useState("");
    const [isError, setIsError] = useState(false);
    const [loadingSlots, setLoadingSlots] = useState(false);
    const [booking, setBooking] = useState(false);

    const today = new Date().toISOString().split("T")[0];
    const showCart = selectedSlot && washType;

    useEffect(() => {
        if (!isOpen || !washer?.id) return;
        setSelectedSlot(null);
        setWashType(null);
        setMessage("");
        setLoadingSlots(true);

        fetch(`${API_BASE}/slots/available?machineId=${washer.id}`, { headers: HEADERS })
            .then(res => res.json())
            .then(data => setSlots(Array.isArray(data) ? data : []))
            .catch(() => setSlots([]))
            .finally(() => setLoadingSlots(false));
    }, [isOpen, washer?.id]);

    if (!isOpen) return null;

    const endTime = selectedSlot && washType
        ? addMinutes(selectedSlot.startTime, washType.duration)
        : null;

    const formattedDate = new Date().toLocaleDateString("en-GB", {
        weekday: "long", day: "numeric", month: "long",
    });

    const handleBook = () => {
        if (!selectedSlot || !washType) {
            setMessage("Please select a time slot and wash type.");
            setIsError(true);
            return;
        }

        if (user.balance < washType.price) {
            setMessage(`Insufficient balance. You need €${washType.price.toFixed(2)} but have €${user.balance.toFixed(2)}.`);
            setIsError(true);
            return;
        }

        setBooking(true);
        const payload = {
            studentId:    user.studentId,
            machineId:    washer.id,
            startTime:    selectedSlot.startTime,
            endTime:      addMinutes(selectedSlot.startTime, washType.duration),
            date:         today,
            washType:     washType.label,
            washDuration: washType.duration,
            price:        washType.price,
        };

        fetch(`${API_BASE}/reservations/create`, {
            method: "POST",
            headers: HEADERS,
            body: JSON.stringify(payload),
        })
            .then(res => {
                if (!res.ok) return res.text().then(t => { throw new Error(t); });
                return res.json();
            })
            .then((data) => {
                setMessage("✅ Reservation confirmed!");
                setIsError(false);
                setSlots(prev => prev.filter(s => s.startTime !== selectedSlot.startTime));
                setSelectedSlot(null);
                setWashType(null);
                if (onBalanceUpdate) onBalanceUpdate(data.newBalance);
            })
            .catch(err => {
                setMessage(err.message || "Something went wrong.");
                setIsError(true);
            })
            .finally(() => setBooking(false));
    };

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="reservation-wrapper" onClick={e => e.stopPropagation()}>

                {/* LEFT: Reservation Modal */}
                <div className="modal reservation-modal">
                    <h2>Reserve {washer.name}</h2>
                    <div className="today-badge">📅 Today — {formattedDate}</div>

                    <label className="modal-label">
                        🕐 Pick a time slot
                        {loadingSlots && <span className="loading-hint"> — loading...</span>}
                    </label>

                    <div className="slot-grid">
                        {!loadingSlots && slots.length === 0 && (
                            <p className="no-slots">No available slots for today.</p>
                        )}
                        {slots.map((slot, i) => (
                            <button
                                key={i}
                                className={`slot-btn ${selectedSlot?.startTime === slot.startTime ? "selected" : ""}`}
                                onClick={() => setSelectedSlot(slot)}
                            >
                                {slot.startTime}
                            </button>
                        ))}
                    </div>

                    <label className="modal-label">{isDryer ? "💨 Dry type" : "🧺 Wash type"}</label>
                    <div className="wash-type-grid">
                        {machineTypes.map(type => (
                            <button
                                key={type.label}
                                className={`wash-type-btn ${washType?.label === type.label ? "selected" : ""}`}
                                onClick={() => setWashType(type)}
                            >
                                <span className="wash-type-name">{type.label}</span>
                                <span className="wash-type-duration">{type.duration} min</span>
                                <span className="wash-type-price">€{type.price.toFixed(2)}</span>
                            </button>
                        ))}
                    </div>

                    {message && (
                        <p className={`modal-message ${isError ? "error" : "success"}`}>
                            {message}
                        </p>
                    )}

                    <button className="modal-close" onClick={onClose}>Cancel</button>
                </div>

                {/* RIGHT: Cart Panel */}
                <div className={`cart-panel ${showCart ? "visible" : ""}`}>
                    <h3 className="cart-title">🛒 Your Order</h3>

                    <div className="cart-items">
                        <div className="cart-row">
                            <span className="cart-label">🖥️ Machine</span>
                            <span className="cart-value">{washer.name}</span>
                        </div>
                        <div className="cart-row">
                            <span className="cart-label">📅 Date</span>
                            <span className="cart-value">{formattedDate}</span>
                        </div>
                        {selectedSlot && (
                            <div className="cart-row">
                                <span className="cart-label">🕐 Time</span>
                                <span className="cart-value">
                                    {selectedSlot.startTime} → {endTime}
                                </span>
                            </div>
                        )}
                        {washType && (
                            <>
                                <div className="cart-row">
                                    <span className="cart-label">🧺 Type</span>
                                    <span className="cart-value">{washType.label}</span>
                                </div>
                                <div className="cart-row">
                                    <span className="cart-label">⏱️ Duration</span>
                                    <span className="cart-value">{washType.duration} min</span>
                                </div>
                            </>
                        )}
                    </div>

                    <div className="cart-divider" />

                    <div className="cart-total-row">
                        <span className="cart-total-label">Total</span>
                        <span className="cart-total-price">
                            €{washType ? washType.price.toFixed(2) : "0.00"}
                        </span>
                    </div>

                    <div className="cart-balance">
                        💳 Balance: €{user ? user.balance.toFixed(2) : "0.00"}
                    </div>

                    <button
                        className="modal-btn cart-confirm-btn"
                        onClick={handleBook}
                        disabled={booking}
                    >
                        {booking ? "Processing..." : "✅ Confirm & Pay"}
                    </button>
                </div>

            </div>
        </div>
    );
}