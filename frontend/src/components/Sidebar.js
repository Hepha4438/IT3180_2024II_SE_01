import React, { useState } from "react";
import { Sidebar, Menu, MenuItem, SubMenu, useProSidebar } from "react-pro-sidebar";

import { FaList, FaRegHeart } from "react-icons/fa";
import { FiHome, FiLogOut, FiArrowLeftCircle, FiArrowRightCircle } from "react-icons/fi";
import { RiPencilLine } from "react-icons/ri";
import { BiCog } from "react-icons/bi";

import "./Sidebar.css";

const Header = () => {
  const { collapseSidebar, collapsed } = useProSidebar();
  const [menuCollapse, setMenuCollapse] = useState(true);

  const menuIconClick = () => {
    setMenuCollapse(!menuCollapse);
    collapseSidebar();
  };

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
          <MenuItem className="menu-item" icon={<RiPencilLine />}>Author</MenuItem>
          <MenuItem className="menu-item" icon={<BiCog />}>Settings</MenuItem>
        </Menu>

        <div className="sidebar-footer">
          <Menu>
            <MenuItem className="menu-item" icon={<FiLogOut />}>Logout</MenuItem>
          </Menu>
        </div>
      </Sidebar>
    </div>
  );
};

export default Header;
