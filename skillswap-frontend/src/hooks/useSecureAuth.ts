import { useState, useEffect, useCallback } from 'react';
import { Alert } from 'react-native';
import { useAppDispatch, useAppSelector } from '@store/hooks';
import { 
  setUser, 
  setToken, 
  clearAuth, 
  setLoading,
  setError,
  incrementFailedAttempts,
  resetFailedAttempts 
} from '@store/slices/authSlice';
import { authService, SECURITY_CONFIG } from '@services/authService';
import { securityManager } from '@utils/securityManager';
import { networkSecurityManager } from '@utils/networkSecurity';

interface UseSecureAuthReturn {
  // Auth state
  isAuthenticated: boolean;
  user: any;
  isLoading: boolean;
  error: string | null;
  failedAttempts: number;
  
  // Auth actions
  login: (email: string, password: string, rememberMe?: boolean) => Promise<boolean>;
  register: (data: RegisterData) => Promise<boolean>;
  logout: () => Promise<void>;
  forgotPassword: (email: string) => Promise<boolean>;
  validateSession: () => Promise<boolean>;
  
  // Security features
  enableBiometric: () => Promise<boolean>;
  checkSecurityStatus: () => Promise<SecurityStatus>;
  isSecurityCompromised: boolean;
}

interface RegisterData {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  acceptTerms: boolean;
}

interface SecurityStatus {
  deviceSecurity: 'low' | 'medium' | 'high' | 'critical';
  networkSecurity: boolean;
  appIntegrity: boolean;
  sessionValid: boolean;
  threats: string[];
}

