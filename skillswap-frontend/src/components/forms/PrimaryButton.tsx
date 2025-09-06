import React from 'react';
import {
  TouchableOpacity,
  Text,
  StyleSheet,
  ViewStyle,
  TextStyle,
  ActivityIndicator,
} from 'react-native';
import { colors, typography, spacing } from '@styles/theme';

interface PrimaryButtonProps {
  title: string;
  onPress: () => void;
  disabled?: boolean;
  loading?: boolean;
  variant?: 'primary' | 'secondary' | 'outline';
  size?: 'small' | 'medium' | 'large';
  style?: ViewStyle;
  textStyle?: TextStyle;
  testID?: string;
}

export const PrimaryButton: React.FC<PrimaryButtonProps> = ({
  title,
  onPress,
  disabled = false,
  loading = false,
  variant = 'primary',
  size = 'medium',
  style,
  textStyle,
  testID,
}) => {
  const isDisabled = disabled || loading;

  const getButtonStyle = (): ViewStyle => {
    const baseStyle = {
      ...styles.button,
      ...styles[`size_${size}`],
    };

    if (isDisabled) {
      return {
        ...baseStyle,
        ...styles.disabled,
      };
    }

    switch (variant) {
      case 'secondary':
        return {
          ...baseStyle,
          ...styles.secondary,
        };
      case 'outline':
        return {
          ...baseStyle,
          ...styles.outline,
        };
      default:
        return {
          ...baseStyle,
          ...styles.primary,
        };
    }
  };

  const getTextStyle = (): TextStyle => {
    const baseTextStyle = {
      ...styles.text,
      ...styles[`text_${size}`],
    };

    if (isDisabled) {
      return {
        ...baseTextStyle,
        ...styles.textDisabled,
      };
    }

    switch (variant) {
      case 'secondary':
        return {
          ...baseTextStyle,
          ...styles.textSecondary,
        };
      case 'outline':
        return {
          ...baseTextStyle,
          ...styles.textOutline,
        };
      default:
        return {
          ...baseTextStyle,
          ...styles.textPrimary,
        };
    }
  };

  return (
    <TouchableOpacity
      style={[getButtonStyle(), style]}
      onPress={onPress}
      disabled={isDisabled}
      activeOpacity={0.8}
      testID={testID}
    >
      {loading ? (
        <ActivityIndicator
          size="small"
          color={variant === 'outline' ? colors.primary[500] : colors.background.primary}
        />
      ) : (
        <Text style={[getTextStyle(), textStyle]}>{title}</Text>
      )}
    </TouchableOpacity>
  );
};

const styles = StyleSheet.create({
  button: {
    borderRadius: 8,
    justifyContent: 'center',
    alignItems: 'center',
    flexDirection: 'row',
  },
  
  // Size variants
  size_small: {
    paddingHorizontal: spacing.md,
    paddingVertical: spacing.sm,
    minHeight: 36,
  },
  size_medium: {
    paddingHorizontal: spacing.lg,
    paddingVertical: spacing.md,
    minHeight: 48,
  },
  size_large: {
    paddingHorizontal: spacing.xl,
    paddingVertical: spacing.lg,
    minHeight: 56,
  },

  // Color variants
  primary: {
    backgroundColor: colors.primary[500],
  },
  secondary: {
    backgroundColor: colors.secondary[500],
  },
  outline: {
    backgroundColor: 'transparent',
    borderWidth: 2,
    borderColor: colors.primary[500],
  },
  disabled: {
    backgroundColor: colors.neutral[300],
    borderColor: colors.neutral[300],
  },

  // Text styles
  text: {
    ...typography.button,
    textAlign: 'center',
    fontWeight: '600',
  },
  text_small: {
    fontSize: 14,
  },
  text_medium: {
    fontSize: 16,
  },
  text_large: {
    fontSize: 18,
  },
  textPrimary: {
    color: colors.background.primary,
  },
  textSecondary: {
    color: colors.background.primary,
  },
  textOutline: {
    color: colors.primary[500],
  },
  textDisabled: {
    color: colors.text.disabled,
  },
});

export default PrimaryButton;
