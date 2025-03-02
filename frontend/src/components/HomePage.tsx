import { useNavigate } from "react-router-dom";
import { LogoutButton } from "./LogoutButton";
import { use, useEffect } from "react";
import axios from "axios";
import { UploadForm } from "./UploadForm";
import { ListButton } from "./ListButton";
import { DataTableDemo } from "./DataTableDemo";

export function HomePage() {
    const axiosClient = axios.create({
        baseURL: "http://localhost:3000", // Replace with your API base URL
        withCredentials: true,
    });

    const navigate = useNavigate();
    const name = localStorage.getItem("name");
    if (name == null) {
        navigate("/login");
    }
    const firstName = name?.split(" ")[0];
    useEffect(() => {
        axiosClient.post("/auth/refresh").then((response) => {
            console.log(response);
            localStorage.setItem("accessToken", response.data.accessToken);
        });
    }, []);
    return (
        <div className="w-11/12">
            <h1>Hi {firstName}</h1>
            <UploadForm />
            <ListButton />
            <LogoutButton />
            <DataTableDemo />
        </div>
    );
}
