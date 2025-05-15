import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import axios from "axios";
import { Grid, Paper } from "@mui/material";
import Icon from "@mui/material/Icon";
import MDBox from "components/MDBox";
import MDTypography from "components/MDTypography";
import DashboardLayout from "examples/LayoutContainers/DashboardLayout";
import DashboardNavbar from "examples/Navbars/DashboardNavbar";
import Footer from "examples/Footer";

// Bản đồ màu & icon theo type
const typeColorMap = {
  success: { icon: "check_circle", color: "#4caf50" },
  warning: { icon: "warning", color: "#ff9800" },
  emergency: { icon: "error", color: "#f44336" },
  default: { icon: "info", color: "#2196f3" },
};

const UserNotificationPage = () => {
  const [notifications, setNotifications] = useState([]);
  const { id } = useParams();

  useEffect(() => {
    const fetchNotifications = async () => {
      try {
        const res = await axios.get(`http://localhost:7070/notifications/user/${id}`, {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        });
        setNotifications(res.data);
      } catch (error) {
        console.error("Failed to fetch notifications", error);
      }
    };

    fetchNotifications();
  }, [id]);

  return (
    <DashboardLayout>
      {" "}
      {/* Bọc nội dung vào DashboardLayout */}
      <DashboardNavbar /> {/* Thêm thanh điều hướng */}
      <MDBox p={3}>
        <MDTypography variant="h5" fontWeight="bold" mb={3}>
          Your notifications
        </MDTypography>

        {notifications.length > 0 ? (
          <Grid container spacing={2}>
            {notifications.map((noti, index) => {
              const typeInfo = typeColorMap[noti.type] || typeColorMap.default;
              return (
                <Grid item xs={12} key={noti.id || `notification-${index}`}>
                  <Paper
                    elevation={1}
                    sx={{
                      p: 2,
                      display: "flex",
                      alignItems: "flex-start",
                      gap: 2,
                      transition: "all 0.2s",
                      "&:hover": {
                        transform: "translateY(-5px)",
                        boxShadow: 3,
                      },
                    }}
                  >
                    <Icon
                      sx={{
                        color: typeInfo.color,
                        fontSize: "2rem",
                        alignSelf: "flex-start",
                        mt: "6px", // hoặc alignSelf: "center"
                      }}
                    >
                      {typeInfo.icon}
                    </Icon>

                    <MDBox>
                      <MDTypography
                        variant="subtitle1"
                        fontWeight="medium"
                        sx={{ color: typeInfo.color }}
                      >
                        {noti.title || "Không tiêu đề"}
                      </MDTypography>

                      <MDTypography
                        variant="button"
                        color="text.primary"
                        sx={{
                          display: "block",
                          whiteSpace: "pre-line",
                          wordBreak: "break-word",
                        }}
                      >
                        {noti.content || "Không có nội dung"}
                      </MDTypography>

                      <MDTypography
                        variant="caption"
                        color="text"
                        sx={{ display: "block", mt: 0.5 }}
                      >
                        Thời gian: {new Date(noti.createdAt).toLocaleString("vi-VN")}
                      </MDTypography>

                      <MDTypography
                        variant="caption"
                        color="text"
                        sx={{ display: "block", mt: 0.5 }}
                      >
                        Loại: {noti.type}
                      </MDTypography>
                    </MDBox>
                  </Paper>
                </Grid>
              );
            })}
          </Grid>
        ) : (
          <MDBox textAlign="center" py={3}>
            <MDTypography variant="body2" color="text">
              Bạn chưa có thông báo nào.
            </MDTypography>
          </MDBox>
        )}
      </MDBox>
      <Footer />
    </DashboardLayout>
  );
};

export default UserNotificationPage;
