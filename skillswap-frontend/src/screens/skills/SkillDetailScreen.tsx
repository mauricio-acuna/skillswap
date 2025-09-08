import React, { useEffect, useState } from 'react';
import {
  View,
  Text,
  Image,
  ScrollView,
  TouchableOpacity,
  StyleSheet,
  Platform,
  SafeAreaView,
  StatusBar,
  Alert,
} from 'react-native';
import { StackScreenProps } from '@react-navigation/stack';
import { Ionicons } from '@expo/vector-icons';
import { 
  Button, 
  Card, 
  Avatar, 
  Badge, 
  Loading 
} from '@components/ui';
import { colors, typography, spacing } from '@styles/theme';
import { PlatformUtils, PlatformConstants } from '@utils/platformUtils';
import { MainStackParamList } from '@navigation/navigationTypes';
import { skillsService, Skill } from '../../services';

type SkillDetailScreenProps = StackScreenProps<MainStackParamList, 'SkillDetail'>;

interface SkillDetail {
  id: string;
  title: string;
  description: string;
  longDescription: string;
  instructor: {
    id: string;
    name: string;
    avatar?: string;
    rating: number;
    reviewCount: number;
    verified: boolean;
    bio: string;
    experience: string;
    skills: string[];
    responseTime: string;
  };
  category: string;
  subcategory: string;
  price: number;
  duration: number;
  rating: number;
  reviewCount: number;
  tags: string[];
  availability: 'available' | 'busy' | 'offline';
  imageUrl?: string;
  gallery: string[];
  featured: boolean;
  learningOutcomes: string[];
  requirements: string[];
  difficulty: 'beginner' | 'intermediate' | 'advanced';
  language: string;
  maxStudents: number;
  totalSessions: number;
  completedSessions: number;
  sessionFormat: 'one-on-one' | 'group' | 'workshop';
  location: 'online' | 'in-person' | 'hybrid';
  timezone: string;
  equipment: string[];
  certificateOffered: boolean;
  refundPolicy: string;
  cancellationPolicy: string;
  reviews: Review[];
  similarSkills: string[];
}

interface Review {
  id: string;
  userId: string;
  userName: string;
  userAvatar?: string;
  rating: number;
  comment: string;
  date: string;
  helpful: number;
  verified: boolean;
}

interface TimeSlot {
  id: string;
  date: string;
  time: string;
  available: boolean;
  price: number;
}

