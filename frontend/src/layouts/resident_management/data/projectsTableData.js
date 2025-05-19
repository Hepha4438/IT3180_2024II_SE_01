/* eslint-disable react/prop-types */
/* eslint-disable react/function-component-definition */
/**
=========================================================
* Material Dashboard 2 React - v2.2.0
=========================================================

* Product Page: https://www.creative-tim.com/product/material-dashboard-react
* Copyright 2023 Creative Tim (https://www.creative-tim.com)

Coded by www.creative-tim.com

 =========================================================

* The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
*/

// @mui material components
import Icon from "@mui/material/Icon";
import { Dialog, DialogTitle, DialogContent, DialogActions, TextField } from "@mui/material";
import { Link } from "react-router-dom";
import React, { useEffect, useState } from "react";
import axios from "axios";

// Material Dashboard 2 React components
import MDBox from "components/MDBox";
import MDTypography from "components/MDTypography";
import MDAvatar from "components/MDAvatar";
import MDProgress from "components/MDProgress";
import MDButton from "components/MDButton";
import MDInput from "components/MDInput";

// Images
import LogoAsana from "assets/images/small-logos/logo-asana.svg";
import logoGithub from "assets/images/small-logos/github.svg";
import logoAtlassian from "assets/images/small-logos/logo-atlassian.svg";
import logoSlack from "assets/images/small-logos/logo-slack.svg";
import logoSpotify from "assets/images/small-logos/logo-spotify.svg";
import logoInvesion from "assets/images/small-logos/logo-invision.svg";

