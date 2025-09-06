import React, { useState } from 'react';
import {
  View,
  Text,
  ScrollView,
  StyleSheet,
  Alert,
  KeyboardAvoidingView,
  Platform,
  TouchableOpacity,
} from 'react-native';
import { useForm } from 'react-hook-form';
import { AuthStackScreenProps } from '@navigation/navigationTypes';
import { FormInput, PasswordInput, PrimaryButton, Checkbox } from '@components/forms';
import { colors, typography, spacing } from '@styles/theme';

type LoginScreenProps = AuthStackScreenProps<'Login'>;

interface LoginForm {
  email: string;
  password: string;
  rememberMe: boolean;
}

// Validation rules
const validationRules = {
  email: {
    required: 'Email is required',
    pattern: {
      value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
      message: 'Please enter a valid email address',
    },
  },
  password: {
    required: 'Password is required',
    minLength: {
      value: 1,
      message: 'Password is required',
    },
  },
};

export const LoginScreen: React.FC<LoginScreenProps> = ({ navigation }) => {
  const [isLoading, setIsLoading] = useState(false);
  const [showBiometric, setShowBiometric] = useState(false);

  const {
    control,
    handleSubmit,
    formState: { errors, isValid },
  } = useForm<LoginForm>({
    mode: 'onChange',
    defaultValues: {
      email: '',
      password: '',
      rememberMe: false,
    },
  });

  const onSubmit = async (data: LoginForm) => {
    try {
      setIsLoading(true);

      // TODO: Integrate with Backend API once available
      // For now, simulate API call
      await new Promise(resolve => setTimeout(resolve, 2000));

      console.log('Login data:', {
        email: data.email,
        rememberMe: data.rememberMe,
        // Don't log passwords in production
      });

      // Simulate successful login
      Alert.alert(
        'Success!',
        'Login successful! Welcome to SkillSwap.',
        [
          {
            text: 'OK',
            onPress: () => {
              // TODO: Navigate to main app
              // For now, just log
              console.log('Navigate to main app');
            },
          },
        ]
      );
    } catch (error) {
      console.error('Login error:', error);
      Alert.alert(
        'Login Failed',
        'Invalid email or password. Please try again.'
      );
    } finally {
      setIsLoading(false);
    }
  };

  const handleBiometricLogin = async () => {
    try {
      setIsLoading(true);

      // TODO: Implement biometric authentication
      // This would use TouchID/FaceID on iOS or Fingerprint on Android
      Alert.alert(
        'Biometric Authentication',
        'Biometric authentication would be implemented here using react-native-biometrics or similar.'
      );
    } catch (error) {
      console.error('Biometric login error:', error);
      Alert.alert(
        'Authentication Failed',
        'Biometric authentication failed. Please try again.'
      );
    } finally {
      setIsLoading(false);
    }
  };

  const handleForgotPassword = () => {
    navigation.navigate('ForgotPassword');
  };

  const handleRegisterPress = () => {
    navigation.navigate('Register');
  };

  const handleGoogleLogin = async () => {
    try {
      setIsLoading(true);

      // TODO: Implement Google OAuth
      Alert.alert(
        'Google Sign In',
        'Google OAuth integration would be implemented here using @react-native-google-signin/google-signin.'
      );
    } catch (error) {
      console.error('Google login error:', error);
      Alert.alert(
        'Login Failed',
        'Google sign in failed. Please try again.'
      );
    } finally {
      setIsLoading(false);
    }
  };

  const handleAppleLogin = async () => {
    try {
      setIsLoading(true);

      // TODO: Implement Apple Sign In (iOS only)
      Alert.alert(
        'Apple Sign In',
        'Apple Sign In would be implemented here using @invertase/react-native-apple-authentication.'
      );
    } catch (error) {
      console.error('Apple login error:', error);
      Alert.alert(
        'Login Failed',
        'Apple sign in failed. Please try again.'
      );
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <KeyboardAvoidingView
      style={styles.container}
      behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
    >
      <ScrollView
        style={styles.scrollView}
        contentContainerStyle={styles.scrollContent}
        keyboardShouldPersistTaps="handled"
        showsVerticalScrollIndicator={false}
      >
        <View style={styles.header}>
          <Text style={styles.title}>Welcome Back</Text>
          <Text style={styles.subtitle}>
            Sign in to continue your skill exchange journey
          </Text>
        </View>

        <View style={styles.form}>
          <FormInput
            name="email"
            control={control}
            label="Email Address"
            placeholder="Enter your email"
            keyboardType="email-address"
            autoCapitalize="none"
            autoCorrect={false}
            required
          />

          <PasswordInput
            name="password"
            control={control}
            label="Password"
            placeholder="Enter your password"
            required
          />

          <View style={styles.formOptions}>
            <Checkbox
              name="rememberMe"
              control={control}
              label="Remember me"
            />

            <TouchableOpacity onPress={handleForgotPassword}>
              <Text style={styles.forgotPasswordText}>Forgot Password?</Text>
            </TouchableOpacity>
          </View>

          <PrimaryButton
            title="Sign In"
            onPress={handleSubmit(onSubmit)}
            loading={isLoading}
            disabled={!isValid || isLoading}
            style={styles.submitButton}
          />

          {/* Biometric Login Option */}
          {showBiometric && (
            <PrimaryButton
              title="Use Biometric Authentication"
              onPress={handleBiometricLogin}
              variant="outline"
              loading={isLoading}
              disabled={isLoading}
              style={styles.biometricButton}
            />
          )}
        </View>

        {/* Social Login Options */}
        <View style={styles.socialLogin}>
          <View style={styles.divider}>
            <View style={styles.dividerLine} />
            <Text style={styles.dividerText}>Or continue with</Text>
            <View style={styles.dividerLine} />
          </View>

          <View style={styles.socialButtons}>
            <PrimaryButton
              title="Google"
              onPress={handleGoogleLogin}
              variant="outline"
              style={styles.socialButton}
              disabled={isLoading}
            />
            {Platform.OS === 'ios' && (
              <PrimaryButton
                title="Apple"
                onPress={handleAppleLogin}
                variant="outline"
                style={styles.socialButton}
                disabled={isLoading}
              />
            )}
          </View>
        </View>

        <View style={styles.footer}>
          <Text style={styles.footerText}>Don't have an account?</Text>
          <PrimaryButton
            title="Create Account"
            onPress={handleRegisterPress}
            variant="outline"
            size="small"
            style={styles.registerButton}
          />
        </View>
      </ScrollView>
    </KeyboardAvoidingView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background.primary,
  },
  scrollView: {
    flex: 1,
  },
  scrollContent: {
    flexGrow: 1,
    paddingHorizontal: spacing.lg,
    paddingVertical: spacing.xl,
  },
  header: {
    alignItems: 'center',
    marginBottom: spacing.xl,
  },
  title: {
    ...typography.h1,
    color: colors.text.primary,
    marginBottom: spacing.sm,
    textAlign: 'center',
  },
  subtitle: {
    ...typography.body1,
    color: colors.text.secondary,
    textAlign: 'center',
    lineHeight: 24,
  },
  form: {
    flex: 1,
  },
  formOptions: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: spacing.lg,
  },
  forgotPasswordText: {
    ...typography.body2,
    color: colors.primary[500],
    fontWeight: '500',
  },
  submitButton: {
    marginBottom: spacing.md,
  },
  biometricButton: {
    marginBottom: spacing.lg,
  },
  socialLogin: {
    marginVertical: spacing.lg,
  },
  divider: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: spacing.lg,
  },
  dividerLine: {
    flex: 1,
    height: 1,
    backgroundColor: colors.border.primary,
  },
  dividerText: {
    ...typography.caption,
    color: colors.text.secondary,
    marginHorizontal: spacing.md,
  },
  socialButtons: {
    flexDirection: 'row',
    gap: spacing.md,
  },
  socialButton: {
    flex: 1,
  },
  footer: {
    alignItems: 'center',
    marginTop: spacing.xl,
    paddingTop: spacing.lg,
    borderTopWidth: 1,
    borderTopColor: colors.border.primary,
  },
  footerText: {
    ...typography.body2,
    color: colors.text.secondary,
    marginBottom: spacing.md,
  },
  registerButton: {
    minWidth: 140,
  },
});

export default LoginScreen;
