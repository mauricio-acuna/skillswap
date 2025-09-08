import React from 'react';
import {
  View,
  StyleSheet,
  ViewStyle,
  TouchableOpacity,
} from 'react-native';
import { colors, spacing } from '@styles/theme';

export interface CardProps {
  children: React.ReactNode;
  variant?: 'default' | 'elevated' | 'outlined' | 'filled';
  padding?: 'none' | 'small' | 'medium' | 'large';
  margin?: 'none' | 'small' | 'medium' | 'large';
  onPress?: () => void;
  disabled?: boolean;
  style?: ViewStyle;
  testID?: string;
}

export const Card: React.FC<CardProps> = ({
  children,
  variant = 'default',
  padding = 'medium',
  margin = 'none',
  onPress,
  disabled = false,
  style,
  testID,
}) => {
  const paddingStyleKey = `padding${capitalize(padding)}`;
  const marginStyleKey = `margin${capitalize(margin)}`;
  
  const paddingStyle = 
    paddingStyleKey === 'paddingNone' ? styles.paddingNone :
    paddingStyleKey === 'paddingSmall' ? styles.paddingSmall :
    paddingStyleKey === 'paddingMedium' ? styles.paddingMedium :
    paddingStyleKey === 'paddingLarge' ? styles.paddingLarge :
    {};
    
  const marginStyle = 
    marginStyleKey === 'marginNone' ? styles.marginNone :
    marginStyleKey === 'marginSmall' ? styles.marginSmall :
    marginStyleKey === 'marginMedium' ? styles.marginMedium :
    marginStyleKey === 'marginLarge' ? styles.marginLarge :
    {};

  const cardStyles = StyleSheet.flatten([
    styles.base,
    styles[variant],
    paddingStyle,
    marginStyle,
    disabled ? styles.disabled : undefined,
    style,
  ]);

  return (
    <View
      style={cardStyles as any}
      testID={testID}
      accessibilityRole={onPress ? 'button' : undefined}
    >
      {onPress ? (
        <TouchableOpacity
          onPress={onPress}
          disabled={disabled}
          activeOpacity={0.7}
        >
          {children}
        </TouchableOpacity>
      ) : (
        children
      )}
    </View>
  );
};

// Helper function to capitalize strings
const capitalize = (str: string): string => {
  return str.charAt(0).toUpperCase() + str.slice(1);
};

const styles = StyleSheet.create({
  base: {
    borderRadius: 12,
    backgroundColor: colors.background.primary,
  },

  // Variants
  default: {
    // Default card with subtle background
    backgroundColor: colors.background.primary,
  },
  elevated: {
    backgroundColor: colors.background.primary,
    shadowColor: colors.shadow.primary,
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 8,
    elevation: 4,
  },
  outlined: {
    backgroundColor: colors.background.primary,
    borderWidth: 1,
    borderColor: colors.border.primary,
  },
  filled: {
    backgroundColor: colors.background.secondary,
  },

  // Padding variants
  paddingNone: {
    padding: 0,
  },
  paddingSmall: {
    padding: spacing.sm,
  },
  paddingMedium: {
    padding: spacing.md,
  },
  paddingLarge: {
    padding: spacing.lg,
  },

  // Margin variants
  marginNone: {
    margin: 0,
  },
  marginSmall: {
    margin: spacing.sm,
  },
  marginMedium: {
    margin: spacing.md,
  },
  marginLarge: {
    margin: spacing.lg,
  },

  // States
  disabled: {
    opacity: 0.5,
  },
});

export default Card;
