import { apiClient } from "@/api/api";
import { AxiosProgressEvent } from "axios";
import { Button } from "./ui/button";
import { useDispatch, useSelector } from "react-redux";
import { RootState } from "@/store/store";
import { updateFiles } from "@/store/fileSlice";
export interface File {
    fileId: string;
    name: string;
    size: number;
    modified: Number;
}
export function ListButton() {
    const accessToken = useSelector((state: RootState) => state.auth.accessToken);
    const dispatch = useDispatch();

    const handleList = () => {
        apiClient
            .get("/list", {
                headers: { Authorization: "Bearer " + accessToken },
            })
            .then((response) => {
                console.log(response);
                const files = response.data.map((file: any) => {
                    return {
                        fileId: file.fileId,
                        name: file.fileName,
                        size: file.size,
                        modified: file.createdOn,
                    };
                });
                dispatch(updateFiles(files));
                apiClient
                    .get("/download", {
                        headers: { Authorization: "Bearer " + accessToken },
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
                        // setState({ downloading: false });
                    })
                    .catch((error) => {
                        // setState({ downloading: false });
                        console.warn("Errore: " + error.message);
                        return [];
                    });
            });
    };

    return <Button onClick={handleList}>List</Button>;
}
