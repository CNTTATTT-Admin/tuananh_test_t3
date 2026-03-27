import axios from "axios";

const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "http://localhost:8080/api/v1",
  timeout: 10000,
});

// Request interceptor
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("access_token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor
axiosInstance.interceptors.response.use(
  (response) => {
    // If the response follows the { statusCode, data, message } pattern, return data
    if (response.data && response.data.statusCode && "data" in response.data) {
      return response.data.data;
    }
    return response.data;
  },
  (error) => {
    const data = error.response?.data;
    const message = data?.message || error.message || "An unexpected error occurred";
    error.message = message;
    return Promise.reject(error);
  }
);

export default axiosInstance;
