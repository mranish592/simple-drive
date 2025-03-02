import { Button } from "@/components/ui/button";
import { SignupForm } from "./components/SignupForm";
import { SignupPage } from "./components/SignupPage";
import { BrowserRouter as Router, Routes, Route, useNavigate } from "react-router-dom";
import { LoginPage } from "./components/LoginPage";
import { HomePage } from "./components/HomePage";

function App() {
    return (
        <Router>
            <div className="flex flex-col items-center justify-center min-h-svh">
                <Routes>
                    <Route path="/" element={<HomePage />} />
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/signup" element={<SignupPage />} />
                </Routes>
            </div>
        </Router>
    );
}

export default App;
