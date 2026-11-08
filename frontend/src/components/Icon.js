import React from "react";

// Lucide-style stroke icons (24x24). Decorative by default (aria-hidden).
const PATHS = {
    washer: <><rect x="3" y="2" width="18" height="20" rx="3" /><circle cx="12" cy="13" r="5" /><path d="M7 6h.01M11 6h.01" /></>,
    alert: <><path d="M10.3 3.9 1.8 18a2 2 0 0 0 1.7 3h17a2 2 0 0 0 1.7-3L13.7 3.9a2 2 0 0 0-3.4 0z" /><path d="M12 9v4M12 17h.01" /></>,
    calendar: <><rect x="3" y="4" width="18" height="18" rx="2" /><path d="M16 2v4M8 2v4M3 10h18" /></>,
    clock: <><circle cx="12" cy="12" r="10" /><path d="M12 6v6l4 2" /></>,
    wallet: <><path d="M20 7H5a2 2 0 0 1 0-4h13v4z" /><path d="M3 5v14a2 2 0 0 0 2 2h15V7" /><path d="M17 14h.01" /></>,
    cart: <><circle cx="9" cy="21" r="1" /><circle cx="20" cy="21" r="1" /><path d="M1 1h4l2.7 13.4a2 2 0 0 0 2 1.6h9.7a2 2 0 0 0 2-1.6L23 6H6" /></>,
    wind: <><path d="M17.7 7.7a2.5 2.5 0 1 1 1.8 4.3H2M9.6 4.6A2 2 0 1 1 11 8H2M12.6 19.4A2 2 0 1 0 14 16H2" /></>,
    check: <><circle cx="12" cy="12" r="10" /><path d="m9 12 2 2 4-4" /></>,
    timer: <><path d="M10 2h4M12 14l3-3" /><circle cx="12" cy="14" r="8" /></>,
    monitor: <><rect x="2" y="3" width="20" height="14" rx="2" /><path d="M8 21h8M12 17v4" /></>,
};

export default function Icon({ name, size = 18, label, className, style }) {
    return (
        <svg
            width={size}
            height={size}
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
            className={className}
            style={{ verticalAlign: "-0.2em", flexShrink: 0, ...style }}
            role={label ? "img" : undefined}
            aria-label={label}
            aria-hidden={label ? undefined : true}
            focusable="false"
        >
            {PATHS[name]}
        </svg>
    );
}
