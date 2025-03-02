import { authClient } from "@/api/api";
import { Button } from "@/components/ui/button";
import { useNavigate } from "react-router-dom";

export function LogoutButton() {
    const navigate = useNavigate();
    const handleLogout = () => {
        localStorage.removeItem("accessToken");
        localStorage.removeItem("name");
        authClient.post("/auth/logout").then((response) => {
            console.log(response);
        });
        navigate("/login");
    };
    return <Button onClick={handleLogout}>Logout</Button>;
}
