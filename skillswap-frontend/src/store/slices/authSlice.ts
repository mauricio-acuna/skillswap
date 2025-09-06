import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { AuthState, User, LoginResponse } from '../../types/api/authTypes';

const initialState: AuthState = {
  isAuthenticated: false,
  accessToken: null,
  refreshToken: null,
  user: null,
  isLoading: false,
  error: null,
  biometricEnabled: false,
  isFirstTimeUser: true,
};

export const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    // Login actions
    loginStart: (state) => {
      state.isLoading = true;
      state.error = null;
    },
    loginSuccess: (state, action: PayloadAction<LoginResponse>) => {
      state.isAuthenticated = true;
      state.accessToken = action.payload.accessToken;
      state.refreshToken = action.payload.refreshToken;
      state.user = action.payload.user;
      state.isLoading = false;
      state.error = null;
      state.isFirstTimeUser = false;
    },
    loginFailure: (state, action: PayloadAction<string>) => {
      state.isLoading = false;
      state.error = action.payload;
      state.isAuthenticated = false;
    },

    // Register actions
    registerStart: (state) => {
      state.isLoading = true;
      state.error = null;
    },
    registerSuccess: (state) => {
      state.isLoading = false;
      state.error = null;
    },
    registerFailure: (state, action: PayloadAction<string>) => {
      state.isLoading = false;
      state.error = action.payload;
    },

    // Logout
    logout: (state) => {
      state.isAuthenticated = false;
      state.accessToken = null;
      state.refreshToken = null;
      state.user = null;
      state.error = null;
      state.biometricEnabled = false;
    },

    // Token refresh
    refreshTokenSuccess: (state, action: PayloadAction<{accessToken: string}>) => {
      state.accessToken = action.payload.accessToken;
    },

    // User update
    updateUser: (state, action: PayloadAction<Partial<User>>) => {
      if (state.user) {
        state.user = { ...state.user, ...action.payload };
      }
    },

    // Biometric settings
    setBiometricEnabled: (state, action: PayloadAction<boolean>) => {
      state.biometricEnabled = action.payload;
    },

    // Clear error
    clearError: (state) => {
      state.error = null;
    },

    // Set first time user
    setFirstTimeUser: (state, action: PayloadAction<boolean>) => {
      state.isFirstTimeUser = action.payload;
    },
  },
});

export const {
  loginStart,
  loginSuccess,
  loginFailure,
  registerStart,
  registerSuccess,
  registerFailure,
  logout,
  refreshTokenSuccess,
  updateUser,
  setBiometricEnabled,
  clearError,
  setFirstTimeUser,
} = authSlice.actions;

export default authSlice.reducer;
