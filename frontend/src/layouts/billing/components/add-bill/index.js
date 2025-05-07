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
import Card from "@mui/material/Card";
import Grid from "@mui/material/Grid";
import Icon from "@mui/material/Icon";
import Tooltip from "@mui/material/Tooltip";
import TextField from "@mui/material/TextField";
// Material Dashboard 2 React components
import MDBox from "components/MDBox";
import MDTypography from "components/MDTypography";
import MDButton from "components/MDButton";
import MenuItem from "@mui/material/MenuItem";
import Select from "@mui/material/Select";
import React, { useState } from "react";
import { createRevenue } from "../../api";
// Images
import masterCardLogo from "assets/images/logos/mastercard.png";
import visaLogo from "assets/images/logos/visa.png";

// Material Dashboard 2 React context
import { useMaterialUIController } from "context";

function AddRevenue() {
  const [controller] = useMaterialUIController();
  const { darkMode } = controller;
  const feeRates = {
    water: 5000, // Giá mỗi đơn vị nước
    electricity: 1230, // Giá mỗi đơn vị điện
    service: 8000, // Phí dịch vụ cố định
    donate: 0, // Quyên góp
  };
  const [newRevenue, setNewRevenue] = useState({
    type: "",
    apartmentID: "",
    total: "",
    fee: "",
    used: "",
  });
  // Xử lý thay đổi thông tin khi người dùng nhập
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    // Cập nhật giá trị nhập vào
    setNewRevenue((prevState) => {
      const updatedState = { ...prevState, [name]: value };
      // Nếu thay đổi `fee` hoặc `used`, tính lại `total`
      if (name === "fee" || name === "used") {
        updatedState.total = (updatedState.fee || 0) * (updatedState.used || 0);
      }
      return updatedState;
    });
  };
  // xử lý khi chọn khoản thu
  const handleTypeChange = (event) => {
    const selectedType = event.target.value;
    const fee = feeRates[selectedType] || 0; // Giá cố định theo loại
    setNewRevenue((prevState) => ({
      ...prevState,
      type: selectedType,
      fee: feeRates[selectedType],
      total: fee * prevState.used,
    }));
  };
  const handleUsedChange = (event) => {
    const used = Number(event.target.value) || 0;
    setNewRevenue((prevState) => ({
      ...prevState,
      used,
      total: prevState.fee * used, // Cập nhật tổng tiền tự động
    }));
  };

  // Thêm khoản thu vào danh sách (hoặc gửi dữ liệu đi)
  const handleAddRevenue = async () => {
    if (!newRevenue.type || !newRevenue.used) {
      alert("Vui lòng chọn khoản thu và nhập số đơn vị đã dùng!");
      return;
    }
    const payload = {
      type: newRevenue.type,
      apartmentId: localStorage.getItem("apartmentId").toString(), // Lấy từ localStorage
      // fee: newRevenue.fee,
      used: newRevenue.used,
      total: newRevenue.used * newRevenue.fee, // Tính tổng tiền
      status: "false",
    };
    try {
      console.log(payload);
      const result = await createRevenue(payload);
      console.log("Thêm khoản thu thành công:", result);
      alert("Khoản thu đã được thêm!");
      // Reset form sau khi gửi thành công
      setNewRevenue({ type: "", fee: "", used: "", total: "" });
    } catch (error) {
      console.error("Lỗi khi thêm khoản thu:", error);
      alert("Có lỗi xảy ra khi tạo khoản thu. Vui lòng thử lại!");
    }
  };
  return (
    <Card id="add-revenue">
      <MDBox pt={2} px={2} display="flex" justifyContent="space-between" alignItems="center">
        <MDTypography variant="h6" fontWeight="medium"></MDTypography>
        <MDButton variant="gradient" color="dark" onClick={handleAddRevenue}>
          <Icon sx={{ fontWeight: "bold" }}>add</Icon>
          &nbsp;Thêm khoản thu
        </MDButton>
      </MDBox>
      <MDBox p={2}>
        <Grid container spacing={3}>
          {/* Trường Nhập Tên Khoản Thu */}
          <Grid item xs={12} md={12}>
            <TextField
              select
              fullWidth
              name="type"
              label="Chọn khoản thu"
              value={newRevenue.type}
              onChange={handleTypeChange}
              SelectProps={{
                native: true,
              }}
            >
              <option value="" disabled></option>
              <option value="water">Nước</option>
              <option value="electricity">Điện</option>
              <option value="service">Dịch vụ</option>
              <option value="donate">Quyên góp</option>
            </TextField>
          </Grid>
          {/* Trường Nhập căn hộ */}
          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              label={`ID căn hộ: ${localStorage.getItem("apartmentId") || 3333}`}
              name="apartmentID"
              value={newRevenue.apartmentID}
              onChange={handleInputChange}
              variant="outlined"
              margin="normal"
              disabled
            />
          </Grid>
          {/* Trường Nhập Tổng Tiền */}
          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              label="Tổng Tiền"
              name="total"
              value={newRevenue.total}
              onChange={handleInputChange}
              variant="outlined"
              margin="normal"
              type="number"
              disabled
            />
          </Grid>
          {/* Trường Nhập Giá Trên Một Đơn Vị */}
          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              label="Giá Trên Một Đơn Vị"
              name="fee"
              value={newRevenue.fee}
              onChange={handleInputChange}
              variant="outlined"
              margin="normal"
              type="number"
            />
          </Grid>
          {/* Trường Nhập Số Đơn Vị Đã Dùng */}
          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              label="Số Đơn Vị Đã Dùng"
              name="used"
              value={newRevenue.used}
              onChange={handleUsedChange}
              variant="outlined"
              margin="normal"
              type="number"
            />
          </Grid>
        </Grid>
      </MDBox>
    </Card>
  );
}

export default AddRevenue;
