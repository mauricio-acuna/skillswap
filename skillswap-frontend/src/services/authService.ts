import AsyncStorage from '@react-native-async-storage/async-storage';
import { MMKV } from 'react-native-mmkv';

// Secure storage using MMKV for sensitive data
const secureStorage = new MMKV({
  id: 'user-secure-storage',
  encryptionKey: 'skillswap-encryption-key-2025', // In production, use dynamic key
});

// API Configuration with security headers
const API_BASE_URL = __DEV__ 
  ? 'http://localhost:3000/api'
  : 'https://api.skillswap.com/api';

// Security configuration
const SECURITY_CONFIG = {
  MAX_LOGIN_ATTEMPTS: 5,
  TOKEN_REFRESH_THRESHOLD: 5 * 60 * 1000, // 5 minutes before expiry
  SESSION_TIMEOUT: 30 * 60 * 1000, // 30 minutes
  BIOMETRIC_TIMEOUT: 5 * 60 * 1000, // 5 minutes
};

interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  deviceInfo?: {
    deviceId: string;
    platform: string;
    version: string;
  };
}

interface LoginRequest {
  email: string;
  password: string;
  deviceInfo?: {
    deviceId: string;
    platform: string;
    version: string;
  };
  biometricHash?: string;
  rememberMe?: boolean;
}

interface AuthResponse {
  success: boolean;
  data: {
    user: {
      id: number;
      email: string;
      firstName: string;
      lastName: string;
      emailVerified: boolean;
      twoFactorEnabled: boolean;
    };
    token: string;
    refreshToken: string;
    expiresIn: number;
  };
  message: string;
}

interface ApiError {
  success: false;
  error: string;
  code: string;
  details?: any;
}

class AuthService {
  private failedAttempts: Map<string, number> = new Map();
  private lastActivity: number = Date.now();

