/* eslint-disable react/prop-types */
/* eslint-disable react/function-component-definition */
import React, { useEffect, useState } from "react";
import axios from "axios";
import { Icon, Dialog, DialogTitle, DialogContent, DialogActions } from "@mui/material";
import MDBox from "components/MDBox";
import MDTypography from "components/MDTypography";
import MDButton from "components/MDButton";
import MDInput from "components/MDInput";

export default function data() {
  const [fees, setFees] = useState([]);
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

  const loadFees = async () => {
    try {
      const response = await axios.get("http://localhost:7070/fees");
      setFees(response.data);
    } catch (error) {
      console.error("Failed to load fees", error);
    }
  };

  useEffect(() => {
    loadFees();
  }, []);

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
    return fees.map((fee) => ({
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
        <MDButton color="info" onClick={handleCreateClick}>
          + Add Fee
        </MDButton>

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
            <MDButton onClick={handleCreateSubmit} color="info">
              Create
            </MDButton>
          </DialogActions>
        </Dialog>

        {/* Edit Dialog */}
        <Dialog open={editDialogOpen} onClose={handleEditClose}>
          <DialogTitle>Edit Fee</DialogTitle>
          <DialogContent>
            <MDBox display="flex" flexDirection="column" gap={2} mt={1}>
              <MDInput label="Type" name="type" value={editFee.type} disabled fullWidth />
              <MDInput
                label="Price Per Unit"
                name="pricePerUnit"
                value={editFee.pricePerUnit}
                onChange={handleEditInputChange}
                fullWidth
              />
            </MDBox>
          </DialogContent>
          <DialogActions>
            <MDButton onClick={handleEditClose} color="dark">
              Cancel
            </MDButton>
            <MDButton onClick={handleEditSubmit} color="info">
              Save
            </MDButton>
          </DialogActions>
        </Dialog>
      </MDBox>
    ),
  };
}
