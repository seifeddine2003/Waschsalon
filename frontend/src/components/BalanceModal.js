import React, { useState } from "react";
import { loadStripe } from "@stripe/stripe-js";
import { Elements, CardElement, useStripe, useElements } from "@stripe/react-stripe-js";
import { STRIPE_PUBLIC_KEY } from "../config";
import { loadBalance } from "../api/studentApi";
import API_BASE, { HEADERS } from "../config";

const stripePromise = loadStripe(STRIPE_PUBLIC_KEY);

const QUICK_AMOUNTS = [5, 10, 20, 50];

function BalanceForm({ user, onClose, onBalanceUpdate }) {
    const stripe = useStripe();
    const elements = useElements();
    const [amount, setAmount] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const handleLoad = async () => {
        const euros = parseFloat(amount);
        if (isNaN(euros) || euros < 5) {
            setError("Minimum amount is €5.00");
            return;
        }
        if (!stripe || !elements) return;

        setLoading(true);
        setError("");

        try {
            // 1. Create PaymentIntent on backend
            const res = await fetch(`${API_BASE}/payment/create-intent`, {
                method: "POST",
                headers: HEADERS,
                body: JSON.stringify({ amount: euros }),
            });
            const { clientSecret, error: serverError } = await res.json();
            if (serverError) throw new Error(serverError);

            // 2. Confirm card payment with Stripe
            const { error: stripeError, paymentIntent } = await stripe.confirmCardPayment(clientSecret, {
                payment_method: { card: elements.getElement(CardElement) },
            });
            if (stripeError) throw new Error(stripeError.message);

            if (paymentIntent.status === "succeeded") {
                // 3. Credit the balance in our backend
                const updated = await loadBalance(user.studentId, euros);
                onBalanceUpdate(updated.balance);
                onClose();
            }
        } catch (e) {
            setError(e.message || "Payment failed");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="modal balance-modal" onClick={e => e.stopPropagation()}>
            <div className="balance-modal-header">
                <span className="balance-modal-icon">💳</span>
                <h2>My Balance</h2>
                <p className="balance-modal-amount">€{user.balance.toFixed(2)}</p>
                <p className="balance-modal-sub">Current balance</p>
            </div>

            <div className="balance-modal-body" autoComplete="off">
                <label className="modal-label">Top up amount</label>
                <div className="balance-input-wrap">
                    <span className="balance-input-prefix">€</span>
                    <input
                        type="number"
                        min="5"
                        step="1"
                        placeholder="0.00"
                        autoComplete="off"
                        value={amount}
                        onChange={e => { setAmount(e.target.value); setError(""); }}
                    />
                </div>
                <p className="balance-modal-hint">Minimum top-up: €5.00</p>

                <div className="balance-quick-amounts">
                    {QUICK_AMOUNTS.map(v => (
                        <button
                            key={v}
                            className={`quick-amount-btn ${parseFloat(amount) === v ? "selected" : ""}`}
                            onClick={() => { setAmount(String(v)); setError(""); }}
                        >
                            €{v}
                        </button>
                    ))}
                </div>

                <label className="modal-label">Card details</label>
                <div className="card-element-wrap">
                    <CardElement options={{
                        disableLink: true,
                        style: {
                            base: { fontSize: "15px", color: "#2d3436", "::placeholder": { color: "#9ca3af" } },
                            invalid: { color: "#ef4444" },
                        }
                    }} />
                </div>

                {error && <p className="error balance-modal-error">{error}</p>}

                <button className="modal-btn" onClick={handleLoad} disabled={loading || !stripe}>
                    {loading ? "Processing..." : `Pay €${parseFloat(amount) >= 5 ? parseFloat(amount).toFixed(2) : "0.00"}`}
                </button>
                <button className="modal-close" onClick={onClose}>Cancel</button>
            </div>
        </div>
    );
}

function BalanceModal({ isOpen, user, onClose, onBalanceUpdate }) {
    if (!isOpen) return null;

    return (
        <div className="modal-overlay" onClick={onClose}>
            <Elements stripe={stripePromise} options={{ loader: "never" }}>
                <BalanceForm user={user} onClose={onClose} onBalanceUpdate={onBalanceUpdate} />
            </Elements>
        </div>
    );
}

export default BalanceModal;
