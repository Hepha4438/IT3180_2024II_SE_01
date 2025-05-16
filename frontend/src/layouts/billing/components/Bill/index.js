// prop-types is a library for typechecking of props
import PropTypes from "prop-types";

// @mui material components
import Icon from "@mui/material/Icon";

// Material Dashboard 2 React components
import MDBox from "components/MDBox";
import MDTypography from "components/MDTypography";
import MDButton from "components/MDButton";
import { QRcode, createPDF } from "../../api";
// Material Dashboard 2 React context
import { useMaterialUIController } from "context";

function Bill({
  name,
  company,
  total,
  used,
  noGutter,
  fee,
  endDate,
  pay,
  bill,
  setQrCodeData,
  setOpenQRModal,
  index,
  paidDate,
}) {
  const [controller] = useMaterialUIController();
  const { darkMode } = controller;

  const handlePayment = async (bill) => {
    try {
      const pdfUrl = await createPDF(localStorage.getItem("apartmentId"), bill.id);
      if (pdfUrl) {
        // Open PDF in a new tab
        window.location.href = pdfUrl;
        setTimeout(() => {
          window.location.reload();
        }, 2000);
      } else {
        alert("Unable to download the PDF file.");
      }
    } catch (err) {
      alert("An error occurred while generating the PDF bill.");
    }
  };

  return (
    <MDBox
      component="li"
      display="flex"
      justifyContent="space-between"
      alignItems="flex-start"
      bgColor={darkMode ? "transparent" : "grey-100"}
      borderRadius="lg"
      p={3}
      mb={noGutter ? 0 : 1}
      mt={2}
    >
      <MDBox width="100%" display="flex" flexDirection="column">
        <MDBox
          display="flex"
          justifyContent="space-between"
          alignItems={{ xs: "flex-start", sm: "center" }}
          flexDirection={{ xs: "column", sm: "row" }}
          mb={2}
        >
          <MDTypography variant="button" fontWeight="medium" textTransform="capitalize">
            {index}. {name}
          </MDTypography>

          <MDBox display="flex" alignItems="center" mt={{ xs: 2, sm: 0 }} ml={{ xs: -1.5, sm: 0 }}>
            <MDButton
              variant="text"
              color={darkMode ? "white" : "dark"}
              onClick={() => handlePayment(bill)}
            >
              <Icon>payment</Icon>&nbsp;Pay
            </MDButton>
          </MDBox>
        </MDBox>

        <MDBox mb={1} lineHeight={0}>
          <MDTypography variant="caption" color="text">
            Collected by:&nbsp;&nbsp;&nbsp;
            <MDTypography
              variant="caption"
              fontWeight="medium"
              textTransform="capitalize"
              color="error"
            >
              {company}
            </MDTypography>
          </MDTypography>
        </MDBox>

        <MDBox mb={1} lineHeight={0}>
          <MDTypography variant="caption" color="text">
            Total amount:&nbsp;&nbsp;&nbsp;
            <MDTypography variant="caption" fontWeight="medium" color="error">
              {total}
            </MDTypography>
          </MDTypography>
        </MDBox>

        <MDBox mb={1} lineHeight={0}>
          <MDTypography variant="caption" color="text">
            Price per unit:&nbsp;&nbsp;&nbsp;
            <MDTypography variant="caption" fontWeight="medium" color="error">
              {fee}
            </MDTypography>
          </MDTypography>
        </MDBox>

        <MDBox mb={1} lineHeight={0}>
          <MDTypography variant="caption" color="text">
            Units used:&nbsp;&nbsp;&nbsp;
            <MDTypography variant="caption" fontWeight="medium" color="error">
              {used}
            </MDTypography>
          </MDTypography>
        </MDBox>

        <MDBox mb={1} lineHeight={0}>
          <MDTypography variant="caption" color="text">
            Due date:&nbsp;&nbsp;&nbsp;
            <MDTypography variant="caption" fontWeight="medium" color="error">
              {endDate}
            </MDTypography>
          </MDTypography>
        </MDBox>

        <MDBox mb={1} lineHeight={0}>
          <MDTypography variant="caption" color="text">
            Payment status:&nbsp;&nbsp;&nbsp;
            <MDTypography variant="caption" fontWeight="medium" color="error">
              {pay}
            </MDTypography>
          </MDTypography>
        </MDBox>
      </MDBox>
    </MDBox>
  );
}

// Setting default values for the props of Bill
Bill.defaultProps = {
  noGutter: false,
};

// Typechecking props for the Bill
Bill.propTypes = {
  name: PropTypes.string.isRequired,
  company: PropTypes.string.isRequired,
  total: PropTypes.string.isRequired,
  vat: PropTypes.string.isRequired,
  noGutter: PropTypes.bool,
  fee: PropTypes.object.isRequired,
  used: PropTypes.number.isRequired,
  endDate: PropTypes.string.isRequired,
  pay: PropTypes.string.isRequired,
  bill: PropTypes.object.isRequired,
  setQrCodeData: PropTypes.func.isRequired,
  setOpenQRModal: PropTypes.func.isRequired,
  index: PropTypes.number.isRequired,
  paidDate: PropTypes.string,
};

export default Bill;
