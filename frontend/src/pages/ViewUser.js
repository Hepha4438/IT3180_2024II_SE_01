import React, { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom';
import axios from 'axios';

export default function ViewUser(){

    const [user, setUser] = useState({
        fullName: "",
        username: "",
        email: "",
        phoneNumber: "",
        password: "",
        citizenIdentification: "",
        role: ""
    })

    const {id}=useParams()

    useEffect(()=>{
        loadUser();
    },[])

    const loadUser = async ()=>{
        const result = await axios.get(`http://localhost:7070/user?id=${id}`)
        setUser(result.data)
    }

    return (
    <div className="container">
        <div className="row">
            <div className="col-md-6 offset-md-3 border rounded p-4 mt-2 shadow">
                <h2 className="text-center m-4">User Details</h2>

                <div className="card">
                    <div className="card-header">
                        Details of user id : {user.id}
                        <ul className="list-group list-group-flush">
                            <li className="list-group-item">
                                <b>Full Name: </b>
                                {user.fullName}
                            </li>
                            <li className="list-group-item">
                                <b>User Name: </b>
                                {user.username}
                            </li>
                            <li className="list-group-item">
                                <b>Email: </b>
                                {user.email}
                            </li>
                            <li className="list-group-item">
                                <b>Phone Number: </b>
                                {user.phoneNumber}
                            </li>
                            <li className="list-group-item">
                                <b>Citizen Identification: </b>
                                {user.citizenIdentification}
                            </li>
                            <li className="list-group-item">
                                <b>Role: </b>
                                {user.role}
                            </li>
                        </ul>
                    </div>
                </div>
                <Link className="btn btn-primary my-2" to="/userslist">
                    Back to Home
                </Link>
            </div>
        </div>
    </div>
  )
}
