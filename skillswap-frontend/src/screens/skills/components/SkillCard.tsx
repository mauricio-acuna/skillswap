import React from 'react';
import {
  View,
  Text,
  Image,
  TouchableOpacity,
  StyleSheet,
  Platform,
  ViewStyle,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { Card, Avatar, Badge } from '@components/ui';
import { colors, typography, spacing } from '@styles/theme';
import { Skill } from '../../../services';

interface SkillCardProps {
  skill: Skill;
  onPress: () => void;
  onInstructorPress: () => void;
  style?: ViewStyle;
  variant?: 'default' | 'compact' | 'featured';
}

export const SkillCard: React.FC<SkillCardProps> = ({
  skill,
  onPress,
  onInstructorPress,
  style,
  variant = 'default',
}) => {
  const getAvailabilityColor = (availability: string) => {
    switch (availability) {
      case 'available':
        return colors.success;
      case 'busy':
        return colors.warning;
      case 'offline':
        return colors.error;
      default:
        return colors.text.secondary;
    }
  };

  const getAvailabilityText = (availability: string) => {
    switch (availability) {
      case 'available':
        return 'Available';
      case 'busy':
        return 'Busy';
      case 'offline':
        return 'Offline';
      default:
        return 'Unknown';
    }
  };

  const formatPrice = (price: number) => {
    return `$${price}/hr`;
  };

  const formatDuration = (duration: number) => {
    if (duration < 60) {
      return `${duration}min`;
    } else {
      const hours = Math.floor(duration / 60);
      const minutes = duration % 60;
      return minutes > 0 ? `${hours}h ${minutes}min` : `${hours}h`;
    }
  };

  const renderTags = () => {
    const visibleTags = skill.tags.slice(0, 3);
    const remainingCount = skill.tags.length - 3;

    return (
      <View style={styles.tagsContainer}>
        {visibleTags.map((tag: string, index: number) => (
          <View key={index} style={styles.tag}>
            <Text style={styles.tagText}>{tag}</Text>
          </View>
        ))}
        {remainingCount > 0 && (
          <View style={styles.tag}>
            <Text style={styles.tagText}>+{remainingCount}</Text>
          </View>
        )}
      </View>
    );
  };

  const renderInstructor = () => (
    <TouchableOpacity
      style={styles.instructorContainer}
      onPress={onInstructorPress}
      activeOpacity={0.7}
    >
      <Avatar
        source={skill.instructor.avatar ? { uri: skill.instructor.avatar } : undefined}
        name={skill.instructor.name}
        size="small"
        style={styles.instructorAvatar}
      />
      <View style={styles.instructorInfo}>
        <View style={styles.instructorNameRow}>
          <Text style={styles.instructorName} numberOfLines={1}>
            {skill.instructor.name}
          </Text>
          {skill.instructor.verifiedInstructor && (
            <Ionicons
              name="checkmark-circle"
              size={14}
              color={colors.primary[500]}
              style={styles.verifiedIcon}
            />
          )}
        </View>
        <View style={styles.ratingContainer}>
          <Ionicons
            name="star"
            size={12}
            color={colors.warning[500]}
          />
          <Text style={styles.ratingText}>
            {skill.instructor.rating.toFixed(1)}
          </Text>
        </View>
      </View>
    </TouchableOpacity>
  );

  const renderFeaturedBadge = () => {
    if (!skill.featured) return null;

    return (
      <View style={styles.featuredBadge}>
        <Ionicons
          name="star"
          size={12}
          color={colors.warning[500]}
        />
        <Text style={styles.featuredText}>Featured</Text>
      </View>
    );
  };

  const cardContent = (
    <View style={styles.content}>
      {/* Header with featured badge and availability */}
      <View style={styles.header}>
        {renderFeaturedBadge()}
        <View style={styles.availabilityContainer}>
          <View
            style={[
              styles.availabilityDot,
              { backgroundColor: getAvailabilityColor(skill.availability) }
            ]}
          />
          <Text style={styles.availabilityText}>
            {getAvailabilityText(skill.availability)}
          </Text>
        </View>
      </View>

      {/* Skill Image */}
      {skill.images.length > 0 && (
        <View style={styles.imageContainer}>
          <Image
            source={{ uri: skill.images[0] }}
            style={styles.skillImage}
            resizeMode="cover"
          />
        </View>
      )}

      {/* Skill Info */}
      <View style={styles.skillInfo}>
        <Text style={styles.categoryText}>{skill.category}</Text>
        <Text style={styles.titleText} numberOfLines={2}>
          {skill.title}
        </Text>
        <Text style={styles.descriptionText} numberOfLines={2}>
          {skill.description}
        </Text>
      </View>

      {/* Tags */}
      {renderTags()}

      {/* Bottom Section */}
      <View style={styles.bottomSection}>
        {/* Instructor */}
        {renderInstructor()}

        {/* Price and Stats */}
        <View style={styles.statsContainer}>
          <View style={styles.priceContainer}>
            <Text style={styles.priceText}>{formatPrice(skill.price)}</Text>
            <Text style={styles.durationText}>{formatDuration(skill.duration)}</Text>
          </View>
          
          <View style={styles.ratingsContainer}>
            <Ionicons
              name="star"
              size={14}
              color={colors.warning[500]}
            />
            <Text style={styles.ratingValue}>
              {skill.rating.toFixed(1)}
            </Text>
            <Text style={styles.reviewCount}>
              ({skill.totalReviews})
            </Text>
          </View>
        </View>
      </View>
    </View>
  );

  const cardStyles = [
    styles.card,
    Platform.OS === 'ios' ? styles.cardIOS : styles.cardAndroid,
    style,
  ];

  if (variant === 'featured') {
    cardStyles.push(styles.featuredCard);
  }

  return (
    <Card
      variant={variant === 'featured' ? 'elevated' : 'default'}
      onPress={onPress}
      style={StyleSheet.flatten(cardStyles)}
    >
      {cardContent}
    </Card>
  );
};

const styles = StyleSheet.create({
  card: {
    padding: spacing.lg,
  },
  // Platform-specific card styles
  cardIOS: {
    shadowColor: colors.shadow.primary,
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 8,
  },
  cardAndroid: {
    elevation: 2,
  },
  featuredCard: {
    borderWidth: 1,
    borderColor: colors.primary[200],
    ...(Platform.OS === 'ios' ? {
      shadowColor: colors.primary[500],
      shadowOffset: { width: 0, height: 4 },
      shadowOpacity: 0.15,
      shadowRadius: 12,
    } : {
      elevation: 4,
    }),
  },
  content: {
    flex: 1,
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: spacing.md,
  },
  featuredBadge: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.warning[100],
    paddingHorizontal: spacing.sm,
    paddingVertical: spacing.xs,
    borderRadius: 12,
  },
  featuredText: {
    ...typography.caption,
    color: colors.warning[700],
    fontWeight: '600',
    marginLeft: spacing.xs,
  },
  availabilityContainer: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  availabilityDot: {
    width: 8,
    height: 8,
    borderRadius: 4,
    marginRight: spacing.xs,
  },
  availabilityText: {
    ...typography.caption,
    color: colors.text.secondary,
    fontWeight: '500',
  },
  imageContainer: {
    borderRadius: 8,
    overflow: 'hidden',
    marginBottom: spacing.md,
  },
  skillImage: {
    width: '100%',
    height: 120,
    // Platform-specific image styles
    ...Platform.select({
      android: {
        borderRadius: 8,
      },
    }),
  },
  skillInfo: {
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
    ...typography.h4,
    color: colors.text.primary,
    marginBottom: spacing.xs,
  },
  descriptionText: {
    ...typography.body2,
    color: colors.text.secondary,
    lineHeight: 20,
  },
  tagsContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    marginBottom: spacing.lg,
    gap: spacing.xs,
  },
  tag: {
    backgroundColor: colors.background.secondary,
    paddingHorizontal: spacing.sm,
    paddingVertical: spacing.xs,
    borderRadius: 6,
  },
  tagText: {
    ...typography.caption,
    color: colors.text.primary,
    fontWeight: '500',
  },
  bottomSection: {
    borderTopWidth: 1,
    borderTopColor: colors.border.secondary,
    paddingTop: spacing.md,
  },
  instructorContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: spacing.md,
  },
  instructorAvatar: {
    marginRight: spacing.sm,
  },
  instructorInfo: {
    flex: 1,
  },
  instructorNameRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: spacing.xs,
  },
  instructorName: {
    ...typography.body2,
    color: colors.text.primary,
    fontWeight: '600',
    flex: 1,
  },
  verifiedIcon: {
    marginLeft: spacing.xs,
  },
  ratingContainer: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  ratingText: {
    ...typography.caption,
    color: colors.text.secondary,
    marginLeft: spacing.xs,
  },
  statsContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  priceContainer: {
    alignItems: 'flex-start',
  },
  priceText: {
    ...typography.h4,
    color: colors.primary[600],
    fontWeight: '700',
  },
  durationText: {
    ...typography.caption,
    color: colors.text.secondary,
  },
  ratingsContainer: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  ratingValue: {
    ...typography.body2,
    color: colors.text.primary,
    fontWeight: '600',
    marginLeft: spacing.xs,
  },
  reviewCount: {
    ...typography.caption,
    color: colors.text.secondary,
    marginLeft: spacing.xs,
  },
});

export default SkillCard;
