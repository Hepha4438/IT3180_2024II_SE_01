import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Card from "@mui/material/Card";
import TextField from "@mui/material/TextField"; // Thêm ô nhập
import MDBox from "components/MDBox";
import MDTypography from "components/MDTypography";
import MDButton from "components/MDButton";
import Invoice from "layouts/billing/components/Invoice";

// import api from "services/api";
import { getAllInvoices, getRevenue } from "../../api"; // Import API
function Invoices() {
  const [invoices, setInvoices] = useState([]);
  const [searchTerm, setSearchTerm] = useState(""); // State lưu giá trị nhập
  const navigate = useNavigate();
  const userId = localStorage.getItem("apartmentId") || 3333;
  useEffect(() => {
    if (userId) {
      getRevenue(userId).then((data) => {
        if (data) {
          setInvoices(data);
          console.log(data);
        }
      });
    }
  }, [userId]);
  const filteredInvoices = invoices.filter(
    (invoice) => invoice.type.toLowerCase().includes(searchTerm.toLowerCase()) // Lọc không phân biệt hoa thường
  );

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat("vi-VN").format(amount);
  };
  return (
    <Card sx={{ height: "100%" }}>
      <MDBox pt={2} px={2} display="flex" justifyContent="space-between" alignItems="center">
        <MDTypography variant="h6" fontWeight="medium">
          Hóa đơn
        </MDTypography>
      </MDBox>

      {/* Ô nhập để tìm kiếm */}
      <MDBox p={2}>
        <TextField
          label="Tìm kiếm khoản thu"
          variant="outlined"
          fullWidth
          size="small"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
      </MDBox>

      <MDBox p={2}>
        <MDBox component="ul" display="flex" flexDirection="column" p={0} m={0}>
          {filteredInvoices.length > 0 ? (
            filteredInvoices.map((invoice, index) => (
              <Invoice
                key={invoice.id}
                date={invoice.type}
                id={`#${index + 1}`}
                price={`${formatCurrency(invoice.total)} VND`}
              />
            ))
          ) : (
            <MDTypography variant="body2" color="textSecondary">
              Không có hóa đơn nào.
            </MDTypography>
          )}
        </MDBox>
      </MDBox>
    </Card>
  );
}

export default Invoices;
