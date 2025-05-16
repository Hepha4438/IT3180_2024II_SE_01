import axios from "axios";

const API_URL = "http://localhost:7070"; // URL

// lấy danh sách hóa đơn
export const getAllInvoices = async () => {
  const response = await axios.get(`${API_URL}/revenue/all`, {
    headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
  });
  return response.data;
};

// lấy hóa đơn theo apartmentId
export const getRevenue = async (id = null) => {
  try {
    const response = await axios.get(`${API_URL}/revenue/${id}`, {
      headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
    });
    console.log("response la: ---------------------", response.data);
    return response.data;
  } catch (error) {
    console.error("Lỗi khi lấy dữ liệu doanh thu:", error);
    return null;
  }
};

// lấy số đơn vị trên 1 đơn theo type
export const getFeeByType = async (type = null) => {
  try {
    const response = await axios.get(`${API_URL}/fees/${type}`, {
      headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
    });
    return response.data;
  } catch (error) {
    console.error("Lỗi khi lấy dữ liệu doanh thu:", error);
    return null;
  }
};

//   // lấy hóa đơn theo apratmentID
// export const getRevenueByApartment = async (apartmentId = null) => {
//     try {
//       const response = await axios.put(`${API_URL}/revenue`, {
//         params: apartmentId ? { apartmentId } : {}, // ID
//       });
//       return response.data;
//     } catch (error) {
//       console.error("Lỗi khi lấy dữ liệu doanh thu:", error);
//       return null;
//     }
//   }

// cập nhật tổng doanh thu căn hộ
export const updateTotalRevenueOfApartment = async (apartmentId) => {
  try {
    // Gửi yêu cầu PUT đến server với apartmentId trong URL
    const response = await axios.put(`${API_URL}/apartment/${apartmentId}/total`, {
      headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
    });
    return response.data; // Dữ liệu trả về là đối tượng apartment đã được cập nhật
  } catch (error) {
    console.error("Lỗi khi cập nhật doanh thu của căn hộ:", error);
    return null;
  }
};

// Cập nhật thông tin doanh thu
export const updateRevenue = async (id, revenueDTO) => {
  try {
    // Gửi yêu cầu PUT đến server với id trong URL và dữ liệu revenueDTO trong body
    const response = await axios.put(`${API_URL}/revenue/${id}`, revenueDTO, {
      headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
    });
    return response.data; // Dữ liệu trả về là đối tượng Revenue đã được cập nhật
  } catch (error) {
    console.error("Lỗi khi cập nhật doanh thu:", error);
    return null;
  }
};

// lấy hóa đơn theo apartmentID và type

export const getRevenueByApartmentAndType = async (apartmentId = null, type = null) => {
  try {
    const response = await axios.get(
      `${API_URL}/revenue`,
      {
        params: apartmentId && type ? { apartmentId, type } : {}, // ID vaf type
      },
      { headers: { Authorization: `Bearer ${localStorage.getItem("token")}` } }
    );
    return response.data;
  } catch (error) {
    console.error("Lỗi khi lấy dữ liệu doanh thu:", error);
    return null;
  }
};

// // lấy danh sách giao dịch
// export const getTransactions = async () => {
//   const response = await axios.get(`${API_URL}/transactions`);
//   return response.data;
// };

// // lấy phương thức thanh toán
// export const getPaymentMethods = async () => {
//   const response = await axios.get(`${API_URL}/payment-methods`);
//   return response.data;
// };

// tạo hóa đơn mới
export const createRevenue = async (revenueDTO) => {
  const response = await axios.post(`${API_URL}/revenue`, revenueDTO, {
    headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
  });
  return response.data;
};

// Xóa hóa đơn theo id
export const deleteRevenue = async (id) => {
  try {
    // Gửi yêu cầu DELETE đến server với id trong query string
    const response = await axios.delete(
      `${API_URL}/delete`,
      {
        params: { id }, // Truyền id qua query string
      },
      { headers: { Authorization: `Bearer ${localStorage.getItem("token")}` } }
    );
    return response.data; // Dữ liệu trả về là thông điệp thành công
  } catch (error) {
    console.error("Lỗi khi xóa doanh thu:", error);
    return null;
  }
};

//Tao ma QR
export const QRcode = async (paymentToken) => {
  console.log("paymentToken trong api.js la : ---------------------", paymentToken);
  try {
    // Gửi yêu cầu PUT đến server với id trong URL
    const response = await axios.get(`http://localhost:7070/revenue/getQRCode/${paymentToken}`, {
      headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
    });
    console.log("response la: ---------------------", response.data);
    return response.data; // Dữ liệu trả về là đối tượng Revenue đã được cập nhật
    // console.log("Dữ liệu thanh toán:", data);
  } catch (error) {
    console.error("Lỗi khi thanh toán:", error);
    return null;
  }
};

// Tao pdf
export const createPDF = async (apartmentId, id, isQR) => {
  console.log("paymentToken trong api.js la : ---------------------", id);
  try {
    // Gửi yêu cầu PUT đến server với id trong URL
    const response = await axios.get(
      `http://localhost:7070/apartment/${apartmentId}/bill?id=${id}&isQR=${isQR}`,
      {
        headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        responseType: "blob", // Đặt responseType là blob để nhận dữ liệu nhị phân
      }
    );
    console.log("response la: ---------------------", response.data);
    const file = new Blob([response.data], { type: "application/pdf" });
    const fileURL = URL.createObjectURL(file);
    return fileURL; // Dữ liệu trả về là đối tượng Revenue đã được cập nhật
    // console.log("Dữ liệu thanh toán:", data);
  } catch (error) {
    console.error("Lỗi khi tạo PDF:", error);
    return null;
  }
};

// Thanh toán hóa đơn
export const payBill = async (paymentToken) => {
  try {
    // Gửi yêu cầu PUT đến server với id trong URL
    const response = await axios.put(`${API_URL}/revenue/complete-payment/${paymentToken}`);
    return response.data; // Dữ liệu trả về là đối tượng Revenue đã được cập nhật
    // console.log("Dữ liệu thanh toán:", data);
  } catch (error) {
    console.error("Lỗi khi thanh toán:", error);
    return null;
  }
};

//lay contribution qua apartmentId
export const getContribution = async (apartmentId) => {
  try {
    const response = await axios.get(`${API_URL}/revenue/contribution/${apartmentId}`, {
      headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
    });
    return response.data;
  } catch (error) {
    console.error("Lỗi khi lấy dữ liệu doanh thu:", error);
    return null;
  }
};

export const getRevenueNotContribution = async (apartmentId) => {
  try {
    const response = await axios.get(`${API_URL}/revenue/not-contribution/${apartmentId}`, {
      headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
    });
    return response.data;
  } catch (error) {
    console.error("Lỗi khi lấy dữ liệu doanh thu:", error);
    return null;
  }
};
