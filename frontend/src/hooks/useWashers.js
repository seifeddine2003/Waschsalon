import { useState, useEffect } from "react";
import { getAll } from "../api/washmachineApi";

export function useWashers() {
    const [washers, setWashers] = useState([]);
    useEffect(() => {
        getAll().then(setWashers).catch(console.error);
    }, []);
    return washers;
}