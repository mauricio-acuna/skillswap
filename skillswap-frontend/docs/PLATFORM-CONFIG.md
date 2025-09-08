# Platform Configuration Guide - SkillSwap Frontend

## Overview
This document outlines the platform-specific configurations and considerations for SkillSwap React Native app on Android and iOS platforms.

## Platform Detection & Utils

### Platform Detection Service
```typescript
// src/utils/platformUtils.ts
import { Platform, Dimensions, StatusBar } from 'react-native';
import DeviceInfo from 'react-native-device-info';

export class PlatformUtils {
  static get isIOS(): boolean {
    return Platform.OS === 'ios';
  }

  static get isAndroid(): boolean {
    return Platform.OS === 'android';
  }

  static get platformVersion(): string {
    return Platform.Version.toString();
  }

  static get screenDimensions() {
    return Dimensions.get('window');
  }

  static get safeAreaInsets() {
    if (this.isIOS) {
      // iOS safe area will be handled by react-native-safe-area-context
      return { top: 44, bottom: 34, left: 0, right: 0 };
    }
    // Android status bar height
    return { 
      top: StatusBar.currentHeight || 0, 
      bottom: 0, 
      left: 0, 
      right: 0 
    };
  }

  static async getDeviceInfo() {
    return {
      brand: await DeviceInfo.getBrand(),
      model: await DeviceInfo.getModel(),
      systemVersion: await DeviceInfo.getSystemVersion(),
      buildNumber: await DeviceInfo.getBuildNumber(),
      bundleId: await DeviceInfo.getBundleId(),
      deviceId: await DeviceInfo.getDeviceId(),
      hasNotch: await DeviceInfo.hasNotch(),
      isTablet: await DeviceInfo.isTablet(),
      supportedAbis: await DeviceInfo.supportedAbis(),
    };
  }
}
```

## iOS Specific Configuration

### 1. Info.plist Requirements
```xml
<!-- ios/SkillSwap/Info.plist -->
<key>NSAppTransportSecurity</key>
<dict>
  <key>NSAllowsArbitraryLoads</key>
  <false/>
  <key>NSExceptionDomains</key>
  <dict>
    <key>skillswap-api.com</key>
    <dict>
      <key>NSExceptionAllowsInsecureHTTPLoads</key>
      <false/>
      <key>NSExceptionMinimumTLSVersion</key>
      <string>TLSv1.2</string>
      <key>NSIncludesSubdomains</key>
      <true/>
    </dict>
  </dict>
</dict>

<!-- Camera and Photo Library Access -->
<key>NSCameraUsageDescription</key>
<string>SkillSwap needs camera access to capture profile photos and skill demonstrations</string>
<key>NSPhotoLibraryUsageDescription</key>
<string>SkillSwap needs photo library access to select profile and skill images</string>

<!-- Location Services -->
<key>NSLocationWhenInUseUsageDescription</key>
<string>SkillSwap uses location to find nearby skill instructors and optimize your learning experience</string>

<!-- Microphone for Video Calls -->
<key>NSMicrophoneUsageDescription</key>
<string>SkillSwap needs microphone access for video calls during skill sessions</string>

<!-- Push Notifications -->
<key>aps-environment</key>
<string>development</string>

<!-- Background Modes -->
<key>UIBackgroundModes</key>
<array>
  <string>audio</string>
  <string>background-processing</string>
  <string>fetch</string>
  <string>remote-notification</string>
</array>

<!-- Supported Interface Orientations -->
<key>UISupportedInterfaceOrientations</key>
<array>
  <string>UIInterfaceOrientationPortrait</string>
  <string>UIInterfaceOrientationLandscapeLeft</string>
  <string>UIInterfaceOrientationLandscapeRight</string>
</array>

<!-- Status Bar Style -->
<key>UIStatusBarStyle</key>
<string>UIStatusBarStyleDefault</string>
<key>UIViewControllerBasedStatusBarAppearance</key>
<false/>
```

