import React from 'react';
import {
  View,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  Platform,
  ViewStyle,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { colors, typography, spacing } from '@styles/theme';

interface SearchBarProps {
  value: string;
  onChangeText: (text: string) => void;
  placeholder?: string;
  onFocus?: () => void;
  onBlur?: () => void;
  onSubmit?: () => void;
  onClear?: () => void;
  style?: ViewStyle;
  autoFocus?: boolean;
  editable?: boolean;
}

export const SearchBar: React.FC<SearchBarProps> = ({
  value,
  onChangeText,
  placeholder = 'Search...',
  onFocus,
  onBlur,
  onSubmit,
  onClear,
  style,
  autoFocus = false,
  editable = true,
}) => {
  const handleClear = () => {
    onChangeText('');
    onClear?.();
  };

  return (
    <View style={[styles.container, style]}>
      <View style={styles.searchIcon}>
        <Ionicons 
          name="search" 
          size={20} 
          color={colors.text.tertiary} 
        />
      </View>
      
      <TextInput
        value={value}
        onChangeText={onChangeText}
        placeholder={placeholder}
        placeholderTextColor={colors.text.tertiary}
        style={styles.input}
        onFocus={onFocus}
        onBlur={onBlur}
        onSubmitEditing={onSubmit}
        autoFocus={autoFocus}
        editable={editable}
        returnKeyType="search"
        clearButtonMode={Platform.OS === 'ios' ? 'while-editing' : 'never'}
        // iOS specific props
        keyboardAppearance={Platform.OS === 'ios' ? 'light' : undefined}
        // Android specific props
        underlineColorAndroid="transparent"
        textContentType={Platform.OS === 'ios' ? 'none' : undefined}
      />

      {/* Android clear button (iOS uses native clearButtonMode) */}
      {Platform.OS === 'android' && value.length > 0 && (
        <TouchableOpacity 
          style={styles.clearButton} 
          onPress={handleClear}
          hitSlop={{ top: 8, bottom: 8, left: 8, right: 8 }}
        >
          <Ionicons 
            name="close-circle" 
            size={18} 
            color={colors.text.tertiary} 
          />
        </TouchableOpacity>
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.background.secondary,
    borderRadius: 20,
    paddingHorizontal: spacing.md,
    height: 40,
    // Platform-specific styles
    ...Platform.select({
      ios: {
        shadowColor: colors.shadow.primary,
        shadowOffset: { width: 0, height: 1 },
        shadowOpacity: 0.1,
        shadowRadius: 2,
      },
      android: {
        elevation: 1,
      },
    }),
  },
  searchIcon: {
    marginRight: spacing.sm,
  },
  input: {
    flex: 1,
    ...typography.body1,
    color: colors.text.primary,
    height: '100%',
    // Platform-specific input styles
    ...Platform.select({
      ios: {
        paddingVertical: 0,
      },
      android: {
        paddingVertical: spacing.xs,
        includeFontPadding: false,
        textAlignVertical: 'center',
      },
    }),
  },
  clearButton: {
    marginLeft: spacing.sm,
    padding: spacing.xs,
  },
});

export default SearchBar;
