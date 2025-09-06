import React, { useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  SafeAreaView,
  Alert,
} from 'react-native';
import { useForm, Controller } from 'react-hook-form';
import { AuthStackScreenProps } from '@navigation/navigationTypes';
import { FormInput, PrimaryButton } from '@components/forms';
import { colors, typography, spacing } from '@styles/theme';

type ForgotPasswordScreenProps = AuthStackScreenProps<'ForgotPassword'>;

interface ForgotPasswordForm {
  email: string;
}

export const ForgotPasswordScreen: React.FC<ForgotPasswordScreenProps> = ({ navigation }) => {
  const [isLoading, setIsLoading] = useState(false);
  const [emailSent, setEmailSent] = useState(false);

  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<ForgotPasswordForm>({
    defaultValues: {
      email: '',
    },
  });

  const onSubmit = async (data: ForgotPasswordForm) => {
    setIsLoading(true);
    
    try {
      // TODO: Implement API call to request password reset
      console.log('Requesting password reset for:', data.email);
      
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 2000));
      
      setEmailSent(true);
      
      Alert.alert(
        'Reset Email Sent',
        'We\'ve sent you instructions to reset your password. Please check your email.',
        [
          {
            text: 'OK',
            onPress: () => navigation.navigate('Login'),
          },
        ]
      );
    } catch (error) {
      Alert.alert(
        'Error',
        'Failed to send reset email. Please try again.'
      );
    } finally {
      setIsLoading(false);
    }
  };

  const handleBackToLogin = () => {
    navigation.navigate('Login');
  };

  if (emailSent) {
    return (
      <SafeAreaView style={styles.container}>
        <View style={styles.content}>
          <View style={styles.successContainer}>
            <Text style={styles.successIcon}>📧</Text>
            <Text style={styles.successTitle}>Check Your Email</Text>
            <Text style={styles.successDescription}>
              We've sent password reset instructions to your email address.
            </Text>
            <Text style={styles.successNote}>
              Didn't receive the email? Check your spam folder or try again.
            </Text>
          </View>
          
          <PrimaryButton
            title="Back to Login"
            onPress={handleBackToLogin}
            style={styles.backButton}
          />
        </View>
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.content}>
        <View style={styles.header}>
          <Text style={styles.title}>Forgot Password?</Text>
          <Text style={styles.subtitle}>
            Don't worry! Enter your email address and we'll send you instructions to reset your password.
          </Text>
        </View>

        <View style={styles.form}>
          <Controller
            control={control}
            name="email"
            rules={{
              required: 'Email is required',
              pattern: {
                value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                message: 'Please enter a valid email address',
              },
            }}
            render={({ field: { onChange, onBlur, value } }) => (
              <FormInput
                control={control}
                name="email"
                label="Email Address"
                placeholder="Enter your email"
                keyboardType="email-address"
                autoCapitalize="none"
                autoComplete="email"
                textContentType="emailAddress"
                style={styles.input}
              />
            )}
          />

          <PrimaryButton
            title="Send Reset Instructions"
            onPress={handleSubmit(onSubmit)}
            loading={isLoading}
            disabled={isLoading}
            style={styles.submitButton}
          />
        </View>

        <View style={styles.footer}>
          <Text style={styles.footerText}>
            Remember your password?{' '}
          </Text>
          <Text 
            style={styles.footerLink}
            onPress={handleBackToLogin}
          >
            Back to Login
          </Text>
        </View>
      </View>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background.primary,
  },
  content: {
    flex: 1,
    paddingHorizontal: spacing.lg,
    paddingVertical: spacing.xl,
  },
  header: {
    marginBottom: spacing.xl * 2,
    paddingTop: spacing.xl,
  },
  title: {
    ...typography.h1,
    color: colors.text.primary,
    marginBottom: spacing.md,
    textAlign: 'center',
  },
  subtitle: {
    ...typography.body1,
    color: colors.text.secondary,
    textAlign: 'center',
    lineHeight: 24,
    paddingHorizontal: spacing.md,
  },
  form: {
    flex: 1,
  },
  input: {
    marginBottom: spacing.lg,
  },
  submitButton: {
    marginTop: spacing.lg,
  },
  footer: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    paddingVertical: spacing.lg,
  },
  footerText: {
    ...typography.body2,
    color: colors.text.secondary,
  },
  footerLink: {
    ...typography.body2,
    color: colors.primary[500],
    fontWeight: '600',
  },
  successContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingHorizontal: spacing.lg,
  },
  successIcon: {
    fontSize: 64,
    marginBottom: spacing.xl,
  },
  successTitle: {
    ...typography.h2,
    color: colors.text.primary,
    marginBottom: spacing.lg,
    textAlign: 'center',
  },
  successDescription: {
    ...typography.body1,
    color: colors.text.secondary,
    textAlign: 'center',
    lineHeight: 24,
    marginBottom: spacing.lg,
  },
  successNote: {
    ...typography.body2,
    color: colors.text.secondary,
    textAlign: 'center',
    lineHeight: 20,
    fontStyle: 'italic',
  },
  backButton: {
    marginBottom: spacing.lg,
  },
});

export default ForgotPasswordScreen;
