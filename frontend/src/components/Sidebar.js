import React, { useState } from "react";
import { Sidebar, Menu, MenuItem, SubMenu, useProSidebar } from "react-pro-sidebar";

import { FaList, FaRegHeart } from "react-icons/fa";
import { FiHome, FiLogOut, FiArrowLeftCircle, FiArrowRightCircle } from "react-icons/fi";
import { RiPencilLine } from "react-icons/ri";
import { BiCog } from "react-icons/bi";
import { Link } from "react-router-dom";
import { useNavigate } from "react-router-dom";

import "./Sidebar.css";

export const isLoggedIn = () => !!localStorage.getItem('token');

export const getRole = () => localStorage.getItem('role');
const Header = () => {
  const { collapseSidebar, collapsed } = useProSidebar();
  const [menuCollapse, setMenuCollapse] = useState(true);
  const navigate = useNavigate();

  const menuIconClick = () => {
    setMenuCollapse(!menuCollapse);
    collapseSidebar();
  };
  const handleLogout = () => {
    if(localStorage.getItem("token") === null){
      alert("Bạn chưa đăng nhập!");
      return;
    }else{
    localStorage.removeItem("token");
    localStorage.removeItem("username");
    localStorage.removeItem("role");
    localStorage.removeItem("id");
    
    alert("Đăng xuất thành công!");
    
    navigate("/home");
    window.location.reload();
  }
};
  const role = getRole();
  const isAuthenticated = isLoggedIn();
  return (
    <div id="header">
      <Sidebar collapsed={menuCollapse}>
        <div className="sidebar-header">
          <div className="logotext">
            <p>{menuCollapse ? "App" : "Managing App"}</p>
          </div>
          <div className="closemenu" onClick={menuIconClick}>
            {menuCollapse ? <FiArrowRightCircle /> : <FiArrowLeftCircle />}
          </div>
        </div>

        <Menu>
          <MenuItem className="menu-item" icon={<FiHome />}>Home</MenuItem>
          <MenuItem className="menu-item" icon={<FaList />}>Category</MenuItem>
          <MenuItem className="menu-item" icon={<FaRegHeart />}>Favourite</MenuItem>
          {isAuthenticated && (role === "ADMIN" || role === "MANAGER") && <MenuItem className="menu-item" icon={<RiPencilLine />}>Author</MenuItem>}
          <MenuItem className="menu-item" icon={<BiCog />}>Settings</MenuItem>
        </Menu>

        <div className="sidebar-footer">
          <Menu>
            <MenuItem className="menu-item" icon={<FiLogOut />} onClick={handleLogout}>Logout
            </MenuItem>
          </Menu>
        </div>
      </Sidebar>
    </div>
  );
};

export default Header;
