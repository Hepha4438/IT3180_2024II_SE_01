import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

export default function SignUp() {
    let navigate = useNavigate();

    const [user, setUser] = useState({
        full_name: "",
        username: "",
        email: "",
        phone_number: "",
        password: "",
        citizen_identification: "",
        role: "",
        room: ""
    });

    const [confirmPassword, setConfirmPassword] = useState("");
    const [passwordError, setPasswordError] = useState("");
    const [passwordsMatch, setPasswordsMatch] = useState(true);

    const { full_name, username, email, phone_number, password, citizen_identification , role , room } = user;

    const onInputChange = (e) => {
        setUser({ ...user, [e.target.name]: e.target.value });
    };

    const onConfirmPasswordChange = (e) => {
        setConfirmPassword(e.target.value);
    };

    const handleRegister = async (e) => {
        e.preventDefault();

        if (password !== confirmPassword) {
            setPasswordError("Passwords do not match");
            setPasswordsMatch(false);
            return;
        } else {
            setPasswordError("");
            setPasswordsMatch(true);
        }

        try {
            const response = await fetch("http://localhost:7070/register", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    username,
                    password,
                    email,
                    role,
                    citizenIdentification: citizen_identification,
                    fullName: full_name,
                    phoneNumber: phone_number,
                    room
                }),
            });

            if (response.ok) {
                const data = await response.json(); // Lấy dữ liệu phản hồi từ server
                if (data.username) {
                    localStorage.setItem("username", data.username); // Lưu username vào Local Storage
                    alert("Đăng ký thành công!");
                    navigate("/userslist");
                } else {
                    alert("Dữ liệu trả về không hợp lệ!");
                }
            } else {
                const errorData = await response.json();
                alert(errorData.error || "Đăng ký thất bại!");
            }
        } catch (error) {
            console.error("Lỗi đăng ký:", error);
            alert("Lỗi kết nối đến server!");
        }
    };

    return (
        <div className="container">
            <div className="row">
                <div className="col-md-6 offset-md-3 border rounded p-4 mt-2 shadow">
                    <h2 className="text-center m-4">Register User</h2>

                    <form onSubmit={handleRegister}>
                        <div className="mb-3">
                            <label htmlFor="Name" className="form-label">
                                Full Name
                            </label>
                            <input type="text" className="form-control" placeholder="Enter your full name" name="full_name"
                                value={full_name}
                                onChange={onInputChange}
                            />
                        </div>
                        <div className="mb-3">
                            <label htmlFor="Username" className="form-label">
                                Username
                            </label>
                            <input type="text" className="form-control" placeholder="Enter your username" name="username"
                                value={username}
                                onChange={onInputChange}
                            />
                        </div>
                        <div className="mb-3">
                            <label htmlFor="Email" className="form-label">
                                E-mail
                            </label>
                            <input type="text" className="form-control" placeholder="Enter your e-mail address" name="email"
                                value={email}
                                onChange={onInputChange}
                            />
                        </div>
                        <div className="mb-3">
                            <label htmlFor="PhoneNumber" className="form-label">
                                Phone Number
                            </label>
                            <input type="text" className="form-control" placeholder="Enter your phone number" name="phone_number"
                                value={phone_number}
                                onChange={onInputChange}
                            />
                        </div>
                        <div className="mb-3">
                            <label htmlFor="CitizenIdentification" className="form-label">
                                Citizen Identification
                            </label>
                            <input type="text" className="form-control" placeholder="Enter your citizen identification" name="citizen_identification"
                                value={citizen_identification}
                                onChange={onInputChange}
                            />
                        </div>
                        <div className="mb-3">
                            <label htmlFor="Password" className="form-label">
                                Password
                            </label>
                            <input type="password" className="form-control" placeholder="Enter your password" name="password"
                                value={password}
                                onChange={onInputChange}
                            />
                        </div>
                        <div className="mb-3">
                            <label htmlFor="ConfirmPassword" className="form-label">
                                Re-enter Password
                            </label>
                            <input
                                type="password"
                                className={`form-control ${!passwordsMatch ? "border border-danger" : ""}`}
                                style={!passwordsMatch ? { borderColor: "red", boxShadow: "0 0 0 0.25rem rgba(220, 53, 69, 0.25)" } : {}}
                                placeholder="Re-enter your password"
                                name="confirmPassword"
                                value={confirmPassword}
                                onChange={onConfirmPasswordChange}
                            />
                            {passwordError && <div className="text-danger">{passwordError}</div>}
                        </div>
                        <div className="mb-3">
                            <label htmlFor="Role" className="form-label">
                                Role
                            </label>
                            <input type="text" className="form-control" placeholder="Enter your role" name="role"
                                value={role}
                                onChange={onInputChange}
                            />
                        </div>
                        <div className="mb-3">
                            <label htmlFor="Room" className="form-label">
                                Room
                            </label>
                            <input type="text" className="form-control" placeholder="Enter your room" name="room"
                                value={room}
                                onChange={onInputChange}
                            />
                        </div>
                        <button type="submit" className="btn btn-outline-primary">
                            Submit
                        </button>
                        <Link className="btn btn-outline-danger mx-2" to="/userslist">
                            Cancel
                        </Link>
                    </form>
                </div>
            </div>
        </div>
    );
}
