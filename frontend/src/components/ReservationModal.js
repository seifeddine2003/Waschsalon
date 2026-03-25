import React, { useEffect, useState } from "react";

const WASH_TYPES = [
    { label: "Quick Wash",  duration: 30 },
    { label: "Normal Wash", duration: 60 },
    { label: "Heavy Duty",  duration: 90 },
    { label: "Delicate",    duration: 45 },
];

function addMinutes(timeStr, mins) {
    const [h, m] = timeStr.split(":").map(Number);
    const total = h * 60 + m + mins;
    const hh = String(Math.floor(total / 60) % 24).padStart(2, "0");
    const mm = String(total % 60).padStart(2, "0");
    return `${hh}:${mm}`;
}

function getToday() {
    return new Date().toISOString().split("T")[0];
}

export default function ReservationModal({ isOpen, onClose, washer, user }) {
    const [slots, setSlots]               = useState([]);
    const [selectedSlot, setSelectedSlot] = useState(null);
    const [washType, setWashType]         = useState(null);
    const [message, setMessage]           = useState("");
    const [isError, setIsError]           = useState(false);
    const [loadingSlots, setLoadingSlots] = useState(false);

    const today = getToday();

    useEffect(() => {
        if (!isOpen || !washer?.id) return;

        setSelectedSlot(null);
        setWashType(null);
        setMessage("");
        setLoadingSlots(true);

        // Fetch available slots for this machine (already filters out past + booked)
        fetch(`http://localhost:8080/slots/available?machineId=${washer.id}`)
            .then(res => res.json())
            .then(data => {
                setSlots(Array.isArray(data) ? data : []);
                setLoadingSlots(false);
            })
            .catch(err => {
                console.error("Slot fetch error:", err);
                setSlots([]);
                setLoadingSlots(false);
            });
    }, [isOpen, washer?.id]);

    if (!isOpen) return null;

    const endTime = selectedSlot && washType
        ? addMinutes(selectedSlot.startTime, washType.duration)
        : null;

    const handleBook = () => {
        if (!selectedSlot || !washType) {
            setMessage("❌ Please select a time slot and wash type.");
            setIsError(true);
            return;
        }

        const payload = {
            studentId: user.studentId,
            machineId: washer.id,
            startTime: selectedSlot.startTime,   // ← changed from slotId
            endTime:   selectedSlot.endTime,     // ← changed from slotId
            date:      today,
            washType:  washType.label,
            washDuration: washType.duration,
        };

        fetch(`http://localhost:8080/reservations/create`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload),
        })
            .then(res => {
                if (!res.ok) return res.text().then(t => { throw new Error(t) });
                return res.json();
            })
            .then(() => {
                setMessage("✅ Reservation successful!");
                setIsError(false);
                // Remove the booked slot from the list immediately
                setSlots(prev => prev.filter(s => s.startTime !== selectedSlot.startTime));
                setSelectedSlot(null);
            })
            .catch(err => {
                setMessage("❌ " + err.message);
                setIsError(true);
            });
    };

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal" onClick={e => e.stopPropagation()}>
                <h2>Reserve {washer.name}</h2>

                <div className="today-badge">
                    📅 Today — {new Date().toLocaleDateString("en-GB", {
                    weekday: "long", day: "numeric", month: "long"
                })}
                </div>

                <label className="modal-label">
                    🕐 Pick a time slot
                    {loadingSlots && <span className="loading-hint"> — loading...</span>}
                </label>

                <div className="slot-grid">
                    {slots.length === 0 && !loadingSlots && (
                        <p style={{ color: "#6b7280", fontSize: "0.85rem" }}>
                            No available slots for today.
                        </p>
                    )}
                    {slots.map((slot, index) => {
                        const isSelected = selectedSlot?.startTime === slot.startTime;
                        return (
                            <button
                                key={index}
                                className={`slot-btn ${isSelected ? "selected" : ""}`}
                                onClick={() => setSelectedSlot(slot)}
                            >
                                {slot.startTime}
                            </button>
                        );
                    })}
                </div>

                <label className="modal-label">🧺 Wash Type</label>
                <div className="wash-type-grid">
                    {WASH_TYPES.map(type => (
                        <button
                            key={type.label}
                            className={`wash-type-btn ${washType?.label === type.label ? "selected" : ""}`}
                            onClick={() => setWashType(type)}
                        >
                            <span className="wash-type-name">{type.label}</span>
                            <span className="wash-type-duration">{type.duration} min</span>
                        </button>
                    ))}
                </div>

                {endTime && (
                    <div className="time-summary">
                        <span>⏱ {selectedSlot.startTime}</span>
                        <span className="time-arrow">→</span>
                        <span>{endTime}</span>
                        <span className="time-label">({washType.duration} min)</span>
                    </div>
                )}

                <button className="modal-btn" onClick={handleBook}>
                    Confirm Booking
                </button>

                {message && (
                    <p className={`modal-message ${isError ? "error" : "success"}`}>
                        {message}
                    </p>
                )}

                <button className="modal-close" onClick={onClose}>Cancel</button>
            </div>
        </div>
    );
}