### 2. iOS Build Configuration
```ruby
# ios/Podfile
platform :ios, '12.4'
require_relative '../node_modules/react-native/scripts/react_native_pods'
require_relative '../node_modules/@react-native-community/cli-platform-ios/native_modules'

target 'SkillSwap' do
  config = use_native_modules!

  use_react_native!(
    :path => config[:reactNativePath],
    :hermes_enabled => true,
    :fabric_enabled => flags[:fabric_enabled],
    :flipper_configuration => FlipperConfiguration.enabled,
    :app_clip_enabled => false
  )

  # Security and Performance Pods
  pod 'RNCAsyncStorage', :path => '../node_modules/@react-native-async-storage/async-storage'
  pod 'react-native-mmkv', :path => '../node_modules/react-native-mmkv'
  pod 'RNKeychain', :path => '../node_modules/react-native-keychain'
  
  # Camera and Media
  pod 'react-native-image-picker', :path => '../node_modules/react-native-image-picker'
  pod 'RNImageCropPicker', :path => '../node_modules/react-native-image-crop-picker'
  
  # Networking and Security
  pod 'RNCNetInfo', :path => '../node_modules/@react-native-community/netinfo'
  pod 'react-native-ssl-pinning', :path => '../node_modules/react-native-ssl-pinning'
  
  # Navigation
  pod 'RNScreens', :path => '../node_modules/react-native-screens'
  pod 'RNGestureHandler', :path => '../node_modules/react-native-gesture-handler'
  
  post_install do |installer|
    react_native_post_install(installer)
    
    # Fix for Xcode 14
    installer.pods_project.targets.each do |target|
      target.build_configurations.each do |config|
        config.build_settings['IPHONEOS_DEPLOYMENT_TARGET'] = '12.4'
        config.build_settings['CODE_SIGNING_ALLOWED'] = 'NO'
      end
    end
  end
end
```

### 3. iOS Specific Styling
```typescript
// src/styles/platformStyles.ts
import { Platform, StyleSheet } from 'react-native';
import { colors, spacing } from './theme';

export const iOSStyles = StyleSheet.create({
  // Navigation Bar
  navigationHeader: {
    backgroundColor: colors.background.primary,
    shadowColor: colors.shadow.primary,
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    borderBottomWidth: 0,
  },
  
  // Card Shadows
  cardShadow: {
    shadowColor: colors.shadow.primary,
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 8,
    backgroundColor: colors.background.card,
  },
  
  // Modal Presentation
  modalStyle: {
    backgroundColor: colors.background.primary,
    borderTopLeftRadius: 20,
    borderTopRightRadius: 20,
    paddingTop: spacing.md,
  },
  
  // Text Input
  textInput: {
    fontSize: 16,
    paddingVertical: 12,
    paddingHorizontal: 16,
    borderRadius: 8,
    backgroundColor: colors.background.secondary,
    borderWidth: 1,
    borderColor: colors.border.primary,
  },
  
  // Button
  primaryButton: {
    backgroundColor: colors.primary[500],
    borderRadius: 8,
    paddingVertical: 12,
    paddingHorizontal: 24,
    shadowColor: colors.primary[500],
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.2,
    shadowRadius: 4,
  },
  
  // Safe Area
  safeArea: {
    backgroundColor: colors.background.primary,
  },
});
```

## Android Specific Configuration

### 1. AndroidManifest.xml Requirements
```xml
<!-- android/app/src/main/AndroidManifest.xml -->
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.skillswap">

    <!-- Internet and Network -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    
    <!-- Camera and Storage -->
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" 
        android:maxSdkVersion="28" />
    
    <!-- Location -->
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    
    <!-- Audio/Video for calls -->
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
    
    <!-- Notifications -->
    <uses-permission android:name="android.permission.VIBRATE" />
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
    
    <!-- Biometric Authentication -->
    <uses-permission android:name="android.permission.USE_FINGERPRINT" />
    <uses-permission android:name="android.permission.USE_BIOMETRIC" />
    
    <!-- Wake Lock for video calls -->
    <uses-permission android:name="android.permission.WAKE_LOCK" />

    <application
        android:name=".MainApplication"
        android:label="@string/app_name"
        android:icon="@mipmap/ic_launcher"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:allowBackup="false"
        android:theme="@style/AppTheme"
        android:usesCleartextTraffic="false"
        android:networkSecurityConfig="@xml/network_security_config">
        
        <activity
            android:name=".MainActivity"
            android:label="@string/app_name"
            android:configChanges="keyboard|keyboardHidden|orientation|screenSize|uiMode"
            android:launchMode="singleTask"
            android:windowSoftInputMode="adjustResize"
            android:screenOrientation="portrait"
            android:exported="true">
            
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
            
            <!-- Deep Link Support -->
            <intent-filter android:autoVerify="true">
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
                <data android:scheme="skillswap" />
            </intent-filter>
        </activity>
        
        <!-- File Provider for camera/gallery -->
        <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="${applicationId}.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths" />
        </provider>
        
        <!-- Push Notifications -->
        <service
            android:name=".messaging.MessagingService"
            android:exported="false">
            <intent-filter>
                <action android:name="com.google.firebase.MESSAGING_EVENT" />
            </intent-filter>
        </service>
        
    </application>
</manifest>
```

