import React from 'react';
import {
  View,
  Text,
  StyleSheet,
  ViewStyle,
  TextStyle,
} from 'react-native';
import { colors, typography, spacing } from '@styles/theme';

interface BadgeProps {
  label?: string;
  count?: number;
  variant?: 'default' | 'success' | 'warning' | 'error' | 'info' | 'secondary';
  size?: 'small' | 'medium' | 'large';
  shape?: 'rounded' | 'pill' | 'square';
  showZero?: boolean;
  maxCount?: number;
  style?: ViewStyle;
  textStyle?: TextStyle;
  testID?: string;
}

export const Badge: React.FC<BadgeProps> = ({
  label,
  count,
  variant = 'default',
  size = 'medium',
  shape = 'rounded',
  showZero = false,
  maxCount = 99,
  style,
  textStyle,
  testID,
}) => {
  // Determine what to display
  const getDisplayText = (): string => {
    if (label !== undefined) {
      return label;
    }
    
    if (count !== undefined) {
      if (count === 0 && !showZero) {
        return '';
      }
      if (count > maxCount) {
        return `${maxCount}+`;
      }
      return count.toString();
    }
    
    return '';
  };

  const displayText = getDisplayText();
  
  // Don't render if no content
  if (!displayText) {
    return null;
  }

  const badgeStyles = [
    styles.base,
    styles[variant],
    styles[size],
    styles[shape],
    style,
  ];

  const textStyles = [
    styles.text,
    styles[`${variant}Text`],
    styles[`${size}Text`],
    textStyle,
  ];

  // Special handling for dot badges (count badges without text)
  const isDot = count !== undefined && count > 0 && displayText === count.toString() && count < 10;
  
  if (isDot && size === 'small') {
    return (
      <View
        style={[styles.dot, styles[variant], style]}
        testID={testID}
        accessibilityLabel={`${count} notifications`}
      />
    );
  }

  return (
    <View
      style={badgeStyles}
      testID={testID}
      accessibilityLabel={label || `${count} items`}
      accessibilityRole="text"
    >
      <Text style={textStyles} numberOfLines={1}>
        {displayText}
      </Text>
    </View>
  );
};

const styles = StyleSheet.create({
  base: {
    alignItems: 'center',
    justifyContent: 'center',
    alignSelf: 'flex-start',
  },

  // Variants
  default: {
    backgroundColor: colors.primary[500],
  },
  success: {
    backgroundColor: colors.success,
  },
  warning: {
    backgroundColor: colors.warning,
  },
  error: {
    backgroundColor: colors.error,
  },
  info: {
    backgroundColor: colors.info,
  },
  secondary: {
    backgroundColor: colors.secondary[500],
  },

  // Sizes
  small: {
    minWidth: 16,
    height: 16,
    paddingHorizontal: 4,
    borderRadius: 8,
  },
  medium: {
    minWidth: 20,
    height: 20,
    paddingHorizontal: 6,
    borderRadius: 10,
  },
  large: {
    minWidth: 24,
    height: 24,
    paddingHorizontal: 8,
    borderRadius: 12,
  },

  // Shapes
  rounded: {
    borderRadius: 6,
  },
  pill: {
    borderRadius: 50,
  },
  square: {
    borderRadius: 0,
  },

  // Text styles
  text: {
    fontWeight: '600',
    textAlign: 'center',
  },

  // Text variants
  defaultText: {
    color: colors.background.primary,
  },
  successText: {
    color: colors.background.primary,
  },
  warningText: {
    color: colors.background.primary,
  },
  errorText: {
    color: colors.background.primary,
  },
  infoText: {
    color: colors.background.primary,
  },
  secondaryText: {
    color: colors.background.primary,
  },

  // Text sizes
  smallText: {
    fontSize: 10,
    lineHeight: 12,
  },
  mediumText: {
    fontSize: 11,
    lineHeight: 14,
  },
  largeText: {
    fontSize: 12,
    lineHeight: 16,
  },

  // Dot style for small notification badges
  dot: {
    width: 8,
    height: 8,
    borderRadius: 4,
  },
});

export default Badge;
