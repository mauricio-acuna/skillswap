import { Platform, Dimensions, StatusBar } from 'react-native';
import { colors, spacing } from '@styles/theme';

/**
 * Platform-specific utilities and configurations
 * Handles differences between iOS and Android platforms
 */

export class PlatformUtils {
  /**
   * Platform detection
   */
  static get isIOS(): boolean {
    return Platform.OS === 'ios';
  }

  static get isAndroid(): boolean {
    return Platform.OS === 'android';
  }

  static get platformVersion(): string | number {
    return Platform.Version;
  }

  /**
   * Screen and device info
   */
  static get screenDimensions() {
    return Dimensions.get('window');
  }

  static get screenInfo() {
    const { width, height } = this.screenDimensions;
    return {
      width,
      height,
      isTablet: width >= 768,
      isSmallScreen: width < 375,
      aspectRatio: width / height,
    };
  }

  /**
   * Status bar and safe area
   */
  static get statusBarHeight(): number {
    if (this.isIOS) {
      const { height } = this.screenDimensions;
      // iPhone X and newer models
      if (height >= 812) {
        return 44;
      }
      return 20;
    }
    return StatusBar.currentHeight || 24;
  }

  static get safeAreaInsets() {
    const { height } = this.screenDimensions;
    
    if (this.isIOS) {
      // iPhone X and newer models with notch/dynamic island
      if (height >= 812) {
        return { 
          top: 44, 
          bottom: 34, 
          left: 0, 
          right: 0 
        };
      }
      // iPhone 8 and older
      return { 
        top: 20, 
        bottom: 0, 
        left: 0, 
        right: 0 
      };
    }
    
    // Android
    return { 
      top: this.statusBarHeight, 
      bottom: 0, 
      left: 0, 
      right: 0 
    };
  }

  /**
   * Platform-specific styling helpers
   */
  static getElevationStyle(elevation: number) {
    if (this.isIOS) {
      return {
        shadowColor: colors.shadow.primary,
        shadowOffset: { 
          width: 0, 
          height: elevation / 2 
        },
        shadowOpacity: elevation * 0.05,
        shadowRadius: elevation,
      };
    }
    
    return {
      elevation,
    };
  }

  static getCardStyle(elevated: boolean = true) {
    return {
      backgroundColor: colors.background.card,
      borderRadius: this.isIOS ? 12 : 8,
      ...(elevated ? this.getElevationStyle(this.isIOS ? 4 : 2) : {}),
    };
  }

  static getButtonStyle(variant: 'primary' | 'secondary' | 'outline' = 'primary') {
    const baseStyle = {
      borderRadius: this.isIOS ? 8 : 4,
      paddingVertical: this.isIOS ? 12 : 14,
      paddingHorizontal: spacing.lg,
    };

    switch (variant) {
      case 'primary':
        return {
          ...baseStyle,
          backgroundColor: colors.primary[500],
          ...this.getElevationStyle(this.isIOS ? 2 : 1),
        };
      case 'secondary':
        return {
          ...baseStyle,
          backgroundColor: colors.background.secondary,
          ...this.getElevationStyle(1),
        };
      case 'outline':
        return {
          ...baseStyle,
          backgroundColor: 'transparent',
          borderWidth: 1,
          borderColor: colors.primary[500],
        };
      default:
        return baseStyle;
    }
  }

  static getInputStyle() {
    return {
      fontSize: 16,
      paddingVertical: this.isIOS ? 12 : 10,
      paddingHorizontal: spacing.md,
      borderRadius: this.isIOS ? 8 : 4,
      backgroundColor: colors.background.secondary,
      borderWidth: 1,
      borderColor: colors.background.secondary,
      ...(this.isAndroid && {
        includeFontPadding: false,
        textAlignVertical: 'center' as const,
      }),
    };
  }

  static getModalStyle() {
    if (this.isIOS) {
      return {
        backgroundColor: colors.background.primary,
        borderTopLeftRadius: 20,
        borderTopRightRadius: 20,
        paddingTop: spacing.md,
      };
    }
    
    return {
      backgroundColor: colors.background.primary,
      flex: 1,
    };
  }

  /**
   * Platform-specific animation configs
   */
  static getAnimationConfig() {
    return {
      modalAnimation: this.isIOS ? 'slide' : 'fade',
      modalPresentation: this.isIOS ? 'pageSheet' : 'fullScreen',
      transitionDuration: this.isIOS ? 300 : 250,
      easing: this.isIOS ? 'ease-out' : 'ease-in-out',
    };
  }

  /**
   * Platform-specific performance configs
   */
  static getFlatListConfig() {
    if (this.isIOS) {
      return {
        removeClippedSubviews: false,
        maxToRenderPerBatch: 10,
        updateCellsBatchingPeriod: 50,
        windowSize: 10,
        initialNumToRender: 8,
        decelerationRate: 'fast' as const,
      };
    }
    
    return {
      removeClippedSubviews: true,
      maxToRenderPerBatch: 5,
      updateCellsBatchingPeriod: 100,
      windowSize: 5,
      initialNumToRender: 5,
      decelerationRate: 'normal' as const,
    };
  }

  /**
   * Platform-specific font configurations
   */
  static getFontFamily(weight: 'normal' | 'medium' | 'semibold' | 'bold' = 'normal') {
    if (this.isIOS) {
      switch (weight) {
        case 'medium':
          return 'System';
        case 'semibold':
          return 'System';
        case 'bold':
          return 'System';
        default:
          return 'System';
      }
    }
    
    // Android
    switch (weight) {
      case 'medium':
        return 'Roboto-Medium';
      case 'semibold':
        return 'Roboto-Medium';
      case 'bold':
        return 'Roboto-Bold';
      default:
        return 'Roboto-Regular';
    }
  }

