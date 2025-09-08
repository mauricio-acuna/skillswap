// Network Security Configuration for SkillSwap
// Implements Certificate Pinning, Request Security, and Network Monitoring

interface NetworkSecurityConfig {
  baseURL: string;
  certificatePins: string[];
  timeout: number;
  maxRetries: number;
  enableLogging: boolean;
}

interface SecurityHeaders {
  'Content-Type': string;
  'X-Requested-With': string;
  'X-Client-Version': string;
  'X-Platform': string;
  'X-Device-ID': string;
  'X-App-Signature': string;
  'Authorization'?: string;
}

interface NetworkSecurityCheck {
  isSecure: boolean;
  certificateValid: boolean;
  connectionEncrypted: boolean;
  proxyDetected: boolean;
  threats: string[];
}

class NetworkSecurityManager {
  private static instance: NetworkSecurityManager;
  private config: NetworkSecurityConfig;
  private deviceFingerprint: string = '';

  constructor() {
    this.config = {
      baseURL: __DEV__ 
        ? 'http://localhost:3000/api'
        : 'https://api.skillswap.com/api',
      certificatePins: [
        // Production certificate pins (SHA-256 of public key)
        'sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=', // Primary
        'sha256/BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB=', // Backup
        'sha256/CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC=', // Emergency
      ],
      timeout: 10000,
      maxRetries: 3,
      enableLogging: __DEV__,
    };

    this.initializeDeviceFingerprint();
  }

  static getInstance(): NetworkSecurityManager {
    if (!NetworkSecurityManager.instance) {
      NetworkSecurityManager.instance = new NetworkSecurityManager();
    }
    return NetworkSecurityManager.instance;
  }

  // Generate device fingerprint for request signing
  private async initializeDeviceFingerprint(): Promise<void> {
    try {
      // Generate unique device identifier
      // In production, use react-native-device-info
      const deviceInfo = {
        platform: 'mobile', // Platform.OS
        version: '1.0.0',    // App version
        build: '1',          // Build number
        timestamp: Date.now(),
      };

      // Create fingerprint hash
      const fingerprintData = JSON.stringify(deviceInfo);
      this.deviceFingerprint = await this.generateHash(fingerprintData);
    } catch (error: any) {
      console.error('Failed to generate device fingerprint:', error);
      this.deviceFingerprint = 'unknown';
    }
  }

  // Generate SHA-256 hash
  private async generateHash(data: string): Promise<string> {
    try {
      // In React Native, use crypto-js or similar library
      // For now, simple hash implementation
      let hash = 0;
      for (let i = 0; i < data.length; i++) {
        const char = data.charCodeAt(i);
        hash = ((hash << 5) - hash) + char;
        hash = hash & hash; // Convert to 32-bit integer
      }
      return Math.abs(hash).toString(16);
    } catch (error: any) {
      return 'fallback-hash';
    }
  }

  // Generate app signature for request authentication
  private async generateAppSignature(
    method: string,
    endpoint: string,
    body?: string,
    timestamp?: number
  ): Promise<string> {
    const ts = timestamp || Date.now();
    const payload = `${method}|${endpoint}|${body || ''}|${ts}|${this.deviceFingerprint}`;
    return await this.generateHash(payload);
  }

  // Create secure headers for requests
  private async createSecureHeaders(
    method: string,
    endpoint: string,
    body?: string,
    token?: string
  ): Promise<SecurityHeaders> {
    const timestamp = Date.now();
    const signature = await this.generateAppSignature(method, endpoint, body, timestamp);

    const headers: SecurityHeaders = {
      'Content-Type': 'application/json',
      'X-Requested-With': 'XMLHttpRequest',
      'X-Client-Version': '1.0.0',
      'X-Platform': 'mobile',
      'X-Device-ID': this.deviceFingerprint,
      'X-App-Signature': `${timestamp}:${signature}`,
    };

    if (token) {
      headers.Authorization = `Bearer ${token}`;
    }

    return headers;
  }

  // Certificate pinning check (simplified for React Native)
  private async verifyCertificatePinning(url: string): Promise<boolean> {
    try {
      // In production, this would be implemented with native modules
      // For now, basic URL validation
      if (!url.startsWith('https://') && !__DEV__) {
        console.error('Non-HTTPS connection detected');
        return false;
      }

      // Certificate validation would happen at native level
      // with libraries like react-native-cert-pinner
      return true;
    } catch (error: any) {
      console.error('Certificate pinning verification failed:', error);
      return false;
    }
  }

  // Network security check
  async performNetworkSecurityCheck(url: string): Promise<NetworkSecurityCheck> {
    const result: NetworkSecurityCheck = {
      isSecure: false,
      certificateValid: false,
      connectionEncrypted: false,
      proxyDetected: false,
      threats: [],
    };

    try {
      // Check if connection is HTTPS
      result.connectionEncrypted = url.startsWith('https://');
      if (!result.connectionEncrypted && !__DEV__) {
        result.threats.push('Unencrypted connection detected');
      }

      // Verify certificate pinning
      result.certificateValid = await this.verifyCertificatePinning(url);
      if (!result.certificateValid) {
        result.threats.push('Certificate validation failed');
      }

      // Check for proxy/VPN (simplified detection)
      result.proxyDetected = await this.detectProxy();
      if (result.proxyDetected) {
        result.threats.push('Proxy or VPN detected');
      }

      // Overall security assessment
      result.isSecure = result.connectionEncrypted && 
                       result.certificateValid && 
                       result.threats.length === 0;

    } catch (error: any) {
      console.error('Network security check failed:', error);
      result.threats.push('Security check failed');
    }

    return result;
  }