export const SkillDetailScreen: React.FC<SkillDetailScreenProps> = ({ 
  navigation, 
  route 
}) => {
  const { skillId } = route.params;
  const [skill, setSkill] = useState<Skill | null>(null);
  const [loading, setLoading] = useState(true);
  const [bookmarked, setBookmarked] = useState(false);
  const [selectedImageIndex, setSelectedImageIndex] = useState(0);
  const [showFullDescription, setShowFullDescription] = useState(false);
  const [activeTab, setActiveTab] = useState<'overview' | 'reviews' | 'instructor'>('overview');

  // Mock data - Replace with API call
  const mockSkill: SkillDetail = {
    id: skillId,
    title: 'React Native Development Masterclass',
    description: 'Learn to build amazing mobile apps with React Native from scratch',
    longDescription: 'This comprehensive React Native course will take you from beginner to advanced level. You\'ll learn how to build cross-platform mobile applications using React Native, Redux, Firebase, and many other technologies. By the end of this course, you\'ll have built several real-world projects and have the skills to create your own mobile apps.',
    instructor: {
      id: 'inst1',
      name: 'Sarah Wilson',
      rating: 4.9,
      reviewCount: 127,
      verified: true,
      bio: 'Senior Mobile Developer with 8+ years of experience in React Native and mobile app development.',
      experience: '8 years',
      skills: ['React Native', 'React', 'JavaScript', 'TypeScript', 'Firebase', 'Redux'],
      responseTime: 'Usually responds within 2 hours',
    },
    category: 'Technology',
    subcategory: 'Mobile Development',
    price: 45,
    duration: 60,
    rating: 4.8,
    reviewCount: 89,
    tags: ['React Native', 'Mobile', 'JavaScript', 'Cross-platform'],
    availability: 'available',
    imageUrl: 'https://example.com/skill-image.jpg',
    gallery: [
      'https://example.com/gallery1.jpg',
      'https://example.com/gallery2.jpg',
      'https://example.com/gallery3.jpg',
    ],
    featured: true,
    learningOutcomes: [
      'Build complete React Native applications',
      'Understand React Native architecture',
      'Implement navigation and state management',
      'Deploy apps to App Store and Google Play',
      'Integrate with REST APIs and databases',
    ],
    requirements: [
      'Basic JavaScript knowledge',
      'Computer with internet connection',
      'Node.js installed',
      'Text editor or IDE',
    ],
    difficulty: 'intermediate',
    language: 'English',
    maxStudents: 5,
    totalSessions: 156,
    completedSessions: 142,
    sessionFormat: 'one-on-one',
    location: 'online',
    timezone: 'PST',
    equipment: ['Computer', 'Webcam', 'Microphone'],
    certificateOffered: true,
    refundPolicy: '100% refund within 24 hours',
    cancellationPolicy: 'Free cancellation up to 24 hours before session',
    reviews: [
      {
        id: '1',
        userId: 'user1',
        userName: 'John Doe',
        rating: 5,
        comment: 'Excellent teaching style and very knowledgeable. Highly recommended!',
        date: '2024-01-15',
        helpful: 12,
        verified: true,
      },
      {
        id: '2',
        userId: 'user2',
        userName: 'Jane Smith',
        rating: 4,
        comment: 'Good course content but could use more practical examples.',
        date: '2024-01-10',
        helpful: 8,
        verified: true,
      },
    ],
    similarSkills: ['skill2', 'skill3', 'skill4'],
  };

  useEffect(() => {
    loadSkillDetail();
  }, [skillId]);

  const loadSkillDetail = async () => {
    try {
      setLoading(true);
      const skillData = await skillsService.getSkillById(skillId);
      
      if (skillData) {
        setSkill(skillData);
        setBookmarked(skillData.isBookmarked);
      } else {
        Alert.alert('Error', 'Skill not found.');
        navigation.goBack();
      }
    } catch (error) {
      console.error('Error loading skill detail:', error);
      Alert.alert('Error', 'Failed to load skill details. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleBookSession = () => {
    if (!skill) return;
    
    navigation.navigate('BookSession', { 
      skillId: skill.id,
      instructorId: skill.instructor.id 
    });
  };

  const handleContactInstructor = () => {
    if (!skill) return;
    
    navigation.navigate('Chat', { 
      recipientId: skill.instructor.id,
      recipientName: skill.instructor.name 
    });
  };

  const handleBookmark = () => {
    setBookmarked(!bookmarked);
    PlatformUtils.triggerHapticFeedback('light');
    
    // TODO: Save bookmark to backend
  };

  const handleShare = () => {
    // TODO: Implement platform-specific sharing
    if (Platform.OS === 'ios') {
      // iOS sharing implementation
    } else {
      // Android sharing implementation
    }
  };

  const renderHeader = () => (
    <View style={styles.header}>
      <TouchableOpacity
        style={styles.backButton}
        onPress={() => navigation.goBack()}
        hitSlop={PlatformConstants.hitSlop}
      >
        <Ionicons
          name="arrow-back"
          size={24}
          color={colors.text.primary}
        />
      </TouchableOpacity>
      
      <View style={styles.headerActions}>
        <TouchableOpacity
          style={styles.actionButton}
          onPress={handleShare}
          hitSlop={PlatformConstants.hitSlop}
        >
          <Ionicons
            name="share-outline"
            size={24}
            color={colors.text.primary}
          />
        </TouchableOpacity>
        
        <TouchableOpacity
          style={styles.actionButton}
          onPress={handleBookmark}
          hitSlop={PlatformConstants.hitSlop}
        >
          <Ionicons
            name={bookmarked ? "bookmark" : "bookmark-outline"}
            size={24}
            color={bookmarked ? colors.primary[500] : colors.text.primary}
          />
        </TouchableOpacity>
      </View>
    </View>
  );

  const renderImageGallery = () => {
    if (!skill?.images.length) return null;

    return (
      <ScrollView
        horizontal
        pagingEnabled
        showsHorizontalScrollIndicator={false}
        style={styles.galleryContainer}
        onMomentumScrollEnd={(event) => {
          const index = Math.round(
            event.nativeEvent.contentOffset.x / 
            event.nativeEvent.layoutMeasurement.width
          );
          setSelectedImageIndex(index);
        }}
      >
        {skill.images.map((imageUrl: string, index: number) => (
          <Image
            key={index}
            source={{ uri: imageUrl }}
            style={styles.galleryImage}
            resizeMode="cover"
          />
        ))}
      </ScrollView>
    );
  };

  const renderSkillInfo = () => {
    if (!skill) return null;

    return (
      <Card style={styles.infoCard}>
        <View style={styles.skillHeader}>
          <View style={styles.skillTitleSection}>
            <Text style={styles.categoryText}>{skill.category}</Text>
            <Text style={styles.titleText}>{skill.title}</Text>
            
            <View style={styles.ratingContainer}>
              <Ionicons name="star" size={16} color={colors.warning[500]} />
              <Text style={styles.ratingText}>
                {skill.rating.toFixed(1)} ({skill.totalReviews} reviews)
              </Text>
            </View>
          </View>
          
          <View style={styles.priceSection}>
            <Text style={styles.priceText}>${skill.price}</Text>
            <Text style={styles.durationText}>per hour</Text>
          </View>
        </View>

        <View style={styles.tagsContainer}>
          {skill.tags.map((tag, index) => (
            <Badge
              key={index}
              label={tag}
              variant="secondary"
              style={styles.tag}
            />
          ))}
        </View>

        <Text style={styles.descriptionText}>
          {skill.description}
        </Text>
      </Card>
    );
  };

  const renderTabs = () => (
    <View style={styles.tabsContainer}>
      {['overview', 'reviews', 'instructor'].map((tab) => (
        <TouchableOpacity
          key={tab}
          style={[
            styles.tab,
            activeTab === tab && styles.activeTab
          ]}
          onPress={() => setActiveTab(tab as any)}
        >
          <Text style={[
            styles.tabText,
            activeTab === tab && styles.activeTabText
          ]}>
            {tab.charAt(0).toUpperCase() + tab.slice(1)}
          </Text>
        </TouchableOpacity>
      ))}
    </View>
  );

  const renderTabContent = () => {
    if (!skill) return null;

    switch (activeTab) {
      case 'overview':
        return renderOverviewTab();
      case 'reviews':
        return renderReviewsTab();
      case 'instructor':
        return renderInstructorTab();
      default:
        return null;
    }
  };

  const renderOverviewTab = () => (
    <View style={styles.tabContent}>
      {/* Learning outcomes and requirements not available in current Skill interface */}
      <Card style={styles.sectionCard}>
        <Text style={styles.sectionTitle}>About this skill</Text>
        <Text style={styles.listItemText}>
          This skill is offered by {skill?.instructor.name} who has a {skill?.instructor.rating} star rating 
          from {skill?.instructor.totalReviews} reviews.
        </Text>
      </Card>
    </View>
  );

  const renderReviewsTab = () => (
    <View style={styles.tabContent}>
      <Card style={styles.sectionCard}>
        <Text style={styles.sectionTitle}>Reviews</Text>
        <Text style={styles.listItemText}>
          This skill has a {skill?.rating.toFixed(1)} star rating from {skill?.totalReviews} reviews.
        </Text>
        <Text style={[styles.listItemText, { marginTop: 8, fontStyle: 'italic' }]}>
          Individual reviews are not yet available through the API.
        </Text>
      </Card>
    </View>
  );

  const renderInstructorTab = () => (
    <View style={styles.tabContent}>
      <Card style={styles.instructorCard}>
        <View style={styles.instructorHeader}>
          <Avatar
            source={skill?.instructor.avatar ? { uri: skill.instructor.avatar } : undefined}
            name={skill?.instructor.name}
            size="large"
          />
          <View style={styles.instructorInfo}>
            <Text style={styles.instructorName}>{skill?.instructor.name}</Text>
            {skill?.instructor.verifiedInstructor && (
              <View style={styles.verifiedContainer}>
                <Ionicons name="checkmark-circle" size={16} color={colors.primary[500]} />
                <Text style={styles.verifiedText}>Verified Instructor</Text>
            </View>
            )}
          </View>
        </View>

        <View style={styles.instructorStats}>
          <View style={styles.statItem}>
            <Text style={styles.statValue}>{skill?.instructor.rating.toFixed(1)}</Text>
            <Text style={styles.statLabel}>Rating</Text>
          </View>
          <View style={styles.statItem}>
            <Text style={styles.statValue}>{skill?.instructor.totalReviews}</Text>
            <Text style={styles.statLabel}>Reviews</Text>
          </View>
        </View>
      </Card>
    </View>
  );

  const renderBottomActions = () => (
    <View style={styles.bottomActions}>
      <Button
        title="Message"
        variant="outline"
        onPress={handleContactInstructor}
        style={styles.messageButton}
      />
      <Button
        title="Book Session"
        onPress={handleBookSession}
        style={styles.bookButton}
      />
    </View>
  );

  if (loading) {
    return (
      <SafeAreaView style={styles.container}>
        <Loading overlay message="Loading skill details..." />
      </SafeAreaView>
    );
  }

  if (!skill) {
    return (
      <SafeAreaView style={styles.container}>
        <View style={styles.errorContainer}>
          <Text style={styles.errorText}>Skill not found</Text>
          <Button
            title="Go Back"
            onPress={() => navigation.goBack()}
            style={styles.errorButton}
          />
        </View>
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView style={styles.container}>
      <StatusBar
        barStyle={Platform.OS === 'ios' ? 'dark-content' : 'light-content'}
        backgroundColor={Platform.OS === 'android' ? colors.background.primary : undefined}
      />
      
      {renderHeader()}
      
      <ScrollView
        style={styles.content}
        showsVerticalScrollIndicator={false}
        bounces={Platform.OS === 'ios'}
      >
        {renderImageGallery()}
        {renderSkillInfo()}
        {renderTabs()}
        {renderTabContent()}
      </ScrollView>
      
      {renderBottomActions()}
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
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: spacing.lg,
    paddingVertical: spacing.md,
    ...PlatformUtils.getElevationStyle(1),
    backgroundColor: colors.background.primary,
  },
  backButton: {
    padding: spacing.sm,
    marginLeft: -spacing.sm,
  },
  headerActions: {
    flexDirection: 'row',
    gap: spacing.md,
  },
  actionButton: {
    padding: spacing.sm,
  },
  content: {
    flex: 1,
  },
  galleryContainer: {
    height: 250,
  },
  galleryImage: {
    width: PlatformUtils.screenDimensions.width,
    height: 250,
  },
  infoCard: {
    margin: spacing.lg,
  },
  skillHeader: {
    marginBottom: spacing.md,
  },
  skillTitleSection: {
    marginBottom: spacing.md,
  },
  categoryText: {
    ...typography.caption,
    color: colors.primary[600],
    fontWeight: '600',
    textTransform: 'uppercase',
    marginBottom: spacing.xs,
  },
  titleText: {
    ...typography.h2,
    color: colors.text.primary,
    marginBottom: spacing.sm,
  },
  ratingContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: spacing.xs,
  },
  ratingText: {
    ...typography.body2,
    color: colors.text.secondary,
  },
  priceSection: {
    alignItems: 'flex-end',
  },
  priceText: {
    ...typography.h2,
    color: colors.primary[600],
    fontWeight: '700',
  },
  durationText: {
    ...typography.caption,
    color: colors.text.secondary,
  },
  tagsContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: spacing.xs,
    marginBottom: spacing.md,
  },
  tag: {
    marginBottom: spacing.xs,
  },
  descriptionText: {
    ...typography.body1,
    color: colors.text.primary,
    lineHeight: 24,
  },
  readMoreText: {
    color: colors.primary[500],
    fontWeight: '600',
  },
  tabsContainer: {
    flexDirection: 'row',
    marginHorizontal: spacing.lg,
    marginVertical: spacing.md,
    backgroundColor: colors.background.secondary,
    borderRadius: 8,
    padding: spacing.xs,
  },
  tab: {
    flex: 1,
    paddingVertical: spacing.sm,
    alignItems: 'center',
    borderRadius: 6,
  },
  activeTab: {
    backgroundColor: colors.primary[500],
  },
  tabText: {
    ...typography.body2,
    color: colors.text.secondary,
    fontWeight: '500',
  },
  activeTabText: {
    color: colors.background.primary,
    fontWeight: '600',
  },
  tabContent: {
    paddingHorizontal: spacing.lg,
    paddingBottom: spacing.xl,
  },
  sectionCard: {
    marginBottom: spacing.lg,
  },
  sectionTitle: {
    ...typography.h4,
    color: colors.text.primary,
    marginBottom: spacing.md,
    fontWeight: '600',
  },
  listItem: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    marginBottom: spacing.sm,
    gap: spacing.sm,
  },
  listItemText: {
    ...typography.body1,
    color: colors.text.primary,
    flex: 1,
    lineHeight: 20,
  },
  reviewCard: {
    marginBottom: spacing.md,
  },
  reviewHeader: {
    flexDirection: 'row',
    marginBottom: spacing.sm,
  },
  reviewInfo: {
    marginLeft: spacing.sm,
    flex: 1,
  },
  reviewerName: {
    ...typography.body2,
    color: colors.text.primary,
    fontWeight: '600',
    marginBottom: spacing.xs,
  },
  reviewRating: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: spacing.xs,
  },
  reviewDate: {
    ...typography.caption,
    color: colors.text.secondary,
    marginLeft: spacing.sm,
  },
  reviewComment: {
    ...typography.body1,
    color: colors.text.primary,
    lineHeight: 20,
    marginBottom: spacing.sm,
  },
  verifiedBadge: {
    alignSelf: 'flex-start',
  },
  instructorCard: {
    marginBottom: spacing.lg,
  },
  instructorHeader: {
    flexDirection: 'row',
    marginBottom: spacing.lg,
  },
  instructorInfo: {
    marginLeft: spacing.md,
    flex: 1,
  },
  instructorName: {
    ...typography.h3,
    color: colors.text.primary,
    marginBottom: spacing.xs,
  },
  verifiedContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: spacing.sm,
    gap: spacing.xs,
  },
  verifiedText: {
    ...typography.caption,
    color: colors.primary[600],
    fontWeight: '600',
  },
  instructorBio: {
    ...typography.body1,
    color: colors.text.secondary,
    lineHeight: 20,
  },
  instructorStats: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    paddingVertical: spacing.lg,
    borderTopWidth: 1,
    borderBottomWidth: 1,
    borderColor: colors.background.secondary,
    marginBottom: spacing.md,
  },
  statItem: {
    alignItems: 'center',
  },
  statValue: {
    ...typography.h3,
    color: colors.text.primary,
    fontWeight: '700',
    marginBottom: spacing.xs,
  },
  statLabel: {
    ...typography.caption,
    color: colors.text.secondary,
  },
  responseTime: {
    ...typography.body2,
    color: colors.text.secondary,
    textAlign: 'center',
  },
  bottomActions: {
    flexDirection: 'row',
    padding: spacing.lg,
    gap: spacing.md,
    backgroundColor: colors.background.primary,
    ...PlatformUtils.getElevationStyle(4),
  },
  messageButton: {
    flex: 1,
  },
  bookButton: {
    flex: 2,
  },
  errorContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: spacing.xl,
  },
  errorText: {
    ...typography.h3,
    color: colors.text.primary,
    marginBottom: spacing.lg,
    textAlign: 'center',
  },
  errorButton: {
    paddingHorizontal: spacing.xl,
  },
});

export default SkillDetailScreen;
