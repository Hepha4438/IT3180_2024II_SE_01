import React from 'react'
import { Link } from 'react-router-dom';

export default function Navbar() {
    return (
        <div>
            <nav className="navbar navbar-expand-lg navbar-dark bg-dark">
                <div className="container-fluid">
                    <Link className="navbar-brand" to="/admin">
                        <img src="https://static.vecteezy.com/system/resources/previews/038/107/379/non_2x/apartment-icon-logo-design-template-vector.jpg" alt="Apartment Managing" width="45" height="45" />
                    </Link>
                    <button
                        className="navbar-toggler" 
                        type="button" 
                        data-bs-toggle="collapse" 
                        data-bs-target="#navbarSupportedContent" 
                        aria-controls="navbarSupportedContent" 
                        aria-expanded="false" 
                        aria-label="Toggle navigation"
                    >
                        <span className="navbar-toggler-icon"></span>
                    </button>
                    <Link className='btn btn-outline-light' to="/register">
                        Sign Up
                    </Link>
                </div>
            </nav>
        </div>
    )
}