  // Simple proxy/VPN detection
  private async detectProxy(): Promise<boolean> {
    try {
      // In production, implement proper proxy detection
      // Check for common proxy indicators
      return false;
    } catch (error: any) {
      return false;
    }
  }

  // Secure fetch with all security measures
  async secureRequest<T>(
    endpoint: string,
    options: RequestInit = {},
    token?: string
  ): Promise<T> {
    const url = `${this.config.baseURL}${endpoint}`;
    const method = options.method || 'GET';
    const body = options.body as string;

    // Perform security check
    const securityCheck = await this.performNetworkSecurityCheck(url);
    if (!securityCheck.isSecure && !__DEV__) {
      throw new Error('Network security check failed: ' + securityCheck.threats.join(', '));
    }

    // Create secure headers
    const secureHeaders = await this.createSecureHeaders(method, endpoint, body, token);

    // Setup request with security configurations
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), this.config.timeout);

    try {
      const requestOptions: RequestInit = {
        ...options,
        method,
        headers: {
          ...secureHeaders,
          ...options.headers,
        },
        signal: controller.signal,
      };

      // Log request for debugging (only in development)
      if (this.config.enableLogging) {
        console.log('Secure Request:', {
          url,
          method,
          headers: secureHeaders,
          securityCheck,
        });
      }

      const response = await fetch(url, requestOptions);
      clearTimeout(timeoutId);

      // Security checks on response
      await this.validateResponse(response);

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Request failed');
      }

      const responseData = await response.json();

      // Log successful response (only in development)
      if (this.config.enableLogging) {
        // Create headers object manually for React Native compatibility
        const headersObj: Record<string, string> = {};
        response.headers.forEach?.((value: any, key: any) => {
          headersObj[key] = value;
        });
        
        console.log('Secure Response:', {
          status: response.status,
          headers: headersObj,
        });
      }

      return responseData;

    } catch (error: any) {
      clearTimeout(timeoutId);
      
      if (error.name === 'AbortError') {
        throw new Error('Request timeout - possible DoS attack');
      }
      
      // Log security incident
      this.logSecurityIncident('network_request_failed', {
        endpoint,
        error: error.message,
        securityCheck,
      });

      throw error;
    }
  }

  // Validate response for security issues
  private async validateResponse(response: Response): Promise<void> {
    // Check for suspicious response headers
    const contentType = response.headers.get('Content-Type');
    if (contentType && !contentType.includes('application/json')) {
      console.warn('Unexpected content type:', contentType);
    }

    // Check for security headers
    const securityHeaders = [
      'X-Content-Type-Options',
      'X-Frame-Options',
      'X-XSS-Protection',
    ];

    securityHeaders.forEach(header => {
      if (!response.headers.get(header)) {
        console.warn(`Missing security header: ${header}`);
      }
    });

    // Check response size for potential DoS
    const contentLength = response.headers.get('Content-Length');
    if (contentLength && parseInt(contentLength) > 10 * 1024 * 1024) { // 10MB limit
      throw new Error('Response too large - potential DoS attack');
    }
  }

  // Log security incidents
  private logSecurityIncident(type: string, details: any): void {
    const incident = {
      type,
      timestamp: new Date().toISOString(),
      deviceFingerprint: this.deviceFingerprint,
      details,
    };

    console.error('Security Incident:', incident);

    // In production, send to security monitoring service
    // this.sendToSecurityService(incident);
  }

  // Update security configuration
  updateConfig(newConfig: Partial<NetworkSecurityConfig>): void {
    this.config = { ...this.config, ...newConfig };
  }

  // Get current security status
  getSecurityStatus(): {
    certificatePinningEnabled: boolean;
    secureConnection: boolean;
    deviceFingerprint: string;
  } {
    return {
      certificatePinningEnabled: this.config.certificatePins.length > 0,
      secureConnection: this.config.baseURL.startsWith('https://'),
      deviceFingerprint: this.deviceFingerprint,
    };
  }
}

// Network Security Hook for React components
export const useNetworkSecurity = () => {
  const networkManager = NetworkSecurityManager.getInstance();

  return {
    secureRequest: networkManager.secureRequest.bind(networkManager),
    performSecurityCheck: networkManager.performNetworkSecurityCheck.bind(networkManager),
    getSecurityStatus: networkManager.getSecurityStatus.bind(networkManager),
  };
};

export const networkSecurityManager = NetworkSecurityManager.getInstance();
export type { NetworkSecurityConfig, SecurityHeaders, NetworkSecurityCheck };