  // Security: Input sanitization and validation
  private sanitizeInput(input: string): string {
    return input.trim().replace(/[<>\"']/g, '');
  }

  // Security: Rate limiting for login attempts
  private checkRateLimit(email: string): boolean {
    const attempts = this.failedAttempts.get(email) || 0;
    if (attempts >= SECURITY_CONFIG.MAX_LOGIN_ATTEMPTS) {
      throw new Error('Too many failed attempts. Please try again later.');
    }
    return true;
  }

  // Security: Device fingerprinting
  private async getDeviceInfo() {
    // Implementation would include device-specific identifiers
    return {
      deviceId: 'device-uuid', // Use react-native-device-info
      platform: 'ios', // Platform.OS
      version: '1.0.0', // App version
    };
  }

  // Security: Secure API request with proper headers
  private async secureRequest<T>(
    endpoint: string,
    options: RequestInit = {}
  ): Promise<T> {
    const token = this.getStoredToken();
    
    const defaultHeaders = {
      'Content-Type': 'application/json',
      'X-Requested-With': 'XMLHttpRequest',
      'X-Client-Version': '1.0.0',
      'X-Platform': 'mobile',
      ...(token && { 'Authorization': `Bearer ${token}` }),
    };

    const requestOptions: RequestInit = {
      ...options,
      headers: {
        ...defaultHeaders,
        ...options.headers,
      },
    };

    // Security: Request timeout
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 10000);

    try {
      const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        ...requestOptions,
        signal: controller.signal,
      });

      clearTimeout(timeoutId);

      // Security: Check for suspicious responses
      if (response.status === 429) {
        throw new Error('Rate limit exceeded. Please slow down.');
      }

      if (!response.ok) {
        const errorData: ApiError = await response.json();
        throw new Error(errorData.error || 'Request failed');
      }

      return await response.json();
    } catch (error: any) {
      clearTimeout(timeoutId);
      if (error.name === 'AbortError') {
        throw new Error('Request timeout');
      }
      throw error;
    }
  }

  // Security: Secure token storage
  private storeTokenSecurely(token: string, refreshToken: string, expiresIn: number): void {
    const expiryTime = Date.now() + (expiresIn * 1000);
    
    // Store tokens in encrypted storage
    secureStorage.set('access_token', token);
    secureStorage.set('refresh_token', refreshToken);
    secureStorage.set('token_expiry', expiryTime.toString());
    
    // Update last activity
    this.lastActivity = Date.now();
  }

  // Security: Secure token retrieval with validation
  private getStoredToken(): string | null {
    try {
      const token = secureStorage.getString('access_token');
      const expiry = secureStorage.getString('token_expiry');
      
      if (!token || !expiry) return null;
      
      const expiryTime = parseInt(expiry);
      const now = Date.now();
      
      // Security: Check token expiry
      if (now >= expiryTime) {
        this.clearStoredTokens();
        return null;
      }
      
      // Security: Check session timeout
      if (now - this.lastActivity > SECURITY_CONFIG.SESSION_TIMEOUT) {
        this.clearStoredTokens();
        return null;
      }
      
      return token;
    } catch (error: any) {
      console.error('Token retrieval error:', error);
      return null;
    }
  }

  // Security: Clear all stored authentication data
  private clearStoredTokens(): void {
    secureStorage.delete('access_token');
    secureStorage.delete('refresh_token');
    secureStorage.delete('token_expiry');
    secureStorage.delete('biometric_enabled');
    secureStorage.delete('user_data');
  }

  // Security: Token refresh with validation
  async refreshToken(): Promise<string | null> {
    try {
      const refreshToken = secureStorage.getString('refresh_token');
      if (!refreshToken) return null;

      const response = await this.secureRequest<AuthResponse>('/auth/refresh', {
        method: 'POST',
        body: JSON.stringify({ refreshToken }),
      });

      if (response.success) {
        this.storeTokenSecurely(
          response.data.token,
          response.data.refreshToken,
          response.data.expiresIn
        );
        return response.data.token;
      }

      return null;
    } catch (error: any) {
      this.clearStoredTokens();
      return null;
    }
  }

  // Main authentication methods
  async register(data: RegisterRequest): Promise<AuthResponse> {
    try {
      // Security: Input sanitization
      const sanitizedData = {
        ...data,
        email: this.sanitizeInput(data.email.toLowerCase()),
        firstName: this.sanitizeInput(data.firstName),
        lastName: this.sanitizeInput(data.lastName),
        deviceInfo: await this.getDeviceInfo(),
      };

      const response = await this.secureRequest<AuthResponse>('/auth/register', {
        method: 'POST',
        body: JSON.stringify(sanitizedData),
      });

      if (response.success) {
        this.storeTokenSecurely(
          response.data.token,
          response.data.refreshToken,
          response.data.expiresIn
        );
      }

      return response;
    } catch (error: any) {
      throw new Error(`Registration failed: ${error.message}`);
    }
  }

  async login(data: LoginRequest): Promise<AuthResponse> {
    try {
      // Security: Rate limiting
      this.checkRateLimit(data.email);

      // Security: Input sanitization
      const sanitizedData = {
        ...data,
        email: this.sanitizeInput(data.email.toLowerCase()),
        deviceInfo: await this.getDeviceInfo(),
      };

      const response = await this.secureRequest<AuthResponse>('/auth/login', {
        method: 'POST',
        body: JSON.stringify(sanitizedData),
      });

      if (response.success) {
        // Reset failed attempts on successful login
        this.failedAttempts.delete(data.email);
        
        this.storeTokenSecurely(
          response.data.token,
          response.data.refreshToken,
          response.data.expiresIn
        );

        // Store user data securely
        secureStorage.set('user_data', JSON.stringify(response.data.user));
      } else {
        // Increment failed attempts
        const current = this.failedAttempts.get(data.email) || 0;
        this.failedAttempts.set(data.email, current + 1);
      }

      return response;
    } catch (error: any) {
      // Increment failed attempts on error
      const current = this.failedAttempts.get(data.email) || 0;
      this.failedAttempts.set(data.email, current + 1);
      
      throw new Error(`Login failed: ${error.message}`);
    }
  }

  async logout(): Promise<void> {
    try {
      const token = this.getStoredToken();
      if (token) {
        // Notify server of logout
        await this.secureRequest('/auth/logout', {
          method: 'POST',
        });
      }
    } catch (error: any) {
      console.error('Logout error:', error);
    } finally {
      // Always clear local storage
      this.clearStoredTokens();
      this.failedAttempts.clear();
    }
  }

  async requestPasswordReset(email: string): Promise<boolean> {
    try {
      const sanitizedEmail = this.sanitizeInput(email.toLowerCase());
      
      const response = await this.secureRequest<{ success: boolean }>('/auth/forgot-password', {
        method: 'POST',
        body: JSON.stringify({ email: sanitizedEmail }),
      });

      return response.success;
    } catch (error: any) {
      throw new Error(`Password reset failed: ${error.message}`);
    }
  }

  // Security: Check authentication status
  isAuthenticated(): boolean {
    const token = this.getStoredToken();
    return token !== null;
  }

  // Security: Get current user with validation
  getCurrentUser(): any | null {
    try {
      if (!this.isAuthenticated()) return null;
      
      const userData = secureStorage.getString('user_data');
      return userData ? JSON.parse(userData) : null;
    } catch (error: any) {
      console.error('Get user error:', error);
      return null;
    }
  }

  // Security: Validate session and refresh if needed
  async validateSession(): Promise<boolean> {
    const token = this.getStoredToken();
    if (!token) return false;

    const expiry = secureStorage.getString('token_expiry');
    if (!expiry) return false;

    const expiryTime = parseInt(expiry);
    const now = Date.now();

    // If token expires within threshold, refresh it
    if (expiryTime - now < SECURITY_CONFIG.TOKEN_REFRESH_THRESHOLD) {
      const newToken = await this.refreshToken();
      return newToken !== null;
    }

    return true;
  }
}

export const authService = new AuthService();
export { SECURITY_CONFIG };
export type { RegisterRequest, LoginRequest, AuthResponse };
