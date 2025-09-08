import React, { useState } from 'react';
import {
  View,
  Text,
  Modal,
  ScrollView,
  TouchableOpacity,
  StyleSheet,
  Platform,
  SafeAreaView,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { Button } from '@components/ui';
import { colors, typography, spacing } from '@styles/theme';

interface FilterOptions {
  categories: string[];
  priceRange: { min: number; max: number };
  rating: number;
  availability: string[];
  duration: { min: number; max: number };
}

interface FilterModalProps {
  visible: boolean;
  onClose: () => void;
  filters: FilterOptions;
  onApplyFilters: (filters: FilterOptions) => void;
  categories: string[];
}

const AVAILABILITY_OPTIONS = [
  { value: 'available', label: 'Available', color: colors.success[500] },
  { value: 'busy', label: 'Busy', color: colors.warning[500] },
  { value: 'offline', label: 'Offline', color: colors.error[500] },
];

const RATING_OPTIONS = [
  { value: 0, label: 'All Ratings' },
  { value: 4.5, label: '4.5+ Stars' },
  { value: 4.0, label: '4.0+ Stars' },
  { value: 3.5, label: '3.5+ Stars' },
  { value: 3.0, label: '3.0+ Stars' },
];

const PRICE_RANGES = [
  { min: 0, max: 25, label: '$0 - $25' },
  { min: 25, max: 50, label: '$25 - $50' },
  { min: 50, max: 100, label: '$50 - $100' },
  { min: 100, max: 200, label: '$100 - $200' },
  { min: 200, max: 1000, label: '$200+' },
];

const DURATION_RANGES = [
  { min: 0, max: 30, label: 'Under 30 min' },
  { min: 30, max: 60, label: '30 - 60 min' },
  { min: 60, max: 120, label: '1 - 2 hours' },
  { min: 120, max: 300, label: '2+ hours' },
];

export const FilterModal: React.FC<FilterModalProps> = ({
  visible,
  onClose,
  filters,
  onApplyFilters,
  categories,
}) => {
  const [localFilters, setLocalFilters] = useState<FilterOptions>(filters);

  const handleApply = () => {
    onApplyFilters(localFilters);
    onClose();
  };

  const handleReset = () => {
    const resetFilters: FilterOptions = {
      categories: [],
      priceRange: { min: 0, max: 1000 },
      rating: 0,
      availability: [],
      duration: { min: 0, max: 300 },
    };
    setLocalFilters(resetFilters);
  };

  const toggleCategory = (category: string) => {
    setLocalFilters(prev => ({
      ...prev,
      categories: prev.categories.includes(category)
        ? prev.categories.filter(c => c !== category)
        : [...prev.categories, category],
    }));
  };

  const toggleAvailability = (availability: string) => {
    setLocalFilters(prev => ({
      ...prev,
      availability: prev.availability.includes(availability)
        ? prev.availability.filter(a => a !== availability)
        : [...prev.availability, availability],
    }));
  };

  const setPriceRange = (range: { min: number; max: number }) => {
    setLocalFilters(prev => ({
      ...prev,
      priceRange: range,
    }));
  };

  const setRating = (rating: number) => {
    setLocalFilters(prev => ({
      ...prev,
      rating,
    }));
  };

  const setDurationRange = (range: { min: number; max: number }) => {
    setLocalFilters(prev => ({
      ...prev,
      duration: range,
    }));
  };

  const renderSectionHeader = (title: string) => (
    <Text style={styles.sectionHeader}>{title}</Text>
  );

  const renderCategories = () => (
    <View style={styles.section}>
      {renderSectionHeader('Categories')}
      <View style={styles.optionsGrid}>
        {categories.map((category) => {
          const isSelected = localFilters.categories.includes(category);
          return (
            <TouchableOpacity
              key={category}
              style={[
                styles.optionChip,
                isSelected && styles.selectedChip,
                Platform.OS === 'ios' ? styles.chipIOS : styles.chipAndroid,
              ]}
              onPress={() => toggleCategory(category)}
            >
              <Text
                style={[
                  styles.optionText,
                  isSelected && styles.selectedOptionText,
                ]}
              >
                {category}
              </Text>
            </TouchableOpacity>
          );
        })}
      </View>
    </View>
  );

  const renderPriceRanges = () => (
    <View style={styles.section}>
      {renderSectionHeader('Price Range')}
      <View style={styles.optionsColumn}>
        {PRICE_RANGES.map((range, index) => {
          const isSelected = 
            localFilters.priceRange.min === range.min && 
            localFilters.priceRange.max === range.max;
          
          return (
            <TouchableOpacity
              key={index}
              style={[
                styles.optionRow,
                isSelected && styles.selectedRow,
              ]}
              onPress={() => setPriceRange(range)}
            >
              <Text
                style={[
                  styles.optionText,
                  isSelected && styles.selectedOptionText,
                ]}
              >
                {range.label}
              </Text>
              {isSelected && (
                <Ionicons
                  name="checkmark"
                  size={20}
                  color={colors.primary[500]}
                />
              )}
            </TouchableOpacity>
          );
        })}
      </View>
    </View>
  );

  const renderRatings = () => (
    <View style={styles.section}>
      {renderSectionHeader('Minimum Rating')}
      <View style={styles.optionsColumn}>
        {RATING_OPTIONS.map((option, index) => {
          const isSelected = localFilters.rating === option.value;
          
          return (
            <TouchableOpacity
              key={index}
              style={[
                styles.optionRow,
                isSelected && styles.selectedRow,
              ]}
              onPress={() => setRating(option.value)}
            >
              <View style={styles.ratingRow}>
                <Text
                  style={[
                    styles.optionText,
                    isSelected && styles.selectedOptionText,
                  ]}
                >
                  {option.label}
                </Text>
                {option.value > 0 && (
                  <View style={styles.starsContainer}>
                    {[...Array(5)].map((_, starIndex) => (
                      <Ionicons
                        key={starIndex}
                        name="star"
                        size={12}
                        color={
                          starIndex < Math.floor(option.value)
                            ? colors.warning[500]
                            : colors.background.tertiary
                        }
                      />
                    ))}
                  </View>
                )}
              </View>
              {isSelected && (
                <Ionicons
                  name="checkmark"
                  size={20}
                  color={colors.primary[500]}
                />
              )}
            </TouchableOpacity>
          );
        })}
      </View>
    </View>
  );

  const renderAvailability = () => (
    <View style={styles.section}>
      {renderSectionHeader('Availability')}
      <View style={styles.optionsGrid}>
        {AVAILABILITY_OPTIONS.map((option) => {
          const isSelected = localFilters.availability.includes(option.value);
          
          return (
            <TouchableOpacity
              key={option.value}
              style={[
                styles.optionChip,
                styles.availabilityChip,
                isSelected && styles.selectedChip,
                Platform.OS === 'ios' ? styles.chipIOS : styles.chipAndroid,
              ]}
              onPress={() => toggleAvailability(option.value)}
            >
              <View
                style={[
                  styles.availabilityDot,
                  { backgroundColor: option.color }
                ]}
              />
              <Text
                style={[
                  styles.optionText,
                  isSelected && styles.selectedOptionText,
                ]}
              >
                {option.label}
              </Text>
            </TouchableOpacity>
          );
        })}
      </View>
    </View>
  );

  const renderDuration = () => (
    <View style={styles.section}>
      {renderSectionHeader('Session Duration')}
      <View style={styles.optionsColumn}>
        {DURATION_RANGES.map((range, index) => {
          const isSelected = 
            localFilters.duration.min === range.min && 
            localFilters.duration.max === range.max;
          
          return (
            <TouchableOpacity
              key={index}
              style={[
                styles.optionRow,
                isSelected && styles.selectedRow,
              ]}
              onPress={() => setDurationRange(range)}
            >
              <Text
                style={[
                  styles.optionText,
                  isSelected && styles.selectedOptionText,
                ]}
              >
                {range.label}
              </Text>
              {isSelected && (
                <Ionicons
                  name="checkmark"
                  size={20}
                  color={colors.primary[500]}
                />
              )}
            </TouchableOpacity>
          );
        })}
      </View>
    </View>
  );

  return (
    <Modal
      visible={visible}
      animationType={Platform.OS === 'ios' ? 'slide' : 'fade'}
      presentationStyle={Platform.OS === 'ios' ? 'pageSheet' : 'fullScreen'}
      onRequestClose={onClose}
    >
      <SafeAreaView style={styles.container}>
        {/* Header */}
        <View style={styles.header}>
          <TouchableOpacity
            style={styles.closeButton}
            onPress={onClose}
          >
            <Ionicons
              name="close"
              size={24}
              color={colors.text.primary}
            />
          </TouchableOpacity>
          
          <Text style={styles.headerTitle}>Filters</Text>
          
          <TouchableOpacity
            style={styles.resetButton}
            onPress={handleReset}
          >
            <Text style={styles.resetText}>Reset</Text>
          </TouchableOpacity>
        </View>

        {/* Content */}
        <ScrollView
          style={styles.content}
          showsVerticalScrollIndicator={false}
          // Platform-specific scroll props
          bounces={Platform.OS === 'ios'}
          overScrollMode={Platform.OS === 'android' ? 'never' : undefined}
        >
          {renderCategories()}
          {renderPriceRanges()}
          {renderRatings()}
          {renderAvailability()}
          {renderDuration()}
        </ScrollView>

        {/* Footer */}
        <View style={styles.footer}>
          <Button
            title="Apply Filters"
            onPress={handleApply}
            style={styles.applyButton}
          />
        </View>
      </SafeAreaView>
    </Modal>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background.primary,
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: spacing.lg,
    paddingVertical: spacing.md,
    borderBottomWidth: 1,
    borderBottomColor: colors.background.secondary,
    // Platform-specific header styles
    ...Platform.select({
      ios: {
        shadowColor: colors.shadow.primary,
        shadowOffset: { width: 0, height: 1 },
        shadowOpacity: 0.1,
        shadowRadius: 2,
      },
      android: {
        elevation: 2,
      },
    }),
  },
  closeButton: {
    padding: spacing.sm,
    marginLeft: -spacing.sm,
  },
  headerTitle: {
    ...typography.h3,
    color: colors.text.primary,
    fontWeight: '600',
  },
  resetButton: {
    padding: spacing.sm,
    marginRight: -spacing.sm,
  },
  resetText: {
    ...typography.body1,
    color: colors.primary[500],
    fontWeight: '600',
  },
  content: {
    flex: 1,
    paddingHorizontal: spacing.lg,
  },
  section: {
    marginVertical: spacing.lg,
  },
  sectionHeader: {
    ...typography.h4,
    color: colors.text.primary,
    fontWeight: '600',
    marginBottom: spacing.md,
  },
  optionsGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: spacing.sm,
  },
  optionsColumn: {
    gap: spacing.sm,
  },
  optionChip: {
    paddingHorizontal: spacing.md,
    paddingVertical: spacing.sm,
    borderRadius: 20,
    backgroundColor: colors.background.secondary,
    borderWidth: 1,
    borderColor: colors.background.tertiary,
  },
  // Platform-specific chip styles
  chipIOS: {
    shadowColor: colors.shadow.primary,
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.05,
    shadowRadius: 2,
  },
  chipAndroid: {
    elevation: 1,
  },
  selectedChip: {
    backgroundColor: colors.primary[100],
    borderColor: colors.primary[300],
  },
  availabilityChip: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  availabilityDot: {
    width: 8,
    height: 8,
    borderRadius: 4,
    marginRight: spacing.sm,
  },
  optionRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingVertical: spacing.md,
    paddingHorizontal: spacing.md,
    borderRadius: 8,
    backgroundColor: colors.background.secondary,
  },
  selectedRow: {
    backgroundColor: colors.primary[50],
  },
  optionText: {
    ...typography.body1,
    color: colors.text.primary,
    fontWeight: '500',
  },
  selectedOptionText: {
    color: colors.primary[700],
    fontWeight: '600',
  },
  ratingRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: spacing.sm,
  },
  starsContainer: {
    flexDirection: 'row',
    gap: 2,
  },
  footer: {
    padding: spacing.lg,
    borderTopWidth: 1,
    borderTopColor: colors.background.secondary,
    // Platform-specific footer styles
    ...Platform.select({
      ios: {
        shadowColor: colors.shadow.primary,
        shadowOffset: { width: 0, height: -1 },
        shadowOpacity: 0.1,
        shadowRadius: 2,
      },
      android: {
        elevation: 4,
      },
    }),
  },
  applyButton: {
    width: '100%',
  },
});

export default FilterModal;
