import React from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  ViewStyle,
  TextStyle,
} from 'react-native';
import { Control, Controller, FieldPath, FieldValues } from 'react-hook-form';
import { colors, typography, spacing } from '@styles/theme';

interface CheckboxProps<
  TFieldValues extends FieldValues = FieldValues,
  TName extends FieldPath<TFieldValues> = FieldPath<TFieldValues>
> {
  name: TName;
  control: Control<TFieldValues>;
  label: string;
  error?: string;
  required?: boolean;
  containerStyle?: ViewStyle;
  labelStyle?: TextStyle;
  checkboxStyle?: ViewStyle;
  errorStyle?: TextStyle;
  onLabelPress?: () => void;
}

export function Checkbox<
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
  checkboxStyle,
  errorStyle,
  onLabelPress,
}: CheckboxProps<TFieldValues, TName>) {
  return (
    <View style={[styles.container, containerStyle]}>
      <Controller
        control={control}
        name={name}
        render={({ field: { onChange, value }, fieldState: { error: fieldError } }) => (
          <>
            <TouchableOpacity
              style={styles.checkboxContainer}
              onPress={() => onChange(!value)}
              activeOpacity={0.8}
            >
              <View
                style={[
                  styles.checkbox,
                  value && styles.checkboxChecked,
                  (fieldError || error) && styles.checkboxError,
                  checkboxStyle,
                ]}
              >
                {value && <Text style={styles.checkmark}>✓</Text>}
              </View>
              
              <TouchableOpacity
                style={styles.labelContainer}
                onPress={onLabelPress}
                activeOpacity={0.8}
              >
                <Text style={[styles.label, labelStyle]}>
                  {label}
                  {required && <Text style={styles.required}> *</Text>}
                </Text>
              </TouchableOpacity>
            </TouchableOpacity>

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
  checkboxContainer: {
    flexDirection: 'row',
    alignItems: 'flex-start',
  },
  checkbox: {
    width: 20,
    height: 20,
    borderWidth: 2,
    borderColor: colors.border.primary,
    borderRadius: 4,
    backgroundColor: colors.background.primary,
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: spacing.sm,
    marginTop: 2, // Align with text baseline
  },
  checkboxChecked: {
    backgroundColor: colors.primary[500],
    borderColor: colors.primary[500],
  },
  checkboxError: {
    borderColor: colors.error[500],
    borderWidth: 2,
  },
  checkmark: {
    color: colors.background.primary,
    fontSize: 12,
    fontWeight: 'bold',
  },
  labelContainer: {
    flex: 1,
  },
  label: {
    ...typography.body2,
    color: colors.text.primary,
    lineHeight: 20,
  },
  required: {
    color: colors.error[500],
  },
  errorText: {
    ...typography.caption,
    color: colors.error[500],
    marginTop: spacing.xs,
    marginLeft: 32, // Align with label text
  },
});

export default Checkbox;