### 2. Network Security Config
```xml
<!-- android/app/src/main/res/xml/network_security_config.xml -->
<network-security-config>
    <domain-config cleartextTrafficPermitted="false">
        <domain includeSubdomains="true">skillswap-api.com</domain>
        <pin-set expiration="2025-12-31">
            <pin digest="SHA-256">XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX</pin>
            <pin digest="SHA-256">YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY</pin>
        </pin-set>
    </domain-config>
    
    <!-- For development only -->
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">localhost</domain>
        <domain includeSubdomains="true">10.0.2.2</domain>
        <domain includeSubdomains="true">192.168.1.0/24</domain>
    </domain-config>
</network-security-config>
```

### 3. Android Build Configuration
```gradle
// android/app/build.gradle
android {
    compileSdkVersion rootProject.ext.compileSdkVersion
    buildToolsVersion rootProject.ext.buildToolsVersion

    defaultConfig {
        applicationId "com.skillswap"
        minSdkVersion rootProject.ext.minSdkVersion
        targetSdkVersion rootProject.ext.targetSdkVersion
        versionCode 1
        versionName "1.0.0"
        multiDexEnabled true
        resConfigs "en", "es"
        
        // Proguard optimization
        proguardFiles getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
        
        // React Native Bundle
        bundleInRelease: true
        bundleInDebug: false
    }

    buildTypes {
        debug {
            debuggable true
            minifyEnabled false
            shrinkResources false
        }
        release {
            debuggable false
            minifyEnabled true
            shrinkResources true
            signingConfig signingConfigs.release
        }
    }

    packagingOptions {
        pickFirst "lib/x86/libc++_shared.so"
        pickFirst "lib/arm64-v8a/libc++_shared.so"
    }

    splits {
        abi {
            reset()
            enable true
            universalApk false
            include "arm64-v8a", "armeabi-v7a", "x86", "x86_64"
        }
    }
}

dependencies {
    implementation 'androidx.multidex:multidex:2.0.1'
    implementation 'androidx.security:security-crypto:1.1.0-alpha06'
    implementation 'androidx.biometric:biometric:1.1.0'
    implementation 'com.google.firebase:firebase-messaging:23.1.2'
    implementation 'com.google.firebase:firebase-analytics:21.2.0'
}
```

### 4. Android Specific Styling
```typescript
// src/styles/platformStyles.ts (continued)
export const androidStyles = StyleSheet.create({
  // Navigation Header
  navigationHeader: {
    backgroundColor: colors.background.primary,
    elevation: 4,
    borderBottomWidth: 0,
  },
  
  // Card Elevation
  cardElevation: {
    elevation: 2,
    backgroundColor: colors.background.card,
  },
  
  // Modal Style
  modalStyle: {
    backgroundColor: colors.background.primary,
    flex: 1,
  },
  
  // Text Input
  textInput: {
    fontSize: 16,
    paddingVertical: 12,
    paddingHorizontal: 16,
    borderRadius: 4,
    backgroundColor: colors.background.secondary,
    borderBottomWidth: 2,
    borderBottomColor: colors.primary[500],
    includeFontPadding: false,
    textAlignVertical: 'center',
  },
  
  // Button
  primaryButton: {
    backgroundColor: colors.primary[500],
    borderRadius: 4,
    paddingVertical: 14,
    paddingHorizontal: 24,
    elevation: 2,
  },
  
  // Status Bar
  statusBar: {
    backgroundColor: colors.primary[600],
  },
});
```

## Platform-Specific Component Implementations

