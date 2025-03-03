import { RootState } from "@/store/store";
import { LogOut } from "lucide-react";
import { useSelector } from "react-redux";
import { LogoutButton } from "./LogoutButton";

export function Navbar() {
    const name = useSelector((state: RootState) => state.auth.name);
    const initials = name
        ? name
              .split(" ")
              .map((word) => word[0])
              .join("")
              .toUpperCase()
        : "";

    return (
        <nav className="flex items-center justify-between p-4 border-b">
            <div className="text-xl font-semibold">Simple Drive</div>
            <div className="flex items-center space-x-4">
                {initials && <div className="w-8 h-8 rounded-full bg-gray-300 flex items-center justify-center font-semibold">{initials}</div>}
                <button className="flex items-center space-x-2">
                    <LogOut className="w-5 h-5" />
                    <span>
                        <LogoutButton></LogoutButton>
                    </span>
                </button>
            </div>
        </nav>
    );
}
