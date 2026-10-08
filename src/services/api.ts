import axios, { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse } from 'axios';

/**
 * =========================================================================
 * CAMPUS EVENT MANAGEMENT SYSTEM - CENTRAL API CONFIGURATION
 * =========================================================================
 * Connects the React TypeScript frontend directly to the Spring Boot REST API.
 * Base URL: http://localhost:8080/api
 * =========================================================================
 */

export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
  timeout: 10000,
});

// Request Interceptor: Injects Spring Boot Bearer JWT Token automatically
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('token') || localStorage.getItem('cems_jwt_token');
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response Interceptor: Handles 401 Unauthorized token expiry
apiClient.interceptors.response.use(
  (response: AxiosResponse) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      console.warn('[API Interceptor] 401 Unauthorized from Spring Boot backend. Clearing expired session.');
      localStorage.removeItem('token');
      localStorage.removeItem('cems_jwt_token');
      localStorage.removeItem('cems_current_user');
      // If unauthorized and currently in a protected route, window redirect can be triggered or handled by state
    }
    return Promise.reject(error);
  }
);

export default apiClient;

