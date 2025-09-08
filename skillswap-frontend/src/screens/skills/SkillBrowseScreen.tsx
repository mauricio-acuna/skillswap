import React, { useState, useEffect, useCallback } from 'react';
import {
  View,
  Text,
  FlatList,
  StyleSheet,
  SafeAreaView,
  RefreshControl,
  Platform,
} from 'react-native';
import { StackScreenProps } from '@react-navigation/stack';
import { 
  Card, 
  Button, 
  Avatar, 
  Badge, 
  Loading,
  LoadingSkeleton 
} from '@components/ui';
import { SearchBar } from './components/SearchBar';
import { FilterModal, CategoryTabs, SkillCard } from './components';
import { colors, typography, spacing } from '@styles/theme';
import { MainStackParamList } from '@navigation/navigationTypes';
import { skillsService, Skill, SkillFilter } from '../../services';

type SkillBrowseScreenProps = StackScreenProps<MainStackParamList, 'SkillBrowse'>;

interface LocalFilters {
  search: string;
  category: string;
  sortBy: 'relevance' | 'rating' | 'price' | 'recent';
  priceRange: [number, number];
  level: 'all' | 'Beginner' | 'Intermediate' | 'Advanced' | 'Expert';
  availability: 'all' | 'available' | 'busy' | 'offline';
}

interface FilterOptions {
  categories: string[];
  priceRange: { min: number; max: number };
  rating: number;
  availability: string[];
  duration: { min: number; max: number };
}

const CATEGORIES = [
  'All',
  'Technology',
  'Design',
  'Business',
  'Language',
  'Music',
  'Fitness',
  'Cooking',
  'Art',
];

const SORT_OPTIONS = [
  { label: 'Relevance', value: 'relevance' },
  { label: 'Price: Low to High', value: 'price_asc' },
  { label: 'Price: High to Low', value: 'price_desc' },
  { label: 'Rating', value: 'rating' },
  { label: 'Newest', value: 'newest' },
];

