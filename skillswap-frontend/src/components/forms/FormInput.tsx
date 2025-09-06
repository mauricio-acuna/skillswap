import React from 'react';
import {
  View,
  Text,
  TextInput,
  TextInputProps,
  StyleSheet,
  ViewStyle,
  TextStyle,
} from 'react-native';
import { Control, Controller, FieldPath, FieldValues } from 'react-hook-form';
import { colors, typography, spacing } from '@styles/theme';

interface FormInputProps<
  TFieldValues extends FieldValues = FieldValues,
  TName extends FieldPath<TFieldValues> = FieldPath<TFieldValues>
> extends Omit<TextInputProps, 'value' | 'onChangeText'> {
  name: TName;
  control: Control<TFieldValues>;
  label?: string;
  error?: string;
  required?: boolean;
  containerStyle?: ViewStyle;
  labelStyle?: TextStyle;
  inputStyle?: TextStyle;
  errorStyle?: TextStyle;
}

export function FormInput<
  TFieldValues extends FieldValues = FieldValues,
  TName extends FieldPath<TFieldValues> = FieldPath<TFieldValues>
>({
  name,
  control,
  label,
  error,
  required = false,
  containerStyle,
  labelStyle,
  inputStyle,
  errorStyle,
  ...textInputProps
}: FormInputProps<TFieldValues, TName>) {
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
            <TextInput
              style={[
                styles.input,
                fieldError || error ? styles.inputError : undefined,
                inputStyle,
              ]}
              value={value}
              onChangeText={onChange}
              onBlur={onBlur}
              placeholderTextColor={colors.text.tertiary}
              {...textInputProps}
            />
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
  input: {
    ...typography.body1,
    borderWidth: 1,
    borderColor: colors.border.primary,
    borderRadius: 8,
    paddingHorizontal: spacing.md,
    paddingVertical: spacing.sm,
    backgroundColor: colors.background.primary,
    color: colors.text.primary,
    minHeight: 48,
  },
  inputError: {
    borderColor: colors.error[500],
    borderWidth: 2,
  },
  errorText: {
    ...typography.caption,
    color: colors.error[500],
    marginTop: spacing.xs,
  },
});

export default FormInput;
