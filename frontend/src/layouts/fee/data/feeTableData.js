import React, { useEffect, useState } from "react";
import axios from "axios";
import { Icon, Dialog, DialogTitle, DialogContent, DialogActions } from "@mui/material";
import MDBox from "components/MDBox";
import MDTypography from "components/MDTypography";
import MDButton from "components/MDButton";
import MDInput from "components/MDInput";

export default function data() {
  const [fees, setFees] = useState([]);
  const [filteredFees, setFilteredFees] = useState([]);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [selectedFee, setSelectedFee] = useState(null);
  const [createDialogOpen, setCreateDialogOpen] = useState(false);
  const [editDialogOpen, setEditDialogOpen] = useState(false);

  const [newFee, setNewFee] = useState({
    type: "",
    pricePerUnit: "",
  });

  const [editFee, setEditFee] = useState({
    type: "",
    pricePerUnit: "",
  });

  const [searchTerm, setSearchTerm] = useState("");
  const [searchType, setSearchType] = useState("type"); // Default search by 'type'

  const loadFees = async () => {
    try {
      const response = await axios.get("http://localhost:7070/fees");
      setFees(response.data);
      setFilteredFees(response.data); // Set initial fees as filtered
    } catch (error) {
      console.error("Failed to load fees", error);
    }
  };

  // Filter fees based on search term
  const filterFees = () => {
    const filtered = fees.filter((fee) => {
      return fee[searchType]?.toString().toLowerCase().includes(searchTerm.toLowerCase());
    });
    setFilteredFees(filtered);
  };

  useEffect(() => {
    loadFees();
  }, []);

  useEffect(() => {
    filterFees(); // Filter whenever searchTerm or searchType changes
  }, [searchTerm, searchType]);

  const handleDeleteClick = (fee) => {
    setSelectedFee(fee);
    setDeleteDialogOpen(true);
  };

  const handleDeleteConfirm = async () => {
    try {
      await axios.delete(`http://localhost:7070/fees/${selectedFee.type}`);
      loadFees();
      setDeleteDialogOpen(false);
    } catch (error) {
      console.error("Failed to delete fee", error);
    }
  };

  const handleDeleteCancel = () => {
    setDeleteDialogOpen(false);
    setSelectedFee(null);
  };

  const handleCreateClick = () => {
    setCreateDialogOpen(true);
  };

  const handleCreateClose = () => {
    setCreateDialogOpen(false);
    setNewFee({ type: "", pricePerUnit: "" });
  };

  const handleCreateSubmit = async () => {
    try {
      await axios.post("http://localhost:7070/fees", newFee);
      loadFees();
      handleCreateClose();
    } catch (error) {
      console.error("Failed to create fee", error);
    }
  };

  const handleEditClick = (fee) => {
    setEditFee(fee);
    setEditDialogOpen(true);
  };

  const handleEditClose = () => {
    setEditDialogOpen(false);
    setEditFee({ type: "", pricePerUnit: "" });
  };

  const handleEditSubmit = async () => {
    try {
      await axios.put(`http://localhost:7070/fees/${editFee.type}`, editFee);
      loadFees();
      handleEditClose();
    } catch (error) {
      console.error("Failed to update fee", error);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setNewFee((prev) => ({ ...prev, [name]: value }));
  };

  const handleEditInputChange = (e) => {
    const { name, value } = e.target;
    setEditFee((prev) => ({ ...prev, [name]: value }));
  };

  const generateRows = () => {
    return filteredFees.map((fee) => ({
      type: (
        <MDTypography variant="button" fontWeight="medium">
          {fee.type}
        </MDTypography>
      ),
      pricePerUnit: (
        <MDTypography variant="caption" color="text">
          {fee.pricePerUnit}
        </MDTypography>
      ),
      action: (
        <MDBox display="flex" gap={1}>
          <MDButton variant="text" color="info" onClick={() => handleEditClick(fee)}>
            <Icon>edit</Icon>
          </MDButton>
          <MDButton variant="text" color="error" onClick={() => handleDeleteClick(fee)}>
            <Icon>delete</Icon>
          </MDButton>
        </MDBox>
      ),
    }));
  };

  return {
    columns: [
      { Header: "Type", accessor: "type", width: "40%", align: "left" },
      { Header: "Price Per Unit", accessor: "pricePerUnit", width: "30%", align: "left" },
      { Header: "Action", accessor: "action", width: "30%", align: "center" },
    ],

    rows: generateRows(),

    searchUI: (
      <MDBox>
        <MDBox
          component="form"
          onSubmit={(e) => {
            e.preventDefault();
            filterFees();
          }}
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
            <Icon>add</Icon> Create Fee
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
              <option value="type">Type</option>
              <option value="pricePerUnit">Price Per Unit</option>
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
              loadFees(); // Reset fees to the original list
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

        {/* Delete Dialog */}
        <Dialog open={deleteDialogOpen} onClose={handleDeleteCancel}>
          <DialogTitle>Delete Fee</DialogTitle>
          <DialogContent>
            <MDTypography>
              Are you sure you want to delete fee &quot;{selectedFee?.type}&quot;?
            </MDTypography>
          </DialogContent>
          <DialogActions>
            <MDButton onClick={handleDeleteCancel} color="dark">
              Cancel
            </MDButton>
            <MDButton onClick={handleDeleteConfirm} color="error">
              Delete
            </MDButton>
          </DialogActions>
        </Dialog>

        {/* Create Dialog */}
        <Dialog open={createDialogOpen} onClose={handleCreateClose}>
          <DialogTitle>Create New Fee</DialogTitle>
          <DialogContent>
            <MDBox display="flex" flexDirection="column" gap={2} mt={1}>
              <MDInput
                label="Type"
                name="type"
                value={newFee.type}
                onChange={handleInputChange}
                fullWidth
              />
              <MDInput
                label="Price Per Unit"
                name="pricePerUnit"
                value={newFee.pricePerUnit}
                onChange={handleInputChange}
                fullWidth
              />
            </MDBox>
          </DialogContent>
          <DialogActions>
            <MDButton onClick={handleCreateClose} color="dark">
              Cancel
            </MDButton>
            <MDButton onClick={handleCreateSubmit} color="success">
              Create
            </MDButton>
          </DialogActions>
        </Dialog>
      </MDBox>
    ),
  };
}
