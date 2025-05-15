import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";

// Material UI components
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import Typography from "@mui/material/Typography";
import CircularProgress from "@mui/material/CircularProgress";
import MDBox from "components/MDBox";
import MDButton from "components/MDButton";

export default function PaymentComplete() {
  const { paymentToken } = useParams(); // Lấy paymentToken từ URL
  const [loading, setLoading] = useState(true);
  const [paymentData, setPaymentData] = useState(null);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const API_URL = "http://localhost:7070";
    const fetchPaymentDetails = async () => {
      try {
        console.log("paymentToken trong paymentComplete là: ", paymentToken);
        const response = await axios.get(`${API_URL}/revenue/getPayment/${paymentToken}`, {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        });
        setPaymentData(response.data); // Lưu thông tin giao dịch
        setLoading(false);
      } catch (err) {
        setError("Không thể hoàn tất giao dịch. Vui lòng thử lại.");
        setLoading(false);
      }
    };

    fetchPaymentDetails();
  }, [paymentToken]);

  // Nếu trang đang tải
  if (loading) {
    return (
      <MDBox display="flex" justifyContent="center" alignItems="center" height="100vh">
        <CircularProgress />
      </MDBox>
    );
  }

  // Nếu có lỗi khi tải dữ liệu
  if (error) {
    return (
      <MDBox textAlign="center" mt={10}>
        <Typography variant="h6" color="error">
          {error}
        </Typography>
      </MDBox>
    );
  }

  return (
    <MDBox display="flex" justifyContent="center" mt={10}>
      <Card sx={{ maxWidth: 400, p: 3 }}>
        <CardContent>
          <Typography variant="h5" gutterBottom>
            Giao dịch #{paymentToken.slice(0, 8)}...
          </Typography>
          <Typography variant="body1" gutterBottom>
            Số tiền cần thanh toán: <strong>{paymentData.total.toLocaleString()} VND</strong>
          </Typography>
          <Typography variant="body2" color="text.secondary" gutterBottom>
            Trạng thái: {paymentData.status}
          </Typography>
          <MDBox mt={3}>
            <MDButton
              color="success"
              fullWidth
              onClick={async () => {
                try {
                  const response = await axios.get(
                    `http://localhost:7070/revenue/complete-payment/${paymentToken}`,
                    {
                      headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
                    }
                  );
                  alert("Giao dịch thành công!");
                  navigate("/billing"); // Chuyển hướng sau khi thanh toán thành công
                } catch (err) {
                  alert("Có lỗi trong quá trình thanh toán.");
                }
              }}
            >
              Thanh toán
            </MDButton>
          </MDBox>
        </CardContent>
      </Card>
    </MDBox>
  );
}
