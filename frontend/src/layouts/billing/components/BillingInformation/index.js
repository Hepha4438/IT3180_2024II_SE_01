import { useState, useEffect } from "react";
import Card from "@mui/material/Card";
import TextField from "@mui/material/TextField";
import MDBox from "components/MDBox";
import MDTypography from "components/MDTypography";
import { FormControl, InputLabel, Select, MenuItem, OutlinedInput, Box } from "@mui/material";
import Bill from "layouts/billing/components/Bill";
import { getRevenue, getFeeByType } from "../../api";
import Dialog from "@mui/material/Dialog";
import DialogContent from "@mui/material/DialogContent";
import QRModal from "../QR/QRModal";

function BillingInformation() {
  const [bills, setBills] = useState([]); // List of bills
  const [fees, setFees] = useState({}); // Fee data per type
  const [searchTerm, setSearchTerm] = useState("");
  const [searchField, setSearchField] = useState("type"); // Default: search by type
  const [searchKeyword, setSearchKeyword] = useState(""); // Search content
  const [loading, setLoading] = useState(true);
  const userId = localStorage.getItem("apartmentId");
  const [searchFilter, setSearchFilter] = useState("type"); // default: type
  const [searchValue, setSearchValue] = useState(""); // search value
  const [qrCodeData, setQrCodeData] = useState(null);
  const [openQRModal, setOpenQRModal] = useState(false);

  // Fetch list of bills by userId
  useEffect(() => {
    const fetchBills = async () => {
      setLoading(true);
      try {
        const data = await getRevenue(userId);
        if (data) {
          setBills(data);
          console.log("Fetched bills: ", data);
        }
      } catch (error) {
        console.error("Error fetching bills:", error);
      }
      setLoading(false);
    };

    fetchBills();
  }, [userId]);

  // Fetch fee by bill type
  useEffect(() => {
    const fetchFees = async () => {
      if (bills.length === 0) return;
      const feeData = {};
      for (const bill of bills) {
        if (bill.type && !feeData[bill.type]) {
          try {
            const fee = await getFeeByType(bill.type);
            feeData[bill.type] = fee;
          } catch (error) {
            console.error(`Error fetching fee for type ${bill.type}:`, error);
          }
        }
      }
      setFees(feeData);
    };

    fetchFees();
  }, [bills]);

  // Filter bills: unpaid only
  const filteredBills = bills
    .filter((bill) => bill.status === "Unpaid")
    .filter((bill) => {
      const value = bill[searchField]?.toLowerCase() || "";
      return value.includes(searchKeyword.toLowerCase());
    });

  const totalUnpaid = filteredBills.length;

  // Format currency
  const formatCurrency = (amount) => {
    return new Intl.NumberFormat("en-US").format(amount);
  };

  // Format date
  const formatDeadline = (dateString) => {
    if (!dateString || typeof dateString !== "string") {
      return "No deadline";
    }
    const datePart = dateString.split("T")[0];
    if (!datePart) {
      return "No deadline";
    }
    const [year, month, day] = datePart.split("-");
    if (!year || !month || !day) {
      return "No deadline";
    }
    const formattedMonth = parseInt(month, 10).toString();
    const formattedDay = parseInt(day, 10).toString();
    return `${formattedDay}/${formattedMonth}/${year}`;
  };

  return (
    <Card id="billing-information" sx={{ boxShadow: "none", border: "none" }}>
      {/* Search bar */}
      <MDBox display="flex" alignItems="center" mb={1}>
        {/* Select search field */}
        <MDBox>
          <select
            value={searchField}
            onChange={(e) => setSearchField(e.target.value)}
            style={{
              height: "38px",
              padding: "0 15px",
              borderRadius: "8px",
              borderColor: "#d2d6da",
              marginRight: "10px",
              width: "150px",
              fontSize: "14px",
              cursor: "pointer",
              transition: "all 0.2s ease-in-out",
            }}
          >
            <option value="type">Revenue Type</option>
            <option value="endDate">Payment Deadline</option>
          </select>
        </MDBox>

        {/* Input search */}
        <FormControl fullWidth variant="outlined" size="small">
          <OutlinedInput
            placeholder={`Enter ${
              searchField === "type"
                ? "revenue type"
                : searchField === "status"
                ? "status"
                : "payment deadline"
            }...`}
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
          />
        </FormControl>
      </MDBox>

      <MDBox pt={1} px={2}>
        <MDTypography variant="subtitle2" color="black" mb={1}>
          Total unpaid revenue(s): <strong>{totalUnpaid}</strong>
        </MDTypography>
      </MDBox>

      <MDBox
        sx={{
          maxHeight: "510px",
          overflowY: "auto",
          border: "0px solid #ddd",
          borderRadius: "2px",
          padding: "0 4px",
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
                  fee={fee ? `${formatCurrency(fee.pricePerUnit)} VND` : "Updating..."}
                  used={`${formatCurrency(bill.used)} units`}
                  endDate={`${formatDeadline(bill.endDate)}`}
                  pay={bill.status === "Unpaid" ? "Unpaid" : "Paid"}
                  noGutter={index === filteredBills.length - 1}
                  bill={bill}
                  apartmentId={localStorage.getItem("apartmentId")}
                  setQrCodeData={setQrCodeData}
                  setOpenQRModal={setOpenQRModal}
                  index={index + 1}
                />
              );
            })
          ) : (
            <MDTypography variant="body2" color="textSecondary">
              No matching results.
            </MDTypography>
          )}
        </MDBox>
      </MDBox>

      <QRModal open={openQRModal} onClose={() => setOpenQRModal(false)} qrCodeData={qrCodeData} />
    </Card>
  );
}

export default BillingInformation;
