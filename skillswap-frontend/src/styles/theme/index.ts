import { colors, ColorPalette } from './colors';
import { typography, fontFamily, Typography, FontFamily } from './typography';
import { spacing, borderRadius, dimensions, Spacing, BorderRadius, Dimensions } from './spacing';
import { shadows, Shadows } from './shadows';

export interface Theme {
  colors: ColorPalette;
  typography: Typography;
  fontFamily: FontFamily;
  spacing: Spacing;
  borderRadius: BorderRadius;
  dimensions: Dimensions;
  shadows: Shadows;
  isDark: boolean;
}

export const lightTheme: Theme = {
  colors,
  typography,
  fontFamily,
  spacing,
  borderRadius,
  dimensions,
  shadows,
  isDark: false,
};

export const darkTheme: Theme = {
  ...lightTheme,
  colors: {
    ...colors,
    primary: colors.dark.primary,
    text: colors.dark.text,
    background: colors.dark.background,
  },
  isDark: true,
};

export {
  colors,
  typography,
  fontFamily,
  spacing,
  borderRadius,
  dimensions,
  shadows,
};

export type {
  ColorPalette,
  Typography,
  FontFamily,
  Spacing,
  BorderRadius,
  Dimensions,
  Shadows,
};
