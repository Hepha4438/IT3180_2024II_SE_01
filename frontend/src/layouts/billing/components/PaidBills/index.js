import { useState, useEffect } from "react";
import Card from "@mui/material/Card";
import TextField from "@mui/material/TextField";
import MDBox from "components/MDBox";
import MDTypography from "components/MDTypography";
import { FormControl, InputLabel, Select, MenuItem, OutlinedInput, Box } from "@mui/material";
import Bill from "layouts/billing/components/Paid";
import { getRevenue, getFeeByType } from "../../api";
import Dialog from "@mui/material/Dialog";
import DialogContent from "@mui/material/DialogContent";
import QRModal from "../QR/QRModal";
// import FeeSearchBar from "./search";
function PaidBills() {
  const [bills, setBills] = useState([]); // Danh sách khoản thu
  const [fees, setFees] = useState({}); // Dữ liệu phí tương ứng
  const [searchTerm, setSearchTerm] = useState("");
  const [searchField, setSearchField] = useState("type"); // Mặc định tìm theo tên khoản thu
  const [searchKeyword, setSearchKeyword] = useState(""); // Nội dung tìm kiếm
  const [loading, setLoading] = useState(true);
  const userId = localStorage.getItem("apartmentId");
  const [searchFilter, setSearchFilter] = useState("type"); // default: tên khoản thu
  const [searchValue, setSearchValue] = useState(""); // giá trị tìm kiếm
  const [qrCodeData, setQrCodeData] = useState(null);
  const [openQRModal, setOpenQRModal] = useState(false);
  // Lấy danh sách hóa đơn theo userId
  useEffect(() => {
    const fetchBills = async () => {
      setLoading(true);
      try {
        const data = await getRevenue(userId);
        if (data) {
          setBills(data);
          console.log("setbill la : --------------", data);
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
      console.log("in bill");
      console.log(bills);
      console.log("in day");
      console.log(bills[0].endDate);
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
  // const filteredBills = bills.filter(
  //   (bill) =>
  //     bill.id.toString().toLowerCase().includes(searchTerm.toLowerCase()) ||
  //     (bill.type && bill.type.toLowerCase().includes(searchTerm.toLowerCase()))
  // );
  const filteredBills = bills
    .filter((bill) => bill.status === "Paid") // chỉ lấy bill chưa thanh toán
    .filter((bill) => {
      const value = bill[searchField]?.toLowerCase() || "";
      return value.includes(searchKeyword.toLowerCase());
    });
  const totalUnpaid = filteredBills.length;
  // hàm chuyển tiền sang số
  const formatCurrency = (amount) => {
    return new Intl.NumberFormat("vi-VN").format(amount);
  };
  const formatDeadline = (dateString) => {
    // Kiểm tra xem chuỗi đầu vào có hợp lệ không
    if (!dateString || typeof dateString !== "string") {
      return "Vô hạn thời gian";
    }
    // Tách phần ngày tháng năm (bỏ phần thời gian sau 'T')
    const datePart = dateString.split("T")[0];
    if (!datePart) {
      return "Vô hạn thời gian";
    }
    // Tách năm, tháng, ngày từ chuỗi
    const [year, month, day] = datePart.split("-");
    if (!year || !month || !day) {
      return "Vô hạn thời gian";
    }
    // Loại bỏ số 0 đứng đầu ở tháng và ngày
    const formattedMonth = parseInt(month, 10).toString();
    const formattedDay = parseInt(day, 10).toString();
    // Ghép các thành phần thành chuỗi kết quả
    return `${formattedDay} tháng ${formattedMonth} năm ${year}`;
  };
  return (
    <MDBox mt={3}>
      <MDTypography variant="h6" gutterBottom color="success" mb={1}>
        Các khoản đã thanh toán: <strong>{totalUnpaid}</strong> khoản
      </MDTypography>
      <MDBox
        component="ul"
        display="flex"
        flexDirection="column"
        p={0}
        m={0}
        sx={{
          maxHeight: "400px", // Chiều cao tối đa, bạn có thể điều chỉnh
          overflowY: "auto", // Cho phép cuộn dọc khi tràn
          pr: 1, // Padding phải để tránh che nội dung bởi thanh cuộn
        }}
      >
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
                paidDate={`${formatDeadline(bill.paidDate)}`}
                pay={`${bill.status == "Unpaid" ? "Chưa thanh toán" : "Đã thanh toán"}`}
                noGutter={index === filteredBills.length - 1}
                bill={bill} // truyền cả bill để dùng khi gửi về backend
                apartmentId={localStorage.getItem("apartmentId")}
                setQrCodeData={setQrCodeData}
                setOpenQRModal={setOpenQRModal}
                index={index + 1}
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
  );
}

export default PaidBills;
