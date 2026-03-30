import API_BASE, { HEADERS } from "../config";

export const getAll = () =>
    fetch(`${API_BASE}/washmachines/all`, { headers: HEADERS })
        .then(res => res.json());