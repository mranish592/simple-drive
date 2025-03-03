import { authClient } from "@/api/api";
import { Button } from "@/components/ui/button";
import { logout } from "@/store/authSlice";
import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";

export function LogoutButton() {
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const handleLogout = () => {
        localStorage.removeItem("accessToken");
        localStorage.removeItem("name");
        authClient.post("/logout").then((response) => {
            console.log(response);
            dispatch(logout());
            navigate("/login");
        });
    };
    return (
        <Button onClick={handleLogout} variant="outline">
            Logout
        </Button>
    );
}
