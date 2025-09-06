import React, { useState } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  ViewStyle,
  TextStyle,
} from 'react-native';
import { Control, Controller, FieldPath, FieldValues } from 'react-hook-form';
import { colors, typography, spacing } from '@styles/theme';

interface PasswordInputProps<
  TFieldValues extends FieldValues = FieldValues,
  TName extends FieldPath<TFieldValues> = FieldPath<TFieldValues>
> {
  name: TName;
  control: Control<TFieldValues>;
  label?: string;
  error?: string;
  required?: boolean;
  placeholder?: string;
  showStrengthIndicator?: boolean;
  containerStyle?: ViewStyle;
  labelStyle?: TextStyle;
  inputStyle?: TextStyle;
  errorStyle?: TextStyle;
}

// Password strength calculation
const getPasswordStrength = (password: string): {
  score: number;
  label: string;
  color: string;
} => {
  let score = 0;
  if (!password) return { score: 0, label: '', color: colors.gray[300] };

  // Length check
  if (password.length >= 8) score++;
  if (password.length >= 12) score++;

  // Character variety checks
  if (/[a-z]/.test(password)) score++;
  if (/[A-Z]/.test(password)) score++;
  if (/[0-9]/.test(password)) score++;
  if (/[^a-zA-Z0-9]/.test(password)) score++;

  // Return strength assessment
  if (score <= 2) return { score, label: 'Weak', color: colors.error[500] };
  if (score <= 4) return { score, label: 'Medium', color: colors.warning[500] };
  return { score, label: 'Strong', color: colors.success[500] };
};

export function PasswordInput<
  TFieldValues extends FieldValues = FieldValues,
  TName extends FieldPath<TFieldValues> = FieldPath<TFieldValues>
>({
  name,
  control,
  label,
  error,
  required = false,
  placeholder = 'Enter password',
  showStrengthIndicator = false,
  containerStyle,
  labelStyle,
  inputStyle,
  errorStyle,
}: PasswordInputProps<TFieldValues, TName>) {
  const [isVisible, setIsVisible] = useState(false);
  const [currentPassword, setCurrentPassword] = useState('');

  const toggleVisibility = () => {
    setIsVisible(!isVisible);
  };

  const passwordStrength = showStrengthIndicator ? getPasswordStrength(currentPassword) : null;

  return (
    <View style={[styles.container, containerStyle]}>
      {label && (
        <Text style={[styles.label, labelStyle]}>
          {label}
          {required && <Text style={styles.required}> *</Text>}
        </Text>
      )}
      
      <Controller
        control={control}
        name={name}
        render={({ field: { onChange, onBlur, value }, fieldState: { error: fieldError } }) => (
          <>
            <View style={styles.inputContainer}>
              <TextInput
                style={[
                  styles.input,
                  fieldError || error ? styles.inputError : undefined,
                  inputStyle,
                ]}
                value={value}
                onChangeText={(text) => {
                  onChange(text);
                  setCurrentPassword(text);
                }}
                onBlur={onBlur}
                placeholder={placeholder}
                placeholderTextColor={colors.text.secondary}
                secureTextEntry={!isVisible}
                autoCapitalize="none"
                autoCorrect={false}
              />
              <TouchableOpacity
                style={styles.eyeButton}
                onPress={toggleVisibility}
                accessibilityLabel={isVisible ? 'Hide password' : 'Show password'}
              >
                <Text style={styles.eyeIcon}>
                  {isVisible ? '🙈' : '👁️'}
                </Text>
              </TouchableOpacity>
            </View>

            {/* Password Strength Indicator */}
            {showStrengthIndicator && currentPassword.length > 0 && passwordStrength && (
              <View style={styles.strengthContainer}>
                <View style={styles.strengthBarContainer}>
                  {[...Array(6)].map((_, index) => (
                    <View
                      key={index}
                      style={[
                        styles.strengthBar,
                        {
                          backgroundColor:
                            index < passwordStrength.score
                              ? passwordStrength.color
                              : colors.gray[200],
                        },
                      ]}
                    />
                  ))}
                </View>
                <Text style={[styles.strengthLabel, { color: passwordStrength.color }]}>
                  {passwordStrength.label}
                </Text>
              </View>
            )}

            {(fieldError || error) && (
              <Text style={[styles.errorText, errorStyle]}>
                {fieldError?.message || error}
              </Text>
            )}
          </>
        )}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    marginBottom: spacing.md,
  },
  label: {
    ...typography.body1,
    color: colors.text.primary,
    marginBottom: spacing.xs,
    fontWeight: '500',
  },
  required: {
    color: colors.error[500],
  },
  inputContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    position: 'relative',
  },
  input: {
    ...typography.body1,
    flex: 1,
    borderWidth: 1,
    borderColor: colors.border.primary,
    borderRadius: 8,
    paddingHorizontal: spacing.md,
    paddingVertical: spacing.sm,
    paddingRight: 48, // Space for eye icon
    backgroundColor: colors.background.primary,
    color: colors.text.primary,
    minHeight: 48,
  },
  inputError: {
    borderColor: colors.error[500],
    borderWidth: 2,
  },
  eyeButton: {
    position: 'absolute',
    right: spacing.sm,
    padding: spacing.xs,
    justifyContent: 'center',
    alignItems: 'center',
  },
  eyeIcon: {
    fontSize: 18,
  },
  strengthContainer: {
    marginTop: spacing.xs,
  },
  strengthBarContainer: {
    flexDirection: 'row',
    marginBottom: spacing.xs,
    gap: 2,
  },
  strengthBar: {
    flex: 1,
    height: 4,
    borderRadius: 2,
  },
  strengthLabel: {
    ...typography.caption,
    fontWeight: '500',
  },
  errorText: {
    ...typography.caption,
    color: colors.error[500],
    marginTop: spacing.xs,
  },
});

export default PasswordInput;
