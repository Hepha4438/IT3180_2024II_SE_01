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

import React, { useState } from "react";
import Card from "@mui/material/Card";
import Icon from "@mui/material/Icon";
import MDBox from "components/MDBox";
import MDTypography from "components/MDTypography";
import MDButton from "components/MDButton";
import Select from "@mui/material/Select";
import InputLabel from "@mui/material/InputLabel";
import FormControl from "@mui/material/FormControl";
import MenuItem from "@mui/material/MenuItem";
function Calendar() {
  // State để quản lý tháng/năm hiện tại
  const [currentDate, setCurrentDate] = useState(new Date()); // Tháng 3/2020 (theo dữ liệu mẫu)

  // Lấy thông tin về tháng hiện tại
  const year = currentDate.getFullYear();
  const month = currentDate.getMonth();
  const firstDayOfMonth = new Date(year, month, 1).getDay(); // Ngày đầu tiên của tháng là thứ mấy
  const daysInMonth = new Date(year, month + 1, 0).getDate(); // Số ngày trong tháng

  // Tạo mảng các ngày để hiển thị
  const daysArray = Array.from({ length: firstDayOfMonth }, () => null).concat(
    Array.from({ length: daysInMonth }, (_, i) => i + 1)
  );

  // Điều hướng tháng trước/tháng sau
  const handlePrevMonth = () => {
    setCurrentDate(new Date(year, month - 1, 1));
  };

  const handleNextMonth = () => {
    setCurrentDate(new Date(year, month + 1, 1));
  };

  // Kiểm tra xem ngày nào có giao dịch
  // const getTransactionsForDay = (day) => {
  //   const dateString = `${year}-${String(month + 1).padStart(2, "0")}-${String(day).padStart(
  //     2,
  //     "0"
  //   )}`;
  //   return transactionData.filter((transaction) => transaction.date === dateString);
  // };

  // Định dạng tên tháng
  const monthNames = [
    "January",
    "February",
    "March",
    "April",
    "May",
    "June",
    "July",
    "August",
    "September",
    "October",
    "November",
    "December",
  ];
  const years = Array.from({ length: 20 }, (_, i) => 2020 + i);
  const handleChangeMonth = (event) => {
    const newMonth = event.target.value;
    setCurrentDate(new Date(year, newMonth, 1));
  };
  const handleChangeYear = (event) => {
    const newYear = event.target.value;
    setCurrentDate(new Date(newYear, month, 1));
  };
  return (
    <Card sx={{ height: "100%" }}>
      {/* Header của lịch */}
      <MDBox display="flex" justifyContent="space-between" alignItems="center" pt={3} px={2}>
        <MDTypography variant="h6" fontWeight="medium" textTransform="capitalize">
          Lịch
        </MDTypography>
        <MDBox display="flex" alignItems="center">
          <MDButton variant="text" color="dark" onClick={handlePrevMonth}>
            <Icon>chevron_left</Icon>
          </MDButton>
          <FormControl sx={{ minWidth: 120 }}>
            <InputLabel>Month</InputLabel>
            <Select value={month} onChange={handleChangeMonth} label="Month">
              {monthNames.map((monthName, index) => (
                <MenuItem key={index} value={index}>
                  {monthName}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
          <FormControl sx={{ minWidth: 120 }}>
            <InputLabel>Year</InputLabel>
            <Select value={year} onChange={handleChangeYear} label="Year">
              {years.map((yearOption) => (
                <MenuItem key={yearOption} value={yearOption}>
                  {yearOption}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
          <MDTypography variant="button" color="text" fontWeight="regular">
            {monthNames[month]} {year}
          </MDTypography>
          <MDButton variant="text" color="dark" onClick={handleNextMonth}>
            <Icon>chevron_right</Icon>
          </MDButton>
        </MDBox>
      </MDBox>

      {/* Hiển thị các ngày trong tuần */}
      <MDBox pt={3} pb={2} px={2}>
        <MDBox
          display="grid"
          gridTemplateColumns="repeat(7, 1fr)"
          textAlign="center"
          mb={1}
          sx={{ borderBottom: "1px solid #e0e0e0" }}
        >
          {["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"].map((day) => (
            <MDTypography
              key={day}
              variant="caption"
              color="text"
              fontWeight="bold"
              textTransform="uppercase"
              p={1}
            >
              {day}
            </MDTypography>
          ))}
        </MDBox>

        {/* Hiển thị các ngày trong tháng */}
        <MDBox display="grid" gridTemplateColumns="repeat(7, 1fr)" textAlign="center" gap={1}>
          {daysArray.map((day, index) => {
            const today = new Date();
            const isToday =
              day &&
              today.getDate() === day &&
              today.getMonth() === month &&
              today.getFullYear() === year;

            return (
              <MDBox
                key={index}
                p={1}
                sx={{
                  height: "60px",
                  display: "flex",
                  flexDirection: "column",
                  alignItems: "center",
                  justifyContent: "center",
                  borderRadius: "4px",
                  backgroundColor: isToday ? "#1976d2" : "transparent",
                  color: isToday ? "white" : "black",
                  fontWeight: isToday ? "bold" : "regular",
                  cursor: day ? "pointer" : "default",
                  "&:hover": day ? { backgroundColor: "#f0f0f0" } : {},
                }}
              >
                {day ? (
                  <MDTypography variant="body2" fontWeight="regular">
                    {day}
                  </MDTypography>
                ) : null}
              </MDBox>
            );
          })}
        </MDBox>
      </MDBox>
    </Card>
  );
}

export default Calendar;
