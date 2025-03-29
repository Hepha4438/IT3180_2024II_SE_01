import React, { useEffect, useState } from 'react'
import axios from 'axios';
import { Link, useNavigate, useParams } from 'react-router-dom';

export default function EditApartment() {

    let navigate = useNavigate()
    
    const { apartmentId } = useParams();

    const [apartment, setApartment] = useState({
        apartmentId: "",
        floor: null,
        isOccupied: false,
        occupants: null,
        owner: "",
        area: null,
        apartmentType: ""
    })

    const{ floor, occupants, owner, area, apartmentType } = apartment;

    const onInputChange = (e) => {
        setApartment({...apartment, [e.target.name]:e.target.value})
    }

    useEffect(()=>{
        loadApartment();
    },[]);

    const onSubmit = async (e) =>{
        e.preventDefault();
        await axios.put(`http://localhost:7070/apartment/${apartmentId}`, apartment);
        navigate(`/viewapartment/${apartmentId}`);
    }

    const loadApartment = async () => {
        const result = await axios.get(`http://localhost:7070/apartment?apartmentId=${apartmentId}`);
        setApartment(result.data);
    }


  return <div className="container">
    <div className="row">
        <div className="col-md-6 offset-md-3 border rounded p-4 mt-2 shadow">
            <h2 className="text-center m-4">Edit Apartment</h2>
            <form onSubmit={(e)=>onSubmit(e)}>
            <div className="mb-3">
                <label htmlFor="ApartmentNumber" className="form-label">
                    Apartment Number
                </label>
                <input type={"text"} className="form-control" placeholder="Enter new Apartment Number" name="apartmentId"
                    value={apartmentId}
                    readOnly
                />
            </div>
            <div className="mb-3">
                <label htmlFor="Floor" className="form-label">
                    Floor
                </label>
                <input type={"text"} className="form-control" placeholder="Enter your floor" name="floor"
                    value={floor}
                    onChange={(e)=>onInputChange(e)}
                />
            </div>
            <div className="mb-3">
                <label htmlFor="Occupants" className="form-label">
                    Occupants
                </label>
                <input type={"text"} className="form-control" placeholder="Enter your occupants" name="occupants"
                    value={occupants}
                    readOnly
                />
            </div>
            <div className="mb-3">
                <label htmlFor="Owner" className="form-label">
                    Owner
                </label>
                <input type={"text"} className="form-control" placeholder="Enter your owner" name="owner"
                    value={owner}
                    onChange={(e)=>onInputChange(e)}
                />
            </div>
            <div className="mb-3">
                <label htmlFor="Area" className="form-label">
                    Area (sqft)
                </label>
                <input type={"text"} className="form-control" placeholder="Enter your Area" name="area"
                    value={area}
                    onChange={(e)=>onInputChange(e)}
                />
            </div>
            <div className="mb-3">
                <label htmlFor="ApartmentType" className="form-label">
                    Apartment Type
                </label>
                <input type={"text"} className="form-control" placeholder="Enter your apartment type" name="apartmentType"
                    value={apartmentType}
                    onChange={(e)=>onInputChange(e)}
                />
            </div>
            <button type="submit" className="btn btn-outline-primary">
                Submit
            </button>
            <Link className="btn btn-outline-danger mx-2" to ={`/viewapartment/${apartmentId}`}>
                Cancel
            </Link>
            </form>
        </div>
    </div>
  </div>
}