export default function data() {
  const Apartment = ({ id, type }) => (
    <MDBox display="flex" alignItems="center" lineHeight={1}>
      <MDAvatar
        src={
          type === "Studio"
            ? LogoAsana
            : type === "1BR"
            ? logoGithub
            : type === "2BR"
            ? logoAtlassian
            : type === "3BR"
            ? logoSpotify
            : logoSlack
        }
        name={id}
        size="sm"
        variant="rounded"
        sx={{
          p: 1,
          border: ({ borders: { borderWidth }, palette: { white } }) =>
            `${borderWidth[1]} solid ${white.main}`,
          backgroundColor: ({ palette: { grey } }) => grey[100],
          "&:hover": {
            transform: "scale(1.1)",
            transition: "transform 0.2s ease-in-out",
          },
        }}
      />
      <MDTypography
        display="block"
        variant="button"
        fontWeight="medium"
        ml={1}
        lineHeight={1}
        sx={{
          "&:hover": {
            color: ({ palette: { info } }) => info.main,
          },
        }}
      >
        {id}
      </MDTypography>
    </MDBox>
  );

  const Floor = ({ number }) => (
    <MDBox lineHeight={1} textAlign="left">
      <MDTypography
        variant="button"
        fontWeight="medium"
        sx={{
          backgroundColor: ({ palette: { dark } }) => `${dark.main}15`,
          color: ({ palette: { dark } }) => dark.main,
          padding: "5px 10px",
          borderRadius: "4px",
          display: "inline-block",
        }}
      >
        {number}
      </MDTypography>
    </MDBox>
  );

  // const Occupants = ({ count, capacity }) => (
  //   <MDBox display="flex" alignItems="center">
  //     <MDTypography variant="caption" color="text" fontWeight="medium">
  //       {count}/{capacity}
  //     </MDTypography>
  //     <MDBox ml={0.5} width="5rem">
  //       <MDProgress
  //         variant="gradient"
  //         color={count < capacity ? "info" : "success"}
  //         value={(count / capacity) * 100}
  //       />
  //     </MDBox>
  //   </MDBox>
  // );

  const Occupants = ({ number }) => (
    <MDBox lineHeight={1} textAlign="left">
      <MDTypography
        variant="caption"
        fontWeight="medium"
        sx={{
          backgroundColor: ({ palette: { dark } }) => `${dark.main}15`,
          color: ({ palette: { dark } }) => dark.main,
          padding: "5px 10px",
          borderRadius: "4px",
          display: "inline-block",
        }}
      >
        {number} occupants
      </MDTypography>
    </MDBox>
  );

  const Area = ({ size }) => (
    <MDBox lineHeight={1} textAlign="left">
      <MDTypography
        variant="caption"
        fontWeight="medium"
        sx={{
          backgroundColor: ({ palette: { dark } }) => `${dark.main}15`,
          color: ({ palette: { dark } }) => dark.main,
          padding: "5px 10px",
          borderRadius: "4px",
          display: "inline-block",
        }}
      >
        {size} m²
      </MDTypography>
    </MDBox>
  );

  const ApartmentType = ({ type }) => (
    <MDBox lineHeight={1} textAlign="left">
      <MDTypography
        variant="caption"
        fontWeight="medium"
        sx={{
          backgroundColor: ({ palette }) =>
            type === "Own"
              ? palette.success.main
              : type === "Rent"
              ? palette.warning.main
              : palette.dark.main,
          color: ({ palette: { white } }) => white.main,
          padding: "5px 10px",
          borderRadius: "4px",
          display: "inline-block",
        }}
      >
        {type}
      </MDTypography>
    </MDBox>
  );

  const [apartments, setApartments] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [searchType, setSearchType] = useState("apartmentId"); // apartmentId or floor
  const [errorMessage, setErrorMessage] = useState("");
  const [createDialogOpen, setCreateDialogOpen] = useState(false);
  const [newApartment, setNewApartment] = useState({
    apartmentId: "",
    floor: 1,
    area: 100,
    owner: "",
    apartmentType: "",
    occupants: 0,
  });

  useEffect(() => {
    loadApartments();
  }, []);

  const loadApartments = async () => {
    try {
      const response = await axios.get(
        "https://it3180-2024ii-se-01-final.onrender.com/apartment/all",
        {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        }
      );
      setApartments(response.data);
      setErrorMessage("");
    } catch (error) {
      console.error("Error loading apartments:", error);
      setApartments([]);
      setErrorMessage("Failed to load apartments.");
    }
  };

  const handleSearch = (e) => {
    e.preventDefault();
    if (!searchTerm.trim()) {
      loadApartments();
      return;
    }

    // Filter apartments based on search term and search type
    const filteredApartments = apartments.filter((apartment) => {
      if (searchType === "apartmentId") {
        return apartment.apartmentId
          ?.toString()
          .toLowerCase()
          .includes(searchTerm.trim().toLowerCase());
      } else if (searchType === "floor") {
        return apartment.floor?.toString() === searchTerm.trim();
      }
      return false;
    });

    if (filteredApartments.length === 0) {
      setErrorMessage(`No apartments found with ${searchType}: ${searchTerm}`);
    } else {
      setErrorMessage("");
    }

    setApartments(filteredApartments);
  };

  const handleCreateClick = () => {
    setCreateDialogOpen(true);
  };

  const handleCreateClose = () => {
    setCreateDialogOpen(false);
    setNewApartment({
      apartmentId: "",
      floor: 1,
      area: 100,
      owner: "",
      apartmentType: "",
      occupants: 0,
    });
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setNewApartment((prev) => ({
      ...prev,
      [name]: name === "floor" || name === "area" ? Number(value) : value,
    }));
  };

  const handleCreateSubmit = async () => {
    try {
      await axios.post("https://it3180-2024ii-se-01-final.onrender.com/apartment", newApartment, {
        headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
      });
      alert("Create apartment successfully!");
      loadApartments(); // Reload the apartments list after successful creation
      handleCreateClose();
    } catch (error) {
      console.error("Error creating apartment:", error);
      setErrorMessage("Failed to create apartment. Please try again.");
      handleCreateClose();
    }
  };

  // Generate rows dynamically based on filtered apartments
  const generateRows = () => {
    if (apartments.length === 0) {
      // Sample data when no apartments are loaded
      return [
        {
          apartmentId: <Apartment id="A101" type="Studio" />,
          floor: <Floor number="1" />,
          occupants: <Occupants count={1} capacity={2} />,
          area: <Area size={45} />,
          type: <ApartmentType type="Studio" />,
          action: (
            <MDButton variant="text" color="info">
              Details
            </MDButton>
          ),
        },
        {
          apartmentId: <Apartment id="B205" type="2BR" />,
          floor: <Floor number="2" />,
          occupants: <Occupants count={3} capacity={4} />,
          area: <Area size={75} />,
          type: <ApartmentType type="2BR" />,
          action: (
            <MDButton variant="text" color="info">
              Details
            </MDButton>
          ),
        },
      ];
    }

    return apartments.map((apartment) => ({
      apartmentId: <Apartment id={apartment.apartmentId} type={apartment.apartmentType || "N/A"} />,
      floor: <Floor number={apartment.floor || "N/A"} />,
      // occupants: (
      //   <Occupants
      //     count={apartment.residents ? apartment.residents.length : 0}
      //     capacity={apartment.occupants || 4}
      //   />
      // ),
      occupants: <Occupants number={apartment.occupants || 0} />,
      area: <Area size={apartment.area || 0} />,
      type: <ApartmentType type={apartment.apartmentType || "N/A"} />,
      action: (
        <MDButton
          variant="text"
          color="info"
          component={Link}
          to={`/apartment/${apartment.apartmentId}`}
        >
          Details
        </MDButton>
      ),
    }));
  };

  return {
    columns: [
      { Header: "Apartment ID", accessor: "apartmentId", width: "25%", align: "left" },
      { Header: "Floor", accessor: "floor", width: "15%", align: "center" },
      { Header: "Occupants", accessor: "occupants", width: "20%", align: "center" },
      { Header: "Area", accessor: "area", width: "15%", align: "center" },
      { Header: "Type", accessor: "type", width: "15%", align: "center" },
      { Header: "Action", accessor: "action", width: "10%", align: "center" },
    ],

    rows: generateRows(),

    // Search UI with dropdown to select search type
    searchUI: (
      <MDBox display="flex" flexDirection="column" mb={3}>
        <Dialog open={createDialogOpen} onClose={handleCreateClose} maxWidth="sm" fullWidth>
          <DialogTitle>Create New Apartment</DialogTitle>
          <DialogContent>
            <MDBox display="flex" flexDirection="column" gap={2} mt={2}>
              <TextField
                label="Apartment ID"
                name="apartmentId"
                value={newApartment.apartmentId}
                onChange={handleInputChange}
                fullWidth
                required
              />
              <TextField
                label="Floor"
                name="floor"
                type="number"
                value={newApartment.floor}
                onChange={handleInputChange}
                fullWidth
                required
                inputProps={{ min: 1 }}
              />
              <TextField
                label="Area"
                name="area"
                type="number"
                value={newApartment.area}
                onChange={handleInputChange}
                fullWidth
                required
                inputProps={{ min: 1 }}
              />
              <TextField
                label="ApartmentType"
                name="apartmentType"
                value={newApartment.apartmentType}
                onChange={handleInputChange}
                fullWidth
                inputProps={{ min: 1 }}
              />
              <TextField
                label="Owner"
                name="owner"
                value={newApartment.owner}
                onChange={handleInputChange}
                fullWidth
                inputProps={{ min: 1 }}
              />
            </MDBox>
          </DialogContent>
          <DialogActions>
            <MDButton onClick={handleCreateClose} color="dark">
              Cancel
            </MDButton>
            <MDButton onClick={handleCreateSubmit} color="info" variant="gradient">
              Create
            </MDButton>
          </DialogActions>
        </Dialog>

        <MDBox
          component="form"
          onSubmit={handleSearch}
          display="flex"
          alignItems="center"
          width="100%"
          mb={2}
        >
          <MDButton
            variant="gradient"
            color="dark"
            onClick={handleCreateClick}
            sx={{
              mr: 2,
              px: 2,
              py: 0.75,
              fontSize: "0.75rem",
              fontWeight: 500,
              display: "flex",
              alignItems: "center",
              gap: 0.5,
              minWidth: "auto",
              "&:hover": {
                transform: "translateY(-1px)",
                boxShadow: "0 7px 14px rgba(50, 50, 93, 0.1), 0 3px 6px rgba(0, 0, 0, 0.08)",
              },
              "& .material-icons-round": {
                fontSize: "1.25rem",
                marginRight: "2px",
              },
            }}
          >
            <Icon>add</Icon> Create Apartment
          </MDButton>
          <MDBox mr={1}>
            <select
              value={searchType}
              onChange={(e) => setSearchType(e.target.value)}
              style={{
                height: "42px",
                padding: "0 15px",
                borderRadius: "8px",
                borderColor: "#d2d6da",
                marginRight: "10px",
                width: "150px",
                fontSize: "14px",
                cursor: "pointer",
                transition: "all 0.2s ease-in-out",
                "&:hover": {
                  borderColor: "#1A73E8",
                },
              }}
            >
              <option value="apartmentId">Apartment ID</option>
              <option value="floor">Floor</option>
            </select>
          </MDBox>
          <MDInput
            label={`Search by ${searchType}`}
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            fullWidth
            sx={{
              "& .MuiOutlinedInput-root": {
                "&:hover fieldset": {
                  borderColor: ({ palette: { info } }) => info.main,
                },
              },
            }}
          />
          <MDButton
            type="submit"
            variant="gradient"
            color="dark"
            sx={{
              ml: 1,
              px: 3,
              "&:hover": {
                transform: "translateY(-1px)",
                boxShadow: "0 7px 14px rgba(50, 50, 93, 0.1), 0 3px 6px rgba(0, 0, 0, 0.08)",
              },
            }}
          >
            <Icon>search</Icon>
          </MDButton>
          <MDButton
            variant="outlined"
            color="dark"
            onClick={() => {
              setSearchTerm("");
              loadApartments();
            }}
            sx={{
              ml: 1,
              px: 3,
              "&:hover": {
                transform: "translateY(-1px)",
                boxShadow: "0 7px 14px rgba(50, 50, 93, 0.1), 0 3px 6px rgba(0, 0, 0, 0.08)",
              },
            }}
          >
            <Icon>refresh</Icon>
          </MDButton>
        </MDBox>
        {errorMessage && (
          <MDTypography color="error" variant="caption" sx={{ mt: 1 }}>
            {errorMessage}
          </MDTypography>
        )}
      </MDBox>
    ),
  };
}
