export const colors = {
  primary: {
    50: '#E3F2FD',
    100: '#BBDEFB',
    200: '#90CAF9',
    300: '#64B5F6',
    400: '#42A5F5',
    500: '#2196F3', // Main brand color
    600: '#1E88E5',
    700: '#1976D2',
    800: '#1565C0',
    900: '#0D47A1'
  },
  secondary: {
    50: '#F3E5F5',
    100: '#E1BEE7',
    200: '#CE93D8',
    300: '#BA68C8',
    400: '#AB47BC',
    500: '#9C27B0', // Secondary brand
    600: '#8E24AA',
    700: '#7B1FA2',
    800: '#6A1B9A',
    900: '#4A148C'
  },
  success: '#4CAF50',
  warning: '#FF9800',
  error: '#F44336',
  info: '#2196F3',
  white: '#FFFFFF',
  gray: {
    50: '#FAFAFA',
    100: '#F5F5F5',
    200: '#EEEEEE',
    300: '#E0E0E0',
    400: '#BDBDBD',
    500: '#9E9E9E',
    600: '#757575',
    700: '#616161',
    800: '#424242',
    900: '#212121'
  },
  neutral: {
    50: '#FAFAFA',
    100: '#F5F5F5',
    200: '#EEEEEE',
    300: '#E0E0E0',
    400: '#BDBDBD',
    500: '#9E9E9E',
    600: '#757575',
    700: '#616161',
    800: '#424242',
    900: '#212121'
  },
  text: {
    primary: '#212121',
    secondary: '#757575',
    disabled: '#BDBDBD',
    hint: '#9E9E9E',
    tertiary: '#9E9E9E'
  },
  background: {
    primary: '#FFFFFF',
    secondary: '#FAFAFA',
    card: '#FFFFFF',
    disabled: '#F5F5F5',
    tertiary: '#F5F5F5'
  },
  surface: {
    primary: '#FFFFFF',
    secondary: '#F8F9FA',
    accent: '#F3E5F5'
  },
  border: {
    primary: '#E0E0E0',
    secondary: '#EEEEEE',
    accent: '#2196F3'
  },
  shadow: {
    primary: '#000000',
    secondary: '#757575'
  },
  // Dark mode colors
  dark: {
    primary: {
      50: '#E3F2FD',
      100: '#BBDEFB',
      200: '#90CAF9',
      300: '#64B5F6',
      400: '#42A5F5',
      500: '#2196F3',
      600: '#1E88E5',
      700: '#1976D2',
      800: '#1565C0',
      900: '#0D47A1'
    },
    text: {
      primary: '#FFFFFF',
      secondary: '#BDBDBD',
      disabled: '#616161',
      hint: '#757575',
      tertiary: '#757575'
    },
    background: {
      primary: '#121212',
      secondary: '#1E1E1E',
      card: '#2D2D2D',
      disabled: '#2D2D2D',
      tertiary: '#2D2D2D'
    }
  }
};

export type ColorPalette = typeof colors;
