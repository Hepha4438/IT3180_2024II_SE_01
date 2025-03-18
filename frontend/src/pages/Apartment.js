import React, { useState } from 'react'
import axios from 'axios';
import { Link, useNavigate } from 'react-router-dom';

export default function LoginUser() {
    let navigate = useNavigate()

    const [user, setUser] = useState({
        username: "",
        password: ""
    })
    
    const{ username, password} = user;

    const onInputChange = (e) => {
        setUser({...user, [e.target.name]:e.target.value})
    }

    const handleLogin = async (e) => {
        e.preventDefault();
    
        try {
            const response = await fetch("http://localhost:7070/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ username, password }),
            });
    
            let data;
            try {
                data = await response.json();
            } catch (jsonError) {
                console.error("Lỗi đọc JSON từ phản hồi:", jsonError);
                alert("Sai tên đăng nhập hoặc mật khẩu!");
                return;
            }
    
            if (response.ok) {
                if (data.username) {
                    localStorage.setItem("username", data.username);
                    alert("Đăng nhập thành công!");
                    navigate("/home");
                } else {
                    alert("Dữ liệu trả về không hợp lệ!");
                }
            } else {
                alert(data?.error || "Sai tên đăng nhập hoặc mật khẩu!");
            }
        } catch (error) {
            console.error("Lỗi đăng nhập:", error);
            alert("Sai tên đăng nhập hoặc mật khẩu!");
        }
    };
    
    

    return (<div className="container">
        <div className="row">
            <div className="col-md-6 offset-md-3 border rounded p-4 mt-2 shadow">
                <h2 className="text-center m-4">Login User</h2>

                <form onSubmit={handleLogin}>
                <div className="mb-3">
                    <label htmlFor="Username" className="form-label">
                        Username
                    </label>
                    <input type={"text"} className="form-control" placeholder="Enter your username" name="username"
                        value={username}
                        onChange={(e)=>onInputChange(e)}
                    />
                </div>
                <div className="mb-3">
                    <label htmlFor="Password" className="form-label">
                        Password
                    </label>
                    <input type={"password"} className="form-control" placeholder="Enter your password" name="password"
                        value={password}
                        onChange={(e)=>onInputChange(e)}
                    />
                </div>
                <button type="submit" className="btn btn-outline-primary">
                    Submit
                </button>
                <Link className="btn btn-outline-danger mx-2" to ="/userlist">
                    Cancel
                </Link>
                </form>
            </div>
        </div>
    </div>)
}