import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Link, useParams } from 'react-router-dom';

export default function UserList() {

    const [users, setUsers] = useState([]);

    const {id}=useParams()

    useEffect(() => {
        loadUsers();
    }, []);

    const loadUsers = async () => {
        const result = await axios.get("http://localhost:7070/users");
        setUsers(result.data);
    }

    const deleteUser = async (id) =>{
        await axios.delete(`http://localhost:7070/user/${id}`)
        loadUsers()
    }

    return (
        <div className='container'>
            <div className='py-4'>
                <table className="table border shadow">
                    <thead>
                        <tr>
                            <th scope="col">Index</th>
                            <th scope="col">Full Name</th>
                            <th scope="col">UserName</th>
                            <th scope="col">Email</th>
                            <th scope="col">Phone Number</th>
                            <th scope="col">Citizen Identification</th>
                            <th scope="col">Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        {
                            users.map((user, index) => (
                                <tr>
                                    <th scope="row" key={index}>{index+1}</th>
                                    <td>{user.fullName}</td>
                                    <td>{user.username}</td>
                                    <td>{user.email}</td>
                                    <td>{user.phoneNumber}</td>
                                    <td>{user.citizenIdentification}</td>
                                    <td>
                                        <Link className='btn btn-primary mx-2'
                                            to={`/viewuser/${user.id}`}
                                        >View</Link>
                                        <Link className='btn btn-outline-primary mx-2'
                                            to={`/edituser/${user.id}`}
                                        >Edit</Link>
                                        <button className='btn btn-danger mx-2'
                                            onClick={()=>deleteUser(user.id)}
                                        >Delete</button>
                                    </td>
                                </tr>
                            ))
                        }
                    </tbody>
                </table>
            </div>
        </div>
    )
}
