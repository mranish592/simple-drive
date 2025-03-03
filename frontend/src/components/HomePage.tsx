import { useNavigate } from "react-router-dom";
import { useEffect } from "react";
import axios from "axios";
import { UploadForm } from "./UploadForm";
import { DataTableDemo } from "./DataTableDemo";
import { useSelector, useDispatch } from "react-redux";
import { RootState } from "@/store/store";
import { refresh } from "@/store/authSlice";

export function HomePage() {
    const auth = useSelector((state: RootState) => state.auth);
    const dispatch = useDispatch();

    useEffect(() => {
        axiosClient.post("/auth/refresh").then((response) => {
            console.log(response);
            dispatch(refresh({ accessToken: response.data.accessToken }));
        });
        if (auth.isAuthenticated == false && !auth.name) {
            console.log("namesdaf", auth.isAuthenticated);
            navigate("/login");
        }
    }, [auth.isAuthenticated]);
    const axiosClient = axios.create({
        baseURL: "http://localhost:3000", // Replace with your API base URL
        withCredentials: true,
    });

    const navigate = useNavigate();
    const name = localStorage.getItem("name");
    if (name == null) {
        navigate("/login");
    }
    setInterval(() => {
        axiosClient.post("/auth/refresh").then((response) => {
            console.log(response);
            dispatch(refresh({ accessToken: response.data.accessToken }));
        });
    }, 1000 * 60 * 14);
    return (
        <div className="w-11/12">
            <UploadForm />
            <DataTableDemo />
        </div>
    );
}
