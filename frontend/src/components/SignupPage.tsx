import { SignupForm } from "./SignupForm";
import { useNavigate } from "react-router-dom";

export function SignupPage() {
    const navigate = useNavigate();
    const handleLoginRedirect = () => {
        navigate("/login");
    };
    return (
        <>
            <div className="flex flex-col items-center justify-center border-2 border-gray-200 p-8 rounded-lg">
                <h1 className="text-3xl font-bold mb-12">Sign up</h1>
                <SignupForm />
            </div>
            <p>
                Already registerd?{" "}
                <button onClick={handleLoginRedirect}>
                    <a>Login here</a>
                </button>
            </p>
        </>
    );
}
