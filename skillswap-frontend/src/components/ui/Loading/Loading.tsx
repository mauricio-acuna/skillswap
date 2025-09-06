import React from 'react';
import {
  View,
  Text,
  ActivityIndicator,
  StyleSheet,
  ViewStyle,
  Modal,
} from 'react-native';
import { colors, typography, spacing } from '@styles/theme';

interface LoadingProps {
  visible?: boolean;
  message?: string;
  size?: 'small' | 'large';
  color?: string;
  overlay?: boolean;
  style?: ViewStyle;
  testID?: string;
}

export const Loading: React.FC<LoadingProps> = ({
  visible = true,
  message,
  size = 'large',
  color = colors.primary[500],
  overlay = false,
  style,
  testID,
}) => {
  if (!visible) {
    return null;
  }

  const renderLoadingContent = () => (
    <View style={[styles.container, !overlay && styles.inline, style]}>
      <View style={styles.content}>
        <ActivityIndicator
          size={size}
          color={color}
          testID={`${testID}-indicator`}
        />
        {message && (
          <Text style={styles.message} testID={`${testID}-message`}>
            {message}
          </Text>
        )}
      </View>
    </View>
  );

  if (overlay) {
    return (
      <Modal
        transparent
        visible={visible}
        animationType="fade"
        testID={testID}
      >
        <View style={styles.overlay}>
          {renderLoadingContent()}
        </View>
      </Modal>
    );
  }

  return renderLoadingContent();
};

// Specific loading components for common use cases
export const LoadingButton: React.FC<{ loading?: boolean; color?: string }> = ({
  loading = false,
  color = colors.background.primary,
}) => {
  if (!loading) return null;
  
  return (
    <ActivityIndicator
      size="small"
      color={color}
      style={styles.buttonLoader}
    />
  );
};

export const LoadingDots: React.FC<{ 
  visible?: boolean; 
  color?: string;
  style?: ViewStyle; 
}> = ({
  visible = true,
  color = colors.primary[500],
  style,
}) => {
  if (!visible) return null;

  return (
    <View style={[styles.dotsContainer, style]}>
      {[0, 1, 2].map((index) => (
        <View
          key={index}
          style={[
            styles.dot,
            { backgroundColor: color },
            { animationDelay: `${index * 0.2}s` } as any, // Animation would need native implementation
          ]}
        />
      ))}
    </View>
  );
};

export const LoadingSkeleton: React.FC<{
  width?: number | string;
  height?: number;
  borderRadius?: number;
  style?: ViewStyle;
}> = ({
  width = '100%',
  height = 20,
  borderRadius = 4,
  style,
}) => {
  return (
    <View
      style={[
        styles.skeleton,
        {
          width,
          height,
          borderRadius,
        },
        style,
      ]}
    />
  );
};

const styles = StyleSheet.create({
  container: {
    justifyContent: 'center',
    alignItems: 'center',
  },
  
  inline: {
    padding: spacing.lg,
  },

  overlay: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
    justifyContent: 'center',
    alignItems: 'center',
  },

  content: {
    backgroundColor: colors.background.primary,
    padding: spacing.xl,
    borderRadius: 12,
    alignItems: 'center',
    minWidth: 120,
    shadowColor: colors.shadow.primary,
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.25,
    shadowRadius: 8,
    elevation: 5,
  },

  message: {
    ...typography.body1,
    color: colors.text.primary,
    marginTop: spacing.md,
    textAlign: 'center',
  },

  buttonLoader: {
    marginHorizontal: spacing.sm,
  },

  // Loading dots
  dotsContainer: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    paddingVertical: spacing.md,
  },

  dot: {
    width: 8,
    height: 8,
    borderRadius: 4,
    marginHorizontal: 2,
    // Animation would be implemented with react-native-reanimated
  },

  // Skeleton loading
  skeleton: {
    backgroundColor: colors.border.secondary,
    // Shimmer effect would be implemented with react-native-shimmer or similar
  },
});

export default Loading;