### 1. Platform-Aware Header Component
```typescript
// src/components/platform/PlatformHeader.tsx
import React from 'react';
import { View, Text, Platform, StatusBar } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { iOSStyles, androidStyles } from '@styles/platformStyles';

interface PlatformHeaderProps {
  title: string;
  backgroundColor?: string;
}

export const PlatformHeader: React.FC<PlatformHeaderProps> = ({
  title,
  backgroundColor,
}) => {
  const styles = Platform.OS === 'ios' ? iOSStyles : androidStyles;
  
  return (
    <>
      <StatusBar
        barStyle={Platform.OS === 'ios' ? 'dark-content' : 'light-content'}
        backgroundColor={Platform.OS === 'android' ? backgroundColor : undefined}
      />
      <SafeAreaView style={[styles.navigationHeader, { backgroundColor }]}>
        <Text style={styles.headerTitle}>{title}</Text>
      </SafeAreaView>
    </>
  );
};
```

### 2. Platform-Aware Modal Component
```typescript
// src/components/platform/PlatformModal.tsx
import React from 'react';
import { Modal, Platform, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

interface PlatformModalProps {
  visible: boolean;
  onClose: () => void;
  children: React.ReactNode;
}

export const PlatformModal: React.FC<PlatformModalProps> = ({
  visible,
  onClose,
  children,
}) => {
  return (
    <Modal
      visible={visible}
      animationType={Platform.OS === 'ios' ? 'slide' : 'fade'}
      presentationStyle={Platform.OS === 'ios' ? 'pageSheet' : 'fullScreen'}
      onRequestClose={onClose}
    >
      <SafeAreaView style={{ flex: 1 }}>
        {children}
      </SafeAreaView>
    </Modal>
  );
};
```

## Performance Optimizations by Platform

### iOS Optimizations
```typescript
// src/utils/performanceOptimizations.ts
export const iOSOptimizations = {
  // Image caching
  imageCache: {
    maxSize: 100 * 1024 * 1024, // 100MB
    ttl: 7 * 24 * 60 * 60 * 1000, // 7 days
  },
  
  // FlatList optimizations
  flatListProps: {
    removeClippedSubviews: false,
    maxToRenderPerBatch: 10,
    updateCellsBatchingPeriod: 50,
    windowSize: 10,
    initialNumToRender: 8,
    getItemLayout: (data: any, index: number) => ({
      length: 120,
      offset: 120 * index,
      index,
    }),
  },
  
  // Memory management
  memoryWarningThreshold: 0.8,
};
```

### Android Optimizations
```typescript
export const androidOptimizations = {
  // Image caching
  imageCache: {
    maxSize: 50 * 1024 * 1024, // 50MB
    ttl: 5 * 24 * 60 * 60 * 1000, // 5 days
  },
  
  // FlatList optimizations
  flatListProps: {
    removeClippedSubviews: true,
    maxToRenderPerBatch: 5,
    updateCellsBatchingPeriod: 100,
    windowSize: 5,
    initialNumToRender: 5,
    disableVirtualization: false,
  },
  
  // Memory management
  memoryWarningThreshold: 0.7,
};
```

## Security Considerations by Platform

### iOS Security Features
- Keychain Services for secure storage
- App Transport Security (ATS) for HTTPS enforcement
- Touch ID / Face ID integration
- Certificate pinning for API calls
- Jailbreak detection
- Runtime Application Self-Protection (RASP)

### Android Security Features
- Android Keystore for secure storage
- Network Security Config for certificate pinning
- Fingerprint / Biometric authentication
- Root detection
- Anti-tampering measures
- ProGuard obfuscation
- SSL certificate validation

## Testing Strategy by Platform

### iOS Testing
- XCTest for unit tests
- XCUITest for integration tests
- TestFlight for beta testing
- Crashlytics for crash reporting

### Android Testing
- JUnit for unit tests
- Espresso for UI tests
- Firebase Test Lab for device testing
- Google Play Console for beta testing

## Deployment Considerations

### iOS Deployment
- App Store Connect configuration
- Certificate and provisioning profile management
- App Store review guidelines compliance
- Privacy policy requirements

### Android Deployment
- Google Play Console setup
- App signing and key management
- Play Store listing optimization
- Google Play Protect compliance

This comprehensive platform configuration ensures optimal performance, security, and user experience across both iOS and Android platforms.
