import React, { useState, useEffect } from "react";
import axios from "axios";
import { Link } from "react-router-dom";

export default function Apartment() {
    const [apartments, setApartments] = useState([]);
    const [searchId, setSearchId] = useState("");
    const [errorMessage, setErrorMessage] = useState("");

    useEffect(() => {
        loadApartments();
    }, []);

    const loadApartments = async (apartmentId = "") => {
        try {
            if (apartmentId) {
                const response = await axios.get(`http://localhost:7070/apartment?apartmentId=${apartmentId}`);
                setApartments([response.data]);  // Convert single object to array for table
                setErrorMessage("");
            } else {
                const response = await axios.get("http://localhost:7070/apartments");
                setApartments(response.data);
                setErrorMessage("");
            }
        } catch (error) {
            setApartments([]);
            setErrorMessage("Apartment not found.");
        }
    };

    // Function to delete an apartment
    const deleteApartment = async (apartmentId) => {
        await axios.delete(`http://localhost:7070/deleteapartment?apartmentId=${apartmentId}`);
        loadApartments();  // Refresh the list
    };

    // Handle form submission for search
    const handleSearch = (e) => {
        e.preventDefault();
        loadApartments(searchId);
    };

    return (
        <div className="container">
            <h2 className="text-center my-4">Quản lý căn hộ</h2>

            <form onSubmit={handleSearch} className="mb-3">
                <div className="input-group">
                    <input 
                        type="text" 
                        className="form-control" 
                        placeholder="Nhập số căn hộ cần tìm" 
                        value={searchId}
                        onChange={(e) => setSearchId(e.target.value)}
                    />
                    <button type="submit" className="btn btn-outline-primary">Search</button>
                    <button type="button" className="btn btn-outline-secondary" onClick={() => loadApartments()}>Reset</button>
                </div>
            </form>

            {/* Error Message */}
            {errorMessage && <p className="text-danger">{errorMessage}</p>}

            {/* Apartment Table */}
            <div className="py-4">
                <table className="table border shadow">
                    <thead>
                        <tr>
                            <th scope="col">Số căn hộ</th>
                            <th scope="col">Thành viên</th>
                            <th scope="col">Tầng</th>
                            <th scope="col">Trạng thái</th>
                            <th scope="col">Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        {apartments.length > 0 ? (
                            apartments.map((apartment, index) => (
                                <tr key={index}>
                                    <td>{apartment.apartmentId}</td>
                                    <td>{apartment.occupants}</td>
                                    <td>{apartment.floor}</td>
                                    <td>{apartment.isOccupied ? "Đang ở" : "Trống"}</td>
                                    <td>
                                        <Link className="btn btn-primary mx-2" to={`/viewapartment/${apartment.apartmentId}`}>View</Link>
                                        <button className="btn btn-danger mx-2" onClick={() => deleteApartment(apartment.apartmentId)}>Delete</button>
                                    </td>
                                </tr>
                            ))
                        ) : (
                            <tr>
                                <td colSpan="5" className="text-center">No apartments found</td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
}
