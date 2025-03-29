import React from "react";
import "../App.css";
import Header from "../components/Sidebar";

function Home() {
  const username = localStorage.getItem("username") || "User"; // Get the name or default to "User"

  return (
    <div className="home-container">
      <Header/>
      <div className="welcome-text">
        <h1>Welcome, {username}</h1>
      </div>
    </div>
  );
}

export default Home;
