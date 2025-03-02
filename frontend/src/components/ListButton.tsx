import { apiClient } from "@/api/api";
import { AxiosProgressEvent } from "axios";
import { Button } from "./ui/button";
import { useState } from "react";

export function ListButton() {
    const [state, setState] = useState({ downloading: false });
    const handleList = () => {
        apiClient.get("/list",{
            headers: { Authorization: "Bearer " + localStorage.getItem("accessToken") },
        }).then((response) => {
            console.log(response);
            apiClient
                .get("/download", {
                    headers: { Authorization: "Bearer " + localStorage.getItem("accessToken") },
                    params: {
                        fileId: response.data[0].fileId,
                    },
                    responseType: "blob", // important
                    onDownloadProgress: (progressEvent: AxiosProgressEvent) => {
                        if (progressEvent.total != null) {
                            let percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
                            console.log(percentCompleted);
                        }
                    },
                })
                .then((response) => {
                    const url = window.URL.createObjectURL(new Blob([response.data]));
                    const link = document.createElement("a");
                    link.href = url;
                    link.setAttribute("download", "abc"); //or any other extension
                    document.body.appendChild(link);
                    link.click();
                    setState({ downloading: false });
                })
                .catch((error) => {
                    setState({ downloading: false });
                    console.warn("Errore: " + error.message);
                    return [];
                });
        });
    };

    return <Button onClick={handleList}>List</Button>;
}
