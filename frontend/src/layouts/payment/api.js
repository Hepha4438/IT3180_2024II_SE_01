import axios from "axios";

const API_URL = "https://it3180-2024ii-se-01-final.onrender.com"; // URL
export const fetchPaymentInfo = async () => {
  try {
    const response = await axios.get(`${API_URL}/payment/complete`, {
      headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
    });
    return response.data;
  } catch (err) {
    setError("Không thể tải thông tin thanh toán.");
  } finally {
    setLoading(false);
  }
};