  static getFontWeight(weight: 'normal' | 'medium' | 'semibold' | 'bold' = 'normal') {
    if (this.isIOS) {
      switch (weight) {
        case 'medium':
          return '500';
        case 'semibold':
          return '600';
        case 'bold':
          return '700';
        default:
          return '400';
      }
    }
    
    // Android uses font family instead of weight
    return 'normal';
  }

  /**
   * Platform-specific haptic feedback
   */
  static triggerHapticFeedback(type: 'light' | 'medium' | 'heavy' | 'success' | 'warning' | 'error' = 'light') {
    if (this.isIOS) {
      // iOS Haptic Feedback (requires react-native-haptic-feedback)
      const HapticFeedback = require('react-native-haptic-feedback').default;
      
      const options = {
        enableVibrateFallback: true,
        ignoreAndroidSystemSettings: false,
      };

      switch (type) {
        case 'light':
          HapticFeedback.trigger('impactLight', options);
          break;
        case 'medium':
          HapticFeedback.trigger('impactMedium', options);
          break;
        case 'heavy':
          HapticFeedback.trigger('impactHeavy', options);
          break;
        case 'success':
          HapticFeedback.trigger('notificationSuccess', options);
          break;
        case 'warning':
          HapticFeedback.trigger('notificationWarning', options);
          break;
        case 'error':
          HapticFeedback.trigger('notificationError', options);
          break;
      }
    } else {
      // Android vibration
      const { Vibration } = require('react-native');
      
      switch (type) {
        case 'light':
          Vibration.vibrate(10);
          break;
        case 'medium':
          Vibration.vibrate(50);
          break;
        case 'heavy':
          Vibration.vibrate(100);
          break;
        case 'success':
          Vibration.vibrate([0, 50, 50, 50]);
          break;
        case 'warning':
          Vibration.vibrate([0, 100, 50, 100]);
          break;
        case 'error':
          Vibration.vibrate([0, 200, 100, 200]);
          break;
      }
    }
  }

  /**
   * Platform-specific keyboard configs
   */
  static getKeyboardConfig() {
    return {
      keyboardAppearance: this.isIOS ? 'light' : undefined,
      keyboardShouldPersistTaps: 'handled' as const,
      enableOnAndroid: true,
      enableResetScrollToCoords: false,
      extraHeight: this.isAndroid ? 75 : 0,
      extraScrollHeight: this.isIOS ? 0 : 100,
    };
  }

  /**
   * Platform-specific navigation configurations
   */
  static getNavigationConfig() {
    return {
      headerStyle: {
        backgroundColor: colors.background.primary,
        ...this.getElevationStyle(this.isIOS ? 1 : 4),
      },
      headerTitleStyle: {
        fontFamily: this.getFontFamily('semibold'),
        fontWeight: this.getFontWeight('semibold'),
        fontSize: this.isIOS ? 17 : 20,
      },
      headerBackTitleVisible: false,
      cardStyleInterpolator: this.isIOS ? 
        require('@react-navigation/stack').CardStyleInterpolators.forHorizontalIOS :
        require('@react-navigation/stack').CardStyleInterpolators.forFadeFromBottomAndroid,
      gestureEnabled: this.isIOS,
      gestureDirection: 'horizontal' as const,
    };
  }

  /**
   * Platform-specific image handling
   */
  static getImagePickerConfig() {
    return {
      mediaType: 'photo' as const,
      includeBase64: false,
      maxHeight: 2000,
      maxWidth: 2000,
      quality: 0.8,
      ...(this.isAndroid && {
        storageOptions: {
          skipBackup: true,
          path: 'images',
        },
      }),
    };
  }

  /**
   * Platform-specific text configurations
   */
  static getTextConfig() {
    return {
      allowFontScaling: true,
      maxFontSizeMultiplier: this.isIOS ? 1.3 : 1.2,
      adjustsFontSizeToFit: this.isIOS,
      minimumFontScale: this.isIOS ? 0.8 : undefined,
      ...(this.isAndroid && {
        includeFontPadding: false,
        textBreakStrategy: 'simple' as const,
      }),
    };
  }
}

/**
 * Platform-specific constants
 */
export const PlatformConstants = {
  // Hit slop for touch targets
  hitSlop: {
    top: PlatformUtils.isIOS ? 8 : 12,
    bottom: PlatformUtils.isIOS ? 8 : 12,
    left: PlatformUtils.isIOS ? 8 : 12,
    right: PlatformUtils.isIOS ? 8 : 12,
  },
  
  // Minimum touch target size
  minTouchTarget: PlatformUtils.isIOS ? 44 : 48,
  
  // Border radius standards
  borderRadius: {
    small: PlatformUtils.isIOS ? 4 : 2,
    medium: PlatformUtils.isIOS ? 8 : 4,
    large: PlatformUtils.isIOS ? 12 : 8,
    xl: PlatformUtils.isIOS ? 16 : 12,
  },
  
  // Animation durations
  animation: {
    fast: PlatformUtils.isIOS ? 200 : 150,
    normal: PlatformUtils.isIOS ? 300 : 250,
    slow: PlatformUtils.isIOS ? 500 : 400,
  },
  
  // Z-index layers
  zIndex: {
    modal: 1000,
    overlay: 999,
    dropdown: 900,
    toast: 800,
    tooltip: 700,
  },
};

export default PlatformUtils;
