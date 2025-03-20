import React from "react";
import "../App.css";
import Header from "../components/Sidebar";
import { Link } from "react-router-dom";

function LoggedIn() {
  return (
    <div className="home-container">
      <Header />
      <div className="welcome-text">
        <h1>You are logged in, please head back to <Link to="/home">Home</Link></h1>
      </div>
    </div>
  );
}

export default LoggedIn;