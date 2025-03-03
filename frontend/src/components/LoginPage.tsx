import { LoginForm } from "./LoginForm";
import { useNavigate } from "react-router-dom";

export function LoginPage() {
    const navigate = useNavigate();

    const handleSignup = () => {
        navigate("/signup");
    };

    return (
        <>
            <div className="flex flex-col items-center justify-center border-2 border-gray-200 p-8 rounded-lg mt-12">
                <h1 className="text-3xl font-bold mb-12">Login</h1>
                <LoginForm />
            </div>
            <p>
                Already registerd?{" "}
                <button onClick={handleSignup} className="text-blue-600 hover:text-blue-800 transition-colors duration-200 underline">
                    <a>Sign up here</a>
                </button>
            </p>
        </>
    );
}
