import React, { useEffect, useState } from 'react'
import axios from 'axios';
import { Link, useNavigate, useParams } from 'react-router-dom';

export default function EditUser() {

    let navigate = useNavigate()

    const { id }=useParams()

    const [user, setUser] = useState({
        fullName: "",
        username: "",
        email: "",
        phoneNumber: "",
        password: "",
        citizenIdentification: "",
        role: "", 
        apartment: ""
    })

    const{ fullName, username, email, phoneNumber, password, citizenIdentification, role ,apartment} = user;

    const onInputChange = (e) => {
        setUser({...user, [e.target.name]:e.target.value})
    }

    useEffect(()=>{
        loadUser();
    },[]);

    const onSubmit =async (e) =>{
        e.preventDefault();
        await axios.put(`http://localhost:7070/user/${id}`, user);
        navigate("/userslist");
    }

    const loadUser = async () => {
        const result = await axios.get(`http://localhost:7070/user?id=${id}`);
        setUser(result.data);
    }


  return <div className="container">
    <div className="row">
        <div className="col-md-6 offset-md-3 border rounded p-4 mt-2 shadow">
            <h2 className="text-center m-4">Edit User</h2>
            <form onSubmit={(e)=>onSubmit(e)}>
            <div className="mb-3">
                <label htmlFor="Name" className="form-label">
                    Full Name
                </label>
                <input type={"text"} className="form-control" placeholder="Enter your full name" name="fullName"
                    value={fullName}
                    onChange={(e)=>onInputChange(e)}
                />
            </div>
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
                <label htmlFor="Email" className="form-label">
                    E-mail
                </label>
                <input type={"text"} className="form-control" placeholder="Enter your e-mail address" name="email"
                    value={email}
                    onChange={(e)=>onInputChange(e)}
                />
            </div>
            <div className="mb-3">
                <label htmlFor="PhoneNumber" className="form-label">
                    Phone Number
                </label>
                <input type={"text"} className="form-control" placeholder="Enter your phone number" name="phoneNumber"
                    value={phoneNumber}
                    onChange={(e)=>onInputChange(e)}
                />
            </div>
            <div className="mb-3">
                <label htmlFor="CitizenIdentification" className="form-label">
                    Citizen Identification
                </label>
                <input type={"text"} className="form-control" placeholder="Enter your citizen identification" name="citizenIdentification"
                    value={citizenIdentification}
                    onChange={(e)=>onInputChange(e)}
                />
            </div>
            <div className="mb-3">
                <label htmlFor="Password" className="form-label">
                    Password
                </label>
                <input type={"text"} className="form-control" placeholder="Enter your password" name="password"
                    value={password}
                    onChange={(e)=>onInputChange(e)}
                />
            </div>
            <div className="mb-3">
                <label htmlFor="Role" className="form-label">
                    Role
                </label>
                <input type={"text"} className="form-control" placeholder="Enter your role" name="role"
                    value={role}
                    onChange={(e)=>onInputChange(e)}
                />
            </div>
            <button type="submit" className="btn btn-outline-primary">
                Submit
            </button>
            <Link className="btn btn-outline-danger mx-2" to ="/userslist">
                Cancel
            </Link>
            </form>
        </div>
    </div>
  </div>
}
