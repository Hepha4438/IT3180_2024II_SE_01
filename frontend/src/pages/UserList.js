import React, { useEffect, useState } from "react";
import axios from "axios";
import { Link } from "react-router-dom";

export default function UserList() {
    const [users, setUsers] = useState([]);
    const [searchUsername, setSearchUsername] = useState("");
    const [errorMessage, setErrorMessage] = useState("");

    useEffect(() => {
        loadUsers();
    }, []);

    const loadUsers = async (username = "") => {
        try {
            if (username) {
                const response = await axios.get(`http://localhost:7070/user?username=${username}`);
                setUsers([response.data]); // Convert single object to array for table display
                setErrorMessage("");
            } else {
                const response = await axios.get("http://localhost:7070/users");
                setUsers(response.data);
                setErrorMessage("");
            }
        } catch (error) {
            setUsers([]);
            setErrorMessage("User not found.");
        }
    };

    const deleteUser = async (id) => {
        await axios.delete(`http://localhost:7070/deleteuser?id=${id}`);
        loadUsers();
    };

    const handleSearch = (e) => {
        e.preventDefault();
        loadUsers(searchUsername);
    };

    return (
        <div className="container">
            <h2 className="text-center my-4">User Management</h2>

            {/* Search Bar */}
            <form onSubmit={handleSearch} className="mb-3">
                <div className="input-group">
                    <input
                        type="text"
                        className="form-control"
                        placeholder="Enter username to search"
                        value={searchUsername}
                        onChange={(e) => setSearchUsername(e.target.value)}
                    />
                    <button type="submit" className="btn btn-outline-primary">Search</button>
                    <button type="button" className="btn btn-outline-secondary" onClick={() => loadUsers()}>Reset</button>
                </div>
            </form>

            {/* Error Message */}
            {errorMessage && <p className="text-danger">{errorMessage}</p>}

            {/* Scrollable Table */}
            <div className="py-4" style={{ maxHeight: "900px", overflowY: "auto", border: "1px solid #ddd", borderRadius: "5px" }}>
                <table className="table border shadow">
                    <thead className="table-dark">
                        <tr>
                            <th scope="col">Index</th>
                            <th scope="col">Full Name</th>
                            <th scope="col">Username</th>
                            <th scope="col">Email</th>
                            <th scope="col">Phone Number</th>
                            <th scope="col">Citizen ID</th>
                            <th scope="col">Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        {users.length > 0 ? (
                            users.map((user, index) => (
                                <tr key={index}>
                                    <td>{index + 1}</td>
                                    <td>{user.fullName}</td>
                                    <td>{user.username}</td>
                                    <td>{user.email}</td>
                                    <td>{user.phoneNumber}</td>
                                    <td>{user.citizenIdentification}</td>
                                    <td>
                                        <Link className="btn btn-primary mx-1" to={`/viewuser/${user.id}`}>View</Link>
                                        <Link className="btn btn-outline-primary mx-1" to={`/edituser/${user.id}`}>Edit</Link>
                                        <button className="btn btn-danger mx-1" onClick={() => deleteUser(user.id)}>Delete</button>
                                    </td>
                                </tr>
                            ))
                        ) : (
                            <tr>
                                <td colSpan="7" className="text-center">No users found</td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>

            {/* Add User Button at the Bottom */}
            <div className="text-center mt-3">
                <Link className="btn btn-success" to="/register">Add User</Link>
            </div>
        </div>
    );
}