export const SkillBrowseScreen: React.FC<SkillBrowseScreenProps> = ({ navigation }) => {
  const [skills, setSkills] = useState<Skill[]>([]);
  const [filteredSkills, setFilteredSkills] = useState<Skill[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('All');
  const [sortBy, setSortBy] = useState('relevance');
  const [showFilterModal, setShowFilterModal] = useState(false);
  const [filters, setFilters] = useState<FilterOptions>({
    categories: [],
    priceRange: { min: 0, max: 1000 },
    rating: 0,
    availability: [],
    duration: { min: 0, max: 300 },
  });

  useEffect(() => {
    loadSkills();
  }, []);

  useEffect(() => {
    applyFiltersAndSort();
  }, [skills, searchQuery, selectedCategory, sortBy, filters]);

  const loadSkills = async () => {
    try {
      setLoading(true);
      const response = await skillsService.getSkills();
      setSkills(response.items);
    } catch (error) {
      console.error('Error loading skills:', error);
      // The service already provides mock data as fallback
      setSkills([]);
    } finally {
      setLoading(false);
    }
  };

  const handleRefresh = useCallback(async () => {
    setRefreshing(true);
    await loadSkills();
    setRefreshing(false);
  }, []);

  const applyFiltersAndSort = () => {
    let filtered = [...skills];

    // Apply search filter
    if (searchQuery.trim()) {
      filtered = filtered.filter(skill =>
        skill.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
        skill.description.toLowerCase().includes(searchQuery.toLowerCase()) ||
        skill.instructor.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        skill.tags.some(tag => tag.toLowerCase().includes(searchQuery.toLowerCase()))
      );
    }

    // Apply category filter
    if (selectedCategory !== 'All') {
      filtered = filtered.filter(skill => skill.category === selectedCategory);
    }

    // Apply additional filters
    if (filters.categories.length > 0) {
      filtered = filtered.filter(skill => filters.categories.includes(skill.category));
    }

    if (filters.rating > 0) {
      filtered = filtered.filter(skill => skill.rating >= filters.rating);
    }

    if (filters.availability.length > 0) {
      filtered = filtered.filter(skill => filters.availability.includes(skill.availability));
    }

    filtered = filtered.filter(skill => 
      skill.price >= filters.priceRange.min && 
      skill.price <= filters.priceRange.max
    );

    filtered = filtered.filter(skill => 
      skill.duration >= filters.duration.min && 
      skill.duration <= filters.duration.max
    );

    // Apply sorting
    filtered.sort((a, b) => {
      switch (sortBy) {
        case 'price_asc':
          return a.price - b.price;
        case 'price_desc':
          return b.price - a.price;
        case 'rating':
          return b.rating - a.rating;
        case 'newest':
          return 0; // Would use createdAt in real implementation
        case 'relevance':
        default:
          // Featured skills first, then by rating
          if (a.featured && !b.featured) return -1;
          if (!a.featured && b.featured) return 1;
          return b.rating - a.rating;
      }
    });

    setFilteredSkills(filtered);
  };

  const handleSkillPress = (skill: Skill) => {
    navigation.navigate('SkillDetail', { skillId: skill.id });
  };

  const handleInstructorPress = (instructorId: string) => {
    navigation.navigate('UserProfile', { userId: instructorId });
  };

  const renderSkillItem = ({ item, index }: { item: Skill; index: number }) => {
    const cardStyles = [
      styles.skillCard,
      Platform.OS === 'ios' ? styles.skillCardIOS : styles.skillCardAndroid
    ];

    return (
      <SkillCard
        skill={item}
        onPress={() => handleSkillPress(item)}
        onInstructorPress={() => handleInstructorPress(item.instructor.id)}
        style={StyleSheet.flatten(cardStyles)}
      />
    );
  };

  const renderEmptyState = () => (
    <View style={styles.emptyState}>
      <Text style={styles.emptyTitle}>No skills found</Text>
      <Text style={styles.emptyDescription}>
        Try adjusting your search or filters to find what you're looking for.
      </Text>
      <Button
        title="Clear Filters"
        onPress={() => {
          setSearchQuery('');
          setSelectedCategory('All');
          setFilters({
            categories: [],
            priceRange: { min: 0, max: 1000 },
            rating: 0,
            availability: [],
            duration: { min: 0, max: 300 },
          });
        }}
        variant="outline"
        style={styles.clearButton}
      />
    </View>
  );

  const renderLoadingSkeleton = () => (
    <View style={styles.container}>
      <View style={styles.header}>
        <LoadingSkeleton height={40} borderRadius={20} style={styles.searchSkeleton} />
        <LoadingSkeleton height={40} width={80} style={styles.filterSkeleton} />
      </View>
      
      <LoadingSkeleton height={50} style={styles.tabsSkeleton} />
      
      {[1, 2, 3].map(index => (
        <LoadingSkeleton 
          key={index}
          height={120} 
          style={styles.cardSkeleton} 
        />
      ))}
    </View>
  );

  if (loading && skills.length === 0) {
    return renderLoadingSkeleton();
  }

  return (
    <SafeAreaView style={styles.container}>
      {/* Header with Search and Filter */}
      <View style={styles.header}>
        <SearchBar
          value={searchQuery}
          onChangeText={setSearchQuery}
          placeholder="Search skills, instructors..."
          style={styles.searchBar}
        />
        <Button
          title="Filter"
          onPress={() => setShowFilterModal(true)}
          variant="outline"
          size="medium"
          style={styles.filterButton}
        />
      </View>

      {/* Category Tabs */}
      <CategoryTabs
        categories={CATEGORIES}
        selectedCategory={selectedCategory}
        onCategoryChange={setSelectedCategory}
        style={styles.categoryTabs}
      />

      {/* Results Summary */}
      <View style={styles.resultsHeader}>
        <Text style={styles.resultsCount}>
          {filteredSkills.length} skills found
        </Text>
        <Button
          title={`Sort: ${SORT_OPTIONS.find(opt => opt.value === sortBy)?.label}`}
          onPress={() => {
            // TODO: Show sort options modal
          }}
          variant="ghost"
          size="small"
        />
      </View>

      {/* Skills List */}
      <FlatList
        data={filteredSkills}
        renderItem={renderSkillItem}
        keyExtractor={(item) => item.id}
        contentContainerStyle={styles.listContent}
        showsVerticalScrollIndicator={false}
        refreshControl={
          <RefreshControl
            refreshing={refreshing}
            onRefresh={handleRefresh}
            colors={[colors.primary[500]]} // Android
            tintColor={colors.primary[500]} // iOS
          />
        }
        ListEmptyComponent={renderEmptyState}
        // Platform-specific optimizations
        removeClippedSubviews={Platform.OS === 'android'}
        maxToRenderPerBatch={Platform.OS === 'ios' ? 10 : 5}
        updateCellsBatchingPeriod={Platform.OS === 'ios' ? 50 : 100}
        windowSize={Platform.OS === 'ios' ? 10 : 5}
      />

      {/* Filter Modal */}
      <FilterModal
        visible={showFilterModal}
        onClose={() => setShowFilterModal(false)}
        filters={filters}
        onApplyFilters={setFilters}
        categories={CATEGORIES.slice(1)} // Exclude 'All'
      />
    </SafeAreaView>
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
    paddingHorizontal: spacing.lg,
    paddingVertical: spacing.md,
    backgroundColor: colors.background.primary,
    // Platform-specific shadows
    ...Platform.select({
      ios: {
        shadowColor: colors.shadow.primary,
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.1,
        shadowRadius: 4,
      },
      android: {
        elevation: 4,
      },
    }),
  },
  searchBar: {
    flex: 1,
    marginRight: spacing.md,
  },
  filterButton: {
    paddingHorizontal: spacing.lg,
  },
  categoryTabs: {
    backgroundColor: colors.background.primary,
  },
  resultsHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: spacing.lg,
    paddingVertical: spacing.md,
    backgroundColor: colors.background.secondary,
  },
  resultsCount: {
    ...typography.body1,
    color: colors.text.secondary,
  },
  listContent: {
    paddingHorizontal: spacing.lg,
    paddingBottom: spacing.xl,
  },
  skillCard: {
    marginBottom: spacing.md,
  },
  // Platform-specific styles
  skillCardIOS: {
    shadowColor: colors.shadow.primary,
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 8,
  },
  skillCardAndroid: {
    elevation: 2,
  },
  emptyState: {
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: spacing.xl * 2,
  },
  emptyTitle: {
    ...typography.h3,
    color: colors.text.primary,
    marginBottom: spacing.md,
  },
  emptyDescription: {
    ...typography.body1,
    color: colors.text.secondary,
    textAlign: 'center',
    marginBottom: spacing.xl,
    paddingHorizontal: spacing.lg,
  },
  clearButton: {
    paddingHorizontal: spacing.xl,
  },
  // Loading skeleton styles
  searchSkeleton: {
    flex: 1,
    marginRight: spacing.md,
  },
  filterSkeleton: {
    // Fixed width for filter button
  },
  tabsSkeleton: {
    marginHorizontal: spacing.lg,
    marginVertical: spacing.md,
  },
  cardSkeleton: {
    marginHorizontal: spacing.lg,
    marginBottom: spacing.md,
  },
});

export default SkillBrowseScreen;
