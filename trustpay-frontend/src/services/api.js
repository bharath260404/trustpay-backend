// API Service - Handles all communication with the backend
import axios from 'axios';

// Base URL for the backend API
const API_BASE_URL = 'http://localhost:8081/api/v1';

// Create an axios instance with default configuration
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000, // 10 second timeout
});

// API Service Object - Contains all our API methods
const apiService = {
  // Health Check - Test if backend is running
  checkHealth: async () => {
    try {
      const response = await apiClient.get('/health');
      return { success: true, data: response.data };
    } catch (error) {
      return { success: false, error: error.message };
    }
  },

  // URL Scanner - Check if a URL is safe
  scanUrl: async (url) => {
    try {
      const response = await apiClient.post('/url/scan', { url });
      return { success: true, data: response.data };
    } catch (error) {
      return { success: false, error: error.message };
    }
  },

  // Transaction Risk Evaluator - Assess transaction risk
  evaluateTransaction: async (transactionData) => {
    try {
      const response = await apiClient.post('/transaction/evaluate', transactionData);
      return { success: true, data: response.data };
    } catch (error) {
      return { success: false, error: error.message };
    }
  },

  // QR Code Scanner - Upload and scan QR code
  scanQRCode: async (file) => {
    try {
      const formData = new FormData();
      formData.append('file', file);
      
      const response = await apiClient.post('/qr/scan', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      return { success: true, data: response.data };
    } catch (error) {
      return { success: false, error: error.message };
    }
  },

  // OCR Screenshot Analyzer - Upload and analyze screenshot
  analyzeScreenshot: async (file) => {
    try {
      const formData = new FormData();
      formData.append('file', file);
      
      const response = await apiClient.post('/ocr/analyze', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      return { success: true, data: response.data };
    } catch (error) {
      return { success: false, error: error.message };
    }
  },

  // Scam Reporter - Submit a scam report
  reportScam: async (reportData) => {
    try {
      const response = await apiClient.post('/report', reportData);
      return { success: true, data: response.data };
    } catch (error) {
      return { success: false, error: error.message };
    }
  },

  // Unified Check - Comprehensive threat verification
  unifiedCheck: async (checkData) => {
    try {
      const response = await apiClient.post('/verify', checkData);
      return { success: true, data: response.data };
    } catch (error) {
      return { success: false, error: error.message };
    }
  },
};

// ═══════════════════════════════════════════════════════════════════
// NAMED EXPORTS
// ═══════════════════════════════════════════════════════════════════
// Export individual functions so they can be imported directly
// This allows: import { scanUrl } from './api'
// Instead of: import apiService from './api'; apiService.scanUrl()

export const checkHealth = apiService.checkHealth;
export const scanUrl = apiService.scanUrl;
export const evaluateTransaction = apiService.evaluateTransaction;
export const scanQRCode = apiService.scanQRCode;
export const analyzeScreenshot = apiService.analyzeScreenshot;
export const reportScam = apiService.reportScam;
export const unifiedCheck = apiService.unifiedCheck;

// ═══════════════════════════════════════════════════════════════════
// DEFAULT EXPORT (for backward compatibility)
// ═══════════════════════════════════════════════════════════════════
export default apiService;
