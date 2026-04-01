import React, { useState } from "react";
import { loadBalance } from "../api/studentApi";

function BalanceModal({ isOpen, user, onClose, onBalanceUpdate }) {
    const [amount, setAmount] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    if (!isOpen) return null;

    const handleLoad = async () => {
        const euros = parseFloat(amount);
        if (isNaN(euros) || euros < 5) {
            setError("Minimum amount is €5.00");
            return;
        }
        setLoading(true);
        setError("");
        try {
            const updated = await loadBalance(user.studentId, euros);
            onBalanceUpdate(updated.balance);
            setAmount("");
            onClose();
        } catch (e) {
            setError(e.message || "Failed to load balance");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal balance-modal" onClick={e => e.stopPropagation()}>
                <div className="balance-modal-header">
                    <span className="balance-modal-icon">💳</span>
                    <h2>My Balance</h2>
                    <p className="balance-modal-amount">€{user.balance.toFixed(2)}</p>
                    <p className="balance-modal-sub">Current balance</p>
                </div>

                <div className="balance-modal-body">
                    <label className="modal-label">Top up amount</label>
                    <div className="balance-input-wrap">
                        <span className="balance-input-prefix">€</span>
                        <input
                            type="number"
                            min="5"
                            step="1"
                            placeholder="0.00"
                            value={amount}
                            onChange={e => { setAmount(e.target.value); setError(""); }}
                        />
                    </div>
                    <p className="balance-modal-hint">Minimum top-up: €5.00</p>
                    {error && <p className="error balance-modal-error">{error}</p>}

                    <div className="balance-quick-amounts">
                        {[5, 10, 20, 50].map(v => (
                            <button
                                key={v}
                                className={`quick-amount-btn ${parseFloat(amount) === v ? "selected" : ""}`}
                                onClick={() => { setAmount(String(v)); setError(""); }}
                            >
                                €{v}
                            </button>
                        ))}
                    </div>

                    <button className="modal-btn" onClick={handleLoad} disabled={loading}>
                        {loading ? "Processing..." : "Load Balance"}
                    </button>
                    <button className="modal-close" onClick={onClose}>Cancel</button>
                </div>
            </div>
        </div>
    );
}

export default BalanceModal;
