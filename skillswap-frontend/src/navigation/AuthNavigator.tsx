import React from 'react';
import { createStackNavigator } from '@react-navigation/stack';
import { AuthStackParamList } from './navigationTypes';

// Import screens (will be created)
// import WelcomeScreen from '@screens/auth/WelcomeScreen';
// import LoginScreen from '@screens/auth/LoginScreen';
// import RegisterScreen from '@screens/auth/RegisterScreen';
// import ForgotPasswordScreen from '@screens/auth/ForgotPasswordScreen';
// import EmailVerificationScreen from '@screens/auth/EmailVerificationScreen';
// import GDPRConsentScreen from '@screens/auth/GDPRConsentScreen';

// Temporary placeholder screens
import { View, Text, StyleSheet } from 'react-native';
import { colors } from '@styles/theme';

const Stack = createStackNavigator<AuthStackParamList>();

// Placeholder screens
const WelcomeScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>Welcome to SkillSwap</Text>
    <Text style={styles.subtitle}>Connect, Learn, Grow</Text>
  </View>
);

const LoginScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>Login</Text>
    <Text style={styles.subtitle}>Sign in to your account</Text>
  </View>
);

const RegisterScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>Create Account</Text>
    <Text style={styles.subtitle}>Join the SkillSwap community</Text>
  </View>
);

const ForgotPasswordScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>Reset Password</Text>
    <Text style={styles.subtitle}>We'll help you recover your account</Text>
  </View>
);

const EmailVerificationScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>Verify Email</Text>
    <Text style={styles.subtitle}>Check your email for verification link</Text>
  </View>
);

const GDPRConsentScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>Privacy & Data</Text>
    <Text style={styles.subtitle}>We respect your privacy and comply with GDPR</Text>
  </View>
);

const AuthNavigator: React.FC = () => {
  return (
    <Stack.Navigator
      initialRouteName="Welcome"
      screenOptions={{
        headerStyle: {
          backgroundColor: colors.primary[500],
        },
        headerTintColor: colors.background.primary,
        headerTitleStyle: {
          fontWeight: '600',
          fontSize: 18,
        },
        cardStyle: {
          backgroundColor: colors.background.primary,
        },
      }}>
      <Stack.Screen 
        name="Welcome" 
        component={WelcomeScreen}
        options={{
          headerShown: false, // Welcome screen typically has no header
        }}
      />
      <Stack.Screen 
        name="Login" 
        component={LoginScreen}
        options={{
          title: 'Sign In',
          headerBackTitleVisible: false,
        }}
      />
      <Stack.Screen 
        name="Register" 
        component={RegisterScreen}
        options={{
          title: 'Create Account',
          headerBackTitleVisible: false,
        }}
      />
      <Stack.Screen 
        name="ForgotPassword" 
        component={ForgotPasswordScreen}
        options={{
          title: 'Reset Password',
          headerBackTitleVisible: false,
        }}
      />
      <Stack.Screen 
        name="EmailVerification" 
        component={EmailVerificationScreen}
        options={{
          title: 'Verify Email',
          headerBackTitleVisible: false,
          headerLeft: () => null, // Prevent going back during verification
        }}
      />
      <Stack.Screen 
        name="GDPRConsent" 
        component={GDPRConsentScreen}
        options={{
          title: 'Privacy Settings',
          headerBackTitleVisible: false,
        }}
      />
    </Stack.Navigator>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 24,
    backgroundColor: colors.background.primary,
  },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    color: colors.primary[500],
    textAlign: 'center',
    marginBottom: 8,
  },
  subtitle: {
    fontSize: 16,
    color: colors.text.secondary,
    textAlign: 'center',
    marginBottom: 32,
  },
});

export default AuthNavigator;
