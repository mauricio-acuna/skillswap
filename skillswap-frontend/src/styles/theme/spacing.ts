export const spacing = {
  xs: 4,
  sm: 8,
  md: 16,
  lg: 24,
  xl: 32,
  xxl: 48,
  xxxl: 64
};

export const borderRadius = {
  none: 0,
  sm: 4,
  md: 8,
  lg: 12,
  xl: 16,
  round: 50
};

export const dimensions = {
  // Screen breakpoints
  mobileS: 320,
  mobileM: 375,
  mobileL: 425,
  tablet: 768,
  
  // Component dimensions
  headerHeight: 56,
  tabBarHeight: 60,
  buttonHeight: {
    small: 36,
    medium: 44,
    large: 52
  },
  inputHeight: 48,
  cardMinHeight: 120,
  avatarSize: {
    small: 32,
    medium: 48,
    large: 64,
    xl: 96
  },
  iconSize: {
    small: 16,
    medium: 24,
    large: 32,
    xl: 48
  }
};

export type Spacing = typeof spacing;
export type BorderRadius = typeof borderRadius;
export type Dimensions = typeof dimensions;