export const useSecureAuth = (): UseSecureAuthReturn => {
  const dispatch = useAppDispatch();
  const authState = useAppSelector((state) => state.auth);
  
  const [isSecurityCompromised, setIsSecurityCompromised] = useState(false);
  const [securityStatus, setSecurityStatus] = useState<SecurityStatus>({
    deviceSecurity: 'low',
    networkSecurity: false,
    appIntegrity: false,
    sessionValid: false,
    threats: [],
  });

  // Initialize security monitoring
  useEffect(() => {
    initializeSecurity();
    
    // Start continuous security monitoring
    securityManager.startSecurityMonitoring();
    
    // Cleanup on unmount
    return () => {
      securityManager.stopSecurityMonitoring();
    };
  }, []);

  // Security validation on auth state changes
  useEffect(() => {
    if (authState.isAuthenticated) {
      validateSecurityStatus();
    }
  }, [authState.isAuthenticated]);

  // Initialize security checks
  const initializeSecurity = async () => {
    try {
      const securityCheck = await securityManager.performSecurityCheck(true);
      
      if (securityCheck.riskLevel === 'critical' || securityCheck.riskLevel === 'high') {
        setIsSecurityCompromised(true);
        handleSecurityViolation(securityCheck);
      }
    } catch (error) {
      console.error('Security initialization failed:', error);
    }
  };

  // Handle security violations
  const handleSecurityViolation = (violation: any) => {
    switch (violation.riskLevel) {
      case 'critical':
        Alert.alert(
          'Security Alert',
          'Critical security vulnerability detected. App access is restricted.',
          [{ text: 'Exit', onPress: () => logout() }]
        );
        break;
      
      case 'high':
        Alert.alert(
          'Security Warning',
          'Security risk detected. Some features may be limited.',
          [{ text: 'Continue', style: 'default' }]
        );
        break;
    }
  };

  // Comprehensive security status check
  const checkSecurityStatus = useCallback(async (): Promise<SecurityStatus> => {
    try {
      // Device security check
      const deviceCheck = await securityManager.performSecurityCheck();
      
      // Network security check
      const networkCheck = await networkSecurityManager.performSecurityCheck('https://api.skillswap.com');
      
      // App integrity check
      const integrityCheck = await securityManager.checkAppIntegrity();
      
      // Session validation
      const sessionValid = await authService.validateSession();

      const status: SecurityStatus = {
        deviceSecurity: deviceCheck.riskLevel,
        networkSecurity: networkCheck.isSecure,
        appIntegrity: integrityCheck,
        sessionValid,
        threats: [
          ...deviceCheck.checks.isJailbroken ? ['Device compromised'] : [],
          ...!networkCheck.isSecure ? ['Network insecure'] : [],
          ...!integrityCheck ? ['App integrity compromised'] : [],
          ...!sessionValid ? ['Session invalid'] : [],
        ],
      };

      setSecurityStatus(status);
      setIsSecurityCompromised(status.threats.length > 0);

      return status;
    } catch (error) {
      console.error('Security status check failed:', error);
      return {
        deviceSecurity: 'critical',
        networkSecurity: false,
        appIntegrity: false,
        sessionValid: false,
        threats: ['Security check failed'],
      };
    }
  }, []);

  // Secure login with comprehensive security checks
  const login = useCallback(async (
    email: string, 
    password: string, 
    rememberMe: boolean = false
  ): Promise<boolean> => {
    dispatch(setLoading(true));
    dispatch(setError(null));

    try {
      // Pre-login security checks
      const securityCheck = await checkSecurityStatus();
      
      if (securityCheck.deviceSecurity === 'critical') {
        throw new Error('Device security compromised. Login blocked.');
      }

      // Rate limiting check
      if (authState.failedAttempts >= SECURITY_CONFIG.MAX_LOGIN_ATTEMPTS) {
        throw new Error('Too many failed attempts. Please try again later.');
      }

      // Attempt login
      const response = await authService.login({
        email,
        password,
        rememberMe,
      });

      if (response.success) {
        // Reset failed attempts on success
        dispatch(resetFailedAttempts());
        
        // Update auth state
        dispatch(setUser(response.data.user));
        dispatch(setToken(response.data.token));
        
        // Log successful authentication
        console.log('Secure login successful');
        
        return true;
      } else {
        // Increment failed attempts
        dispatch(incrementFailedAttempts());
        throw new Error(response.message || 'Login failed');
      }

    } catch (error) {
      // Handle login failure
      dispatch(incrementFailedAttempts());
      
      const errorMessage = error instanceof Error ? error.message : 'Login failed';
      dispatch(setError(errorMessage));
      
      // Log security incident
      console.error('Login failed:', errorMessage);
      
      return false;
    } finally {
      dispatch(setLoading(false));
    }
  }, [dispatch, authState.failedAttempts, checkSecurityStatus]);

  // Secure registration
  const register = useCallback(async (data: RegisterData): Promise<boolean> => {
    dispatch(setLoading(true));
    dispatch(setError(null));

    try {
      // Pre-registration security checks
      const securityCheck = await checkSecurityStatus();
      
      if (securityCheck.deviceSecurity === 'critical') {
        throw new Error('Device security compromised. Registration blocked.');
      }

      // Validate terms acceptance
      if (!data.acceptTerms) {
        throw new Error('Terms and conditions must be accepted');
      }

      // Attempt registration
      const response = await authService.register({
        email: data.email,
        password: data.password,
        firstName: data.firstName,
        lastName: data.lastName,
      });

      if (response.success) {
        // Update auth state
        dispatch(setUser(response.data.user));
        dispatch(setToken(response.data.token));
        
        console.log('Secure registration successful');
        return true;
      } else {
        throw new Error(response.message || 'Registration failed');
      }

    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Registration failed';
      dispatch(setError(errorMessage));
      
      console.error('Registration failed:', errorMessage);
      return false;
    } finally {
      dispatch(setLoading(false));
    }
  }, [dispatch, checkSecurityStatus]);

  // Secure logout
  const logout = useCallback(async (): Promise<void> => {
    dispatch(setLoading(true));

    try {
      await authService.logout();
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      // Always clear local auth state
      dispatch(clearAuth());
      dispatch(setLoading(false));
      
      console.log('Secure logout completed');
    }
  }, [dispatch]);

  // Forgot password
  const forgotPassword = useCallback(async (email: string): Promise<boolean> => {
    dispatch(setLoading(true));
    dispatch(setError(null));

    try {
      const success = await authService.requestPasswordReset(email);
      
      if (!success) {
        throw new Error('Password reset request failed');
      }
      
      return true;
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Password reset failed';
      dispatch(setError(errorMessage));
      return false;
    } finally {
      dispatch(setLoading(false));
    }
  }, [dispatch]);

  // Session validation
  const validateSession = useCallback(async (): Promise<boolean> => {
    try {
      const isValid = await authService.validateSession();
      
      if (!isValid) {
        // Session expired, logout user
        await logout();
      }
      
      return isValid;
    } catch (error) {
      console.error('Session validation failed:', error);
      await logout();
      return false;
    }
  }, [logout]);

  // Validate security status periodically
  const validateSecurityStatus = useCallback(async () => {
    const status = await checkSecurityStatus();
    
    if (status.threats.length > 0) {
      console.warn('Security threats detected:', status.threats);
      
      // Handle critical threats
      if (status.deviceSecurity === 'critical' || !status.appIntegrity) {
        await logout();
      }
    }
  }, [checkSecurityStatus, logout]);

  // Biometric authentication (placeholder)
  const enableBiometric = useCallback(async (): Promise<boolean> => {
    try {
      // TODO: Implement actual biometric authentication
      // const biometricResult = await TouchID.authenticate('Enable biometric authentication');
      console.log('Biometric authentication enabled');
      return true;
    } catch (error) {
      console.error('Biometric setup failed:', error);
      return false;
    }
  }, []);

  return {
    // Auth state
    isAuthenticated: authState.isAuthenticated,
    user: authState.user,
    isLoading: authState.isLoading,
    error: authState.error,
    failedAttempts: authState.failedAttempts,
    
    // Auth actions
    login,
    register,
    logout,
    forgotPassword,
    validateSession,
    
    // Security features
    enableBiometric,
    checkSecurityStatus,
    isSecurityCompromised,
  };
};

export type { RegisterData, SecurityStatus };
