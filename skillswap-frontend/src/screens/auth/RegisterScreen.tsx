import React, { useState } from 'react';
import {
  View,
  Text,
  ScrollView,
  StyleSheet,
  Alert,
  KeyboardAvoidingView,
  Platform,
} from 'react-native';
import { useForm } from 'react-hook-form';
import { AuthStackScreenProps } from '@navigation/navigationTypes';
import { FormInput, PasswordInput, PrimaryButton, Checkbox } from '@components/forms';
import { colors, typography, spacing } from '@styles/theme';

type RegisterScreenProps = AuthStackScreenProps<'Register'>;

interface RegisterForm {
  email: string;
  password: string;
  confirmPassword: string;
  firstName: string;
  lastName: string;
  acceptTerms: boolean;
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
      value: 8,
      message: 'Password must be at least 8 characters',
    },
    pattern: {
      value: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]/,
      message: 'Password must contain uppercase, lowercase, number and special character',
    },
  },
  firstName: {
    required: 'First name is required',
    minLength: {
      value: 2,
      message: 'First name must be at least 2 characters',
    },
    maxLength: {
      value: 50,
      message: 'First name cannot exceed 50 characters',
    },
  },
  lastName: {
    required: 'Last name is required',
    minLength: {
      value: 2,
      message: 'Last name must be at least 2 characters',
    },
    maxLength: {
      value: 50,
      message: 'Last name cannot exceed 50 characters',
    },
  },
  acceptTerms: {
    required: 'You must accept the terms and conditions',
  },
};

export const RegisterScreen: React.FC<RegisterScreenProps> = ({ navigation }) => {
  const [isLoading, setIsLoading] = useState(false);

  const {
    control,
    handleSubmit,
    watch,
    formState: { errors, isValid },
  } = useForm<RegisterForm>({
    mode: 'onChange',
    defaultValues: {
      email: '',
      password: '',
      confirmPassword: '',
      firstName: '',
      lastName: '',
      acceptTerms: false,
    },
  });

  const watchedPassword = watch('password');

  const onSubmit = async (data: RegisterForm) => {
    try {
      setIsLoading(true);

      // Validate password confirmation
      if (data.password !== data.confirmPassword) {
        Alert.alert('Error', 'Passwords do not match');
        return;
      }

      // TODO: Integrate with Backend API once available
      // For now, simulate API call
      await new Promise(resolve => setTimeout(resolve, 2000));

      console.log('Registration data:', {
        email: data.email,
        firstName: data.firstName,
        lastName: data.lastName,
        // Don't log passwords in production
      });

      // Show success message
      Alert.alert(
        'Success!',
        'Your account has been created successfully. Please check your email to verify your account.',
        [
          {
            text: 'OK',
            onPress: () => navigation.navigate('EmailVerification', { email: data.email }),
          },
        ]
      );
    } catch (error) {
      console.error('Registration error:', error);
      Alert.alert(
        'Registration Failed',
        'There was an error creating your account. Please try again.'
      );
    } finally {
      setIsLoading(false);
    }
  };

  const handleTermsPress = () => {
    // TODO: Navigate to Terms & Conditions screen or open modal
    Alert.alert('Terms & Conditions', 'Terms and conditions content would be displayed here.');
  };

  const handleLoginPress = () => {
    navigation.navigate('Login');
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
          <Text style={styles.title}>Create Account</Text>
          <Text style={styles.subtitle}>
            Join the SkillSwap community and start learning
          </Text>
        </View>

        <View style={styles.form}>
          <View style={styles.nameRow}>
            <View style={styles.nameField}>
              <FormInput
                name="firstName"
                control={control}
                label="First Name"
                placeholder="Enter your first name"
                rules={validationRules.firstName}
                required
                autoCapitalize="words"
                autoCorrect={false}
              />
            </View>
            <View style={styles.nameField}>
              <FormInput
                name="lastName"
                control={control}
                label="Last Name"
                placeholder="Enter your last name"
                rules={validationRules.lastName}
                required
                autoCapitalize="words"
                autoCorrect={false}
              />
            </View>
          </View>

          <FormInput
            name="email"
            control={control}
            label="Email Address"
            placeholder="Enter your email"
            rules={validationRules.email}
            required
            keyboardType="email-address"
            autoCapitalize="none"
            autoCorrect={false}
          />

          <PasswordInput
            name="password"
            control={control}
            label="Password"
            placeholder="Create a strong password"
            rules={validationRules.password}
            required
            showStrengthIndicator
          />

          <PasswordInput
            name="confirmPassword"
            control={control}
            label="Confirm Password"
            placeholder="Confirm your password"
            rules={{
              required: 'Please confirm your password',
              validate: (value: string) =>
                value === watchedPassword || 'Passwords do not match',
            }}
            required
          />

          <Checkbox
            name="acceptTerms"
            control={control}
            label="I accept the Terms & Conditions and Privacy Policy"
            rules={validationRules.acceptTerms}
            required
            onLabelPress={handleTermsPress}
          />

          <PrimaryButton
            title="Create Account"
            onPress={handleSubmit(onSubmit)}
            loading={isLoading}
            disabled={!isValid || isLoading}
            style={styles.submitButton}
          />
        </View>

        <View style={styles.footer}>
          <Text style={styles.footerText}>Already have an account?</Text>
          <PrimaryButton
            title="Sign In"
            onPress={handleLoginPress}
            variant="outline"
            size="small"
            style={styles.loginButton}
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
  nameRow: {
    flexDirection: 'row',
    gap: spacing.md,
  },
  nameField: {
    flex: 1,
  },
  submitButton: {
    marginTop: spacing.lg,
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
  loginButton: {
    minWidth: 120,
  },
});

export default RegisterScreen;
