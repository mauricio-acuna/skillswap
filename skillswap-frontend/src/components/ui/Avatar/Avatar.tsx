import React from 'react';
import {
  View,
  Text,
  Image,
  StyleSheet,
  ViewStyle,
  ImageSourcePropType,
  TouchableOpacity,
} from 'react-native';
import { colors, typography } from '@styles/theme';

interface AvatarProps {
  source?: ImageSourcePropType;
  name?: string;
  size?: 'xs' | 'small' | 'medium' | 'large' | 'xl';
  variant?: 'circular' | 'rounded' | 'square';
  backgroundColor?: string;
  textColor?: string;
  onPress?: () => void;
  style?: ViewStyle;
  testID?: string;
  badge?: React.ReactNode;
  badgePosition?: 'top-right' | 'bottom-right' | 'top-left' | 'bottom-left';
}

export const Avatar: React.FC<AvatarProps> = ({
  source,
  name,
  size = 'medium',
  variant = 'circular',
  backgroundColor,
  textColor,
  onPress,
  style,
  testID,
  badge,
  badgePosition = 'top-right',
}) => {
  const sizeStyles = styles[size];
  const variantStyles = styles[variant];

  const containerStyles = [
    styles.container,
    sizeStyles,
    variantStyles,
    backgroundColor && { backgroundColor },
    style,
  ];

  const getInitials = (name: string): string => {
    return name
      .split(' ')
      .map(word => word.charAt(0))
      .join('')
      .substring(0, 2)
      .toUpperCase();
  };

  const getBackgroundColor = (): string => {
    if (backgroundColor) return backgroundColor;
    if (!name) return colors.primary[100];
    
    // Generate a consistent color based on the name
    const colors_list = [
      colors.primary[100],
      colors.secondary[100],
      '#FFE0B2', // Orange 100
      '#E8F5E8', // Green 100
      '#F3E5F5', // Purple 100
      '#E1F5FE', // Light Blue 100
      '#FFF3E0', // Deep Orange 100
      '#F1F8E9', // Light Green 100
    ];
    
    const hash = name.split('').reduce((acc, char) => acc + char.charCodeAt(0), 0);
    return colors_list[hash % colors_list.length];
  };

  const renderContent = () => {
    if (source) {
      return (
        <Image
          source={source}
          style={[sizeStyles, variantStyles]}
          testID={`${testID}-image`}
        />
      );
    }

    if (name) {
      const initials = getInitials(name);
      const bgColor = getBackgroundColor();
      
      return (
        <View style={[styles.initialsContainer, { backgroundColor: bgColor }]}>
          <Text 
            style={[
              styles.initialsText,
              styles[`${size}Text`],
              textColor && { color: textColor }
            ]}
          >
            {initials}
          </Text>
        </View>
      );
    }

    // Default placeholder
    return (
      <View style={[styles.placeholder, { backgroundColor: colors.border.primary }]}>
        <Text style={[styles.placeholderText, styles[`${size}Text`]]}>?</Text>
      </View>
    );
  };

  const renderBadge = () => {
    if (!badge) return null;

    return (
      <View style={[styles.badge, styles[`badge${capitalize(badgePosition)}`]]}>
        {badge}
      </View>
    );
  };

  const AvatarComponent = onPress ? TouchableOpacity : View;

  return (
    <AvatarComponent
      style={containerStyles}
      onPress={onPress}
      activeOpacity={onPress ? 0.7 : 1}
      testID={testID}
      accessibilityRole={onPress ? 'button' : 'image'}
      accessibilityLabel={name || 'User avatar'}
    >
      {renderContent()}
      {renderBadge()}
    </AvatarComponent>
  );
};

// Helper function
const capitalize = (str: string): string => {
  return str
    .split('-')
    .map(word => word.charAt(0).toUpperCase() + word.slice(1))
    .join('');
};

const styles = StyleSheet.create({
  container: {
    position: 'relative',
    overflow: 'hidden',
  },

  // Sizes
  xs: {
    width: 24,
    height: 24,
  },
  small: {
    width: 32,
    height: 32,
  },
  medium: {
    width: 40,
    height: 40,
  },
  large: {
    width: 56,
    height: 56,
  },
  xl: {
    width: 72,
    height: 72,
  },

  // Variants
  circular: {
    borderRadius: 50,
  },
  rounded: {
    borderRadius: 8,
  },
  square: {
    borderRadius: 0,
  },

  // Content containers
  initialsContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  placeholder: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },

  // Text styles
  initialsText: {
    fontWeight: '600',
    color: colors.text.primary,
  },
  placeholderText: {
    fontWeight: '400',
    color: colors.text.secondary,
  },

  // Text sizes
  xsText: {
    fontSize: 10,
  },
  smallText: {
    fontSize: 12,
  },
  mediumText: {
    fontSize: 14,
  },
  largeText: {
    fontSize: 18,
  },
  xlText: {
    fontSize: 24,
  },

  // Badge positioning
  badge: {
    position: 'absolute',
    borderRadius: 10,
    minWidth: 16,
    minHeight: 16,
    justifyContent: 'center',
    alignItems: 'center',
  },
  badgeTopRight: {
    top: -2,
    right: -2,
  },
  badgeBottomRight: {
    bottom: -2,
    right: -2,
  },
  badgeTopLeft: {
    top: -2,
    left: -2,
  },
  badgeBottomLeft: {
    bottom: -2,
    left: -2,
  },
});

export default Avatar;
