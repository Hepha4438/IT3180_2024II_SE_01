import { useState, useEffect } from "react";
import Card from "@mui/material/Card";
import TextField from "@mui/material/TextField";
import MDBox from "components/MDBox";
import MDTypography from "components/MDTypography";
import Bill from "layouts/billing/components/Bill";
import { getRevenue, getFeeByType } from "../../api"; // Cần đảm bảo có getFeeByType

function BillingInformation() {
  const [bills, setBills] = useState([]); // Danh sách khoản thu
  const [fees, setFees] = useState({}); // Dữ liệu phí tương ứng
  const [searchTerm, setSearchTerm] = useState("");
  const [loading, setLoading] = useState(true);
  const userId = localStorage.getItem("apartmentId");

  // Lấy danh sách hóa đơn theo userId
  useEffect(() => {
    const fetchBills = async () => {
      setLoading(true);
      try {
        const data = await getRevenue(userId);
        if (data) {
          setBills(data);
        }
      } catch (error) {
        console.error("Lỗi khi lấy hóa đơn:", error);
      }
      setLoading(false);
    };

    fetchBills();
  }, [userId]);

  // Lấy phí theo từng loại hóa đơn
  useEffect(() => {
    const fetchFees = async () => {
      if (bills.length === 0) return; // Chỉ chạy khi có bills

      const feeData = {};
      for (const bill of bills) {
        if (bill.type && !feeData[bill.type]) {
          try {
            const fee = await getFeeByType(bill.type);
            feeData[bill.type] = fee; // Lưu phí theo loại
          } catch (error) {
            console.error(`Lỗi khi lấy phí cho loại ${bill.type}:`, error);
          }
        }
      }
      setFees(feeData);
    };

    fetchFees();
  }, [bills]);

  // Lọc danh sách theo ID hóa đơn hoặc tên khoản thu
  const filteredBills = bills.filter(
    (bill) =>
      bill.id.toString().toLowerCase().includes(searchTerm.toLowerCase()) ||
      (bill.type && bill.type.toLowerCase().includes(searchTerm.toLowerCase()))
  );
  // hàm chuyển tiền sang số
  const formatCurrency = (amount) => {
    return new Intl.NumberFormat("vi-VN").format(amount);
  };
  return (
    <Card id="billing-information">
      <MDBox pt={3} px={2}>
        <MDTypography variant="h6" fontWeight="medium">
          Thông tin chi tiết từng khoản thu
        </MDTypography>
      </MDBox>

      {/* Ô tìm kiếm */}
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

      <MDBox
        sx={{
          maxHeight: "500px", // Giới hạn chiều cao
          overflowY: "auto", // Thêm thanh cuộn
          border: "1px solid #ddd",
          borderRadius: "8px",
          padding: "8px",
        }}
      >
        <MDBox component="ul" display="flex" flexDirection="column" p={0} m={0}>
          {filteredBills.length > 0 ? (
            filteredBills.map((bill, index) => {
              const fee = fees[bill.type];
              return (
                <Bill
                  key={bill.id}
                  name={bill.type}
                  total={`${formatCurrency(bill.total)} VND`}
                  fee={fee ? `${formatCurrency(fee.pricePerUnit)} VND` : "Đang cập nhật..."}
                  used={`${formatCurrency(bill.used)} đơn vị`}
                  noGutter={index === filteredBills.length - 1}
                />
              );
            })
          ) : (
            <MDTypography variant="body2" color="textSecondary">
              Không có kết quả phù hợp.
            </MDTypography>
          )}
        </MDBox>
      </MDBox>
    </Card>
  );
}

export default BillingInformation;
