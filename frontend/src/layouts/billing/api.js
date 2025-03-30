import axios from "axios";

const API_URL = "http://localhost:7070"; // URL

// lấy danh sách hóa đơn
export const getAllInvoices = async () => {
  const response = await axios.get(`${API_URL}/revenues`);
  return response.data;
};

// lấy hóa đơn theo apartmentId
export const getRevenue = async (id = null) => {
  try {
    const response = await axios.get(`${API_URL}/revenue/${id}`);
    return response.data;
  } catch (error) {
    console.error("Lỗi khi lấy dữ liệu doanh thu:", error);
    return null;
  }
};

// lấy số đơn vị trên 1 đơn theo type
export const getFeeByType = async (type = null) => {
  try {
    const response = await axios.get(`${API_URL}/fee/${type}`);
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
    const response = await axios.put(`${API_URL}/apartment/${apartmentId}/total`);
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
    const response = await axios.put(`${API_URL}/revenue/${id}`, revenueDTO);
    return response.data; // Dữ liệu trả về là đối tượng Revenue đã được cập nhật
  } catch (error) {
    console.error("Lỗi khi cập nhật doanh thu:", error);
    return null;
  }
};

// lấy hóa đơn theo apartmentID và type

export const getRevenueByApartmentAndType = async (apartmentId = null, type = null) => {
  try {
    const response = await axios.get(`${API_URL}/revenue`, {
      params: apartmentId && type ? { apartmentId, type } : {}, // ID vaf type
    });
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
  const response = await axios.post(`${API_URL}/revenue`, revenueDTO);
  return response.data;
};

// Xóa hóa đơn theo id
export const deleteRevenue = async (id) => {
  try {
    // Gửi yêu cầu DELETE đến server với id trong query string
    const response = await axios.delete(`${API_URL}/deleterevenue`, {
      params: { id }, // Truyền id qua query string
    });
    return response.data; // Dữ liệu trả về là thông điệp thành công
  } catch (error) {
    console.error("Lỗi khi xóa doanh thu:", error);
    return null;
  }
};
