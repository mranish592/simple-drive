import { LoginForm } from "./LoginForm";
import { useNavigate } from "react-router-dom";

export function LoginPage() {
    const navigate = useNavigate();

    const handleSignup = () => {
        navigate("/signup");
    };

    return (
        <>
            <div className="flex flex-col items-center justify-center border-2 border-gray-200 p-8 rounded-lg">
                <h1 className="text-3xl font-bold mb-12">Login</h1>
                <LoginForm />
            </div>
            <p>
                Already registerd?{" "}
                <button onClick={handleSignup}>
                    <a>Sign up here</a>
                </button>
            </p>
        </>
    );
}
