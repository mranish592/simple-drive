import { SignupPage } from "./components/SignupPage";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { LoginPage } from "./components/LoginPage";
import { HomePage } from "./components/HomePage";
import { Navbar } from "./components/Navbar";

function App() {
    return (
        <Router>
            <Navbar></Navbar>
            <div className="flex flex-col items-center justify-center">
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
