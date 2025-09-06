import React from 'react';
import {
  View,
  Text,
  ScrollView,
  TouchableOpacity,
  StyleSheet,
  Platform,
  ViewStyle,
} from 'react-native';
import { colors, typography, spacing } from '@styles/theme';

interface CategoryTabsProps {
  categories: string[];
  selectedCategory: string;
  onCategoryChange: (category: string) => void;
  style?: ViewStyle;
}

export const CategoryTabs: React.FC<CategoryTabsProps> = ({
  categories,
  selectedCategory,
  onCategoryChange,
  style,
}) => {
  const renderTab = (category: string, index: number) => {
    const isSelected = category === selectedCategory;
    
    return (
      <TouchableOpacity
        key={category}
        style={[
          styles.tab,
          isSelected && styles.selectedTab,
          // Platform-specific tab styles
          Platform.OS === 'ios' ? styles.tabIOS : styles.tabAndroid,
          isSelected && Platform.OS === 'ios' && styles.selectedTabIOS,
          isSelected && Platform.OS === 'android' && styles.selectedTabAndroid,
        ]}
        onPress={() => onCategoryChange(category)}
        activeOpacity={0.7}
      >
        <Text
          style={[
            styles.tabText,
            isSelected && styles.selectedTabText,
          ]}
          numberOfLines={1}
        >
          {category}
        </Text>
      </TouchableOpacity>
    );
  };

  return (
    <View style={[styles.container, style]}>
      <ScrollView
        horizontal
        showsHorizontalScrollIndicator={false}
        contentContainerStyle={styles.scrollContent}
        // Platform-specific scroll props
        bounces={Platform.OS === 'ios'}
        overScrollMode={Platform.OS === 'android' ? 'never' : undefined}
        decelerationRate={Platform.OS === 'ios' ? 'fast' : 'normal'}
      >
        {categories.map((category, index) => renderTab(category, index))}
      </ScrollView>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    backgroundColor: colors.background.primary,
    paddingVertical: spacing.md,
    // Platform-specific container styles
    ...Platform.select({
      ios: {
        shadowColor: colors.shadow.primary,
        shadowOffset: { width: 0, height: 1 },
        shadowOpacity: 0.05,
        shadowRadius: 2,
      },
      android: {
        elevation: 1,
      },
    }),
  },
  scrollContent: {
    paddingHorizontal: spacing.lg,
    gap: spacing.sm,
  },
  tab: {
    paddingHorizontal: spacing.lg,
    paddingVertical: spacing.sm,
    borderRadius: 20,
    backgroundColor: colors.background.secondary,
    minWidth: 60,
    alignItems: 'center',
    justifyContent: 'center',
  },
  // iOS specific tab styles
  tabIOS: {
    shadowColor: colors.shadow.primary,
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
  },
  // Android specific tab styles
  tabAndroid: {
    elevation: 1,
  },
  selectedTab: {
    backgroundColor: colors.primary[500],
  },
  // iOS specific selected tab styles
  selectedTabIOS: {
    shadowColor: colors.primary[500],
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.2,
    shadowRadius: 4,
  },
  // Android specific selected tab styles
  selectedTabAndroid: {
    elevation: 3,
  },
  tabText: {
    ...typography.body2,
    color: colors.text.secondary,
    fontWeight: '500',
    // Platform-specific text styles
    ...Platform.select({
      ios: {
        letterSpacing: -0.2,
      },
      android: {
        includeFontPadding: false,
      },
    }),
  },
  selectedTabText: {
    color: colors.white,
    fontWeight: '600',
  },
});

export default CategoryTabs;
