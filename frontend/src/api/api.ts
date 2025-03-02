import axios from "axios";

const authClient = axios.create({
    baseURL: "http://localhost:3000/auth", // Replace with your API base URL
    withCredentials: true,
});

const apiClient = axios.create({
    baseURL: "http://localhost:3000/api", // Replace with your API base URL
    withCredentials: true,
});

export { authClient, apiClient };
