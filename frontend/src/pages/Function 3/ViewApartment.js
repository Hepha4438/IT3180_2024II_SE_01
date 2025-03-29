import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import axios from 'axios';

export default function ViewApartment() {
    const [apartment, setApartment] = useState({
        apartmentId: "",
        floor: null,
        isOccupied: false,
        occupants: null,
        owner: "",
        area: null,
        apartmentType: "",
        residents: []
    });

    const { apartmentId } = useParams();

    useEffect(() => {
        loadApartment();
    }, [apartmentId]);

    // Load apartment details including residents
    const loadApartment = async () => {
        try {
            const result = await axios.get(`http://localhost:7070/apartment?apartmentId=${apartmentId}`);
            setApartment(result.data);
        } catch (error) {
            console.error("Error loading apartment:", error);
        }
    };

    // Remove a resident from the apartment
    const deleteUser = async (id) => {
        try {
            await axios.put(`http://localhost:7070/apartment/remove-resident/${apartmentId}?userId=${id}`);
            loadApartment();
        } catch (error) {
            console.error("Error removing resident:", error);
        }
    };

    return (
        <div className="container">
            <div className="row">
                <div className="col-md-6 offset-md-3 border rounded p-4 mt-2 shadow">
                    <h2 className="text-center m-4">Apartment Details</h2>
                    <div className="card">
                        <div className="card-header">
                            Details of apartment: {apartment.apartmentId}
                            <ul className="list-group list-group-flush">
                                <li className="list-group-item"><b>Apartment Number:</b> {apartment.apartmentId}</li>
                                <li className="list-group-item"><b>Floor:</b> {apartment.floor}</li>
                                <li className="list-group-item"><b>Number of Residents:</b> {apartment.occupants}</li>
                                <li className="list-group-item"><b>Owner:</b> {apartment.owner}</li>
                                <li className="list-group-item"><b>Area:</b> {apartment.area}</li>
                                <li className="list-group-item"><b>Type:</b> {apartment.apartmentType}</li>
                                <li className="list-group-item"><b>Status:</b> {apartment.isOccupied ? "Occupied" : "Vacant"}</li>
                            </ul>
                        </div>
                    </div>
                    <Link className="btn btn-primary my-2" to={`/editapartment/${apartmentId}`}>Edit</Link>
                </div>
            </div>

            {/* Residents Table */}
            <div className="py-4">
                <h3>Residents</h3>
                <table className="table border shadow">
                    <thead>
                        <tr>
                            <th scope="col">STT</th>
                            <th scope="col">Họ và tên</th>
                            <th scope="col">Tên đăng nhập</th>
                            <th scope="col">Email</th>
                            <th scope="col">SĐT</th>
                            <th scope="col">Số CCCD</th>
                            <th scope="col">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {apartment.residents.length > 0 ? (
                            apartment.residents.map((user, index) => (
                                <tr key={user.id}>
                                    <th scope="row">{index + 1}</th>
                                    <td>{user.fullName}</td>
                                    <td>{user.username}</td>
                                    <td>{user.email}</td>
                                    <td>{user.phoneNumber}</td>
                                    <td>{user.citizenIdentification}</td>
                                    <td>
                                        <Link className="btn btn-primary mx-2" to={`/viewuser/${user.id}`}>View</Link>
                                        <Link className="btn btn-outline-primary mx-2" to={`/edituser/${user.id}`}>Edit</Link>
                                        <button className="btn btn-danger mx-2" onClick={() => deleteUser(user.id)}>Delete</button>
                                    </td>
                                </tr>
                            ))
                        ) : (
                            <tr>
                                <td colSpan="7" className="text-center">No residents found</td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>        
        </div>
    );
}
