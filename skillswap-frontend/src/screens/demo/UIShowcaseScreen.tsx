import React, { useState } from 'react';
import {
  View,
  Text,
  ScrollView,
  StyleSheet,
  SafeAreaView,
} from 'react-native';
import {
  Button,
  Card,
  Avatar,
  Badge,
  Loading,
  LoadingDots,
  LoadingSkeleton,
} from '@components/ui';
import { colors, typography, spacing } from '@styles/theme';

export const UIShowcaseScreen: React.FC = () => {
  const [loading, setLoading] = useState(false);

  const handleButtonPress = (buttonType: string) => {
    console.log(`${buttonType} button pressed`);
    setLoading(true);
    setTimeout(() => setLoading(false), 2000);
  };

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView style={styles.scrollView} showsVerticalScrollIndicator={false}>
        <Text style={styles.title}>UI Components Showcase</Text>
        
        {/* Buttons Section */}
        <Card variant="elevated" padding="large" margin="medium">
          <Text style={styles.sectionTitle}>Buttons</Text>
          
          <View style={styles.buttonRow}>
            <Button
              title="Primary"
              onPress={() => handleButtonPress('Primary')}
              variant="primary"
              size="medium"
              style={styles.button}
            />
            <Button
              title="Secondary"
              onPress={() => handleButtonPress('Secondary')}
              variant="secondary"
              size="medium"
              style={styles.button}
            />
          </View>

          <View style={styles.buttonRow}>
            <Button
              title="Outline"
              onPress={() => handleButtonPress('Outline')}
              variant="outline"
              size="medium"
              style={styles.button}
            />
            <Button
              title="Ghost"
              onPress={() => handleButtonPress('Ghost')}
              variant="ghost"
              size="medium"
              style={styles.button}
            />
          </View>

          <Button
            title="Loading Button"
            onPress={() => handleButtonPress('Loading')}
            loading={loading}
            fullWidth
            style={styles.fullButton}
          />
        </Card>

        {/* Avatars Section */}
        <Card variant="elevated" padding="large" margin="medium">
          <Text style={styles.sectionTitle}>Avatars</Text>
          
          <View style={styles.avatarRow}>
            <Avatar name="John Doe" size="small" />
            <Avatar name="Jane Smith" size="medium" />
            <Avatar name="Bob Johnson" size="large" />
            <Avatar 
              name="Alice Brown" 
              size="xl" 
              badge={<Badge count={5} size="small" />}
            />
          </View>

          <View style={styles.avatarRow}>
            <Avatar name="Test User" variant="square" size="medium" />
            <Avatar name="Demo User" variant="rounded" size="medium" />
            <Avatar name="Sample User" variant="circular" size="medium" />
          </View>
        </Card>

        {/* Badges Section */}
        <Card variant="elevated" padding="large" margin="medium">
          <Text style={styles.sectionTitle}>Badges</Text>
          
          <View style={styles.badgeRow}>
            <Badge label="New" variant="success" />
            <Badge count={5} variant="error" />
            <Badge count={99} variant="warning" />
            <Badge count={150} maxCount={99} variant="info" />
          </View>

          <View style={styles.badgeRow}>
            <Badge label="Premium" variant="secondary" size="large" />
            <Badge count={0} showZero variant="default" />
            <Badge label="Hot" variant="error" shape="pill" />
          </View>
        </Card>

        {/* Cards Section */}
        <Card variant="elevated" padding="large" margin="medium">
          <Text style={styles.sectionTitle}>Cards</Text>
          
          <Card variant="outlined" padding="medium" margin="small">
            <Text style={styles.cardContent}>Outlined Card</Text>
          </Card>
          
          <Card variant="filled" padding="medium" margin="small">
            <Text style={styles.cardContent}>Filled Card</Text>
          </Card>
          
          <Card 
            variant="elevated" 
            padding="medium" 
            margin="small"
            onPress={() => console.log('Card pressed')}
          >
            <Text style={styles.cardContent}>Pressable Card</Text>
          </Card>
        </Card>

        {/* Loading Section */}
        <Card variant="elevated" padding="large" margin="medium">
          <Text style={styles.sectionTitle}>Loading States</Text>
          
          <View style={styles.loadingSection}>
            <Loading 
              visible={true} 
              message="Loading..." 
              overlay={false}
              style={styles.loadingDemo}
            />
            
            <LoadingDots visible={true} />
            
            <View style={styles.skeletonDemo}>
              <Text style={styles.subTitle}>Skeleton Loading:</Text>
              <LoadingSkeleton height={16} style={{ marginBottom: 8 }} />
              <LoadingSkeleton height={16} width="80%" style={{ marginBottom: 8 }} />
              <LoadingSkeleton height={16} width="60%" />
            </View>
          </View>
        </Card>

        {/* Skills Preview */}
        <Card variant="elevated" padding="large" margin="medium">
          <Text style={styles.sectionTitle}>Skills Preview Card</Text>
          
          <View style={styles.skillCard}>
            <View style={styles.skillHeader}>
              <Avatar name="Sarah Wilson" size="medium" />
              <View style={styles.skillInfo}>
                <Text style={styles.skillTitle}>React Native Development</Text>
                <Text style={styles.skillInstructor}>by Sarah Wilson</Text>
              </View>
              <Badge label="Pro" variant="success" />
            </View>
            
            <Text style={styles.skillDescription}>
              Learn to build amazing mobile apps with React Native. 
              Perfect for beginners and intermediate developers.
            </Text>
            
            <View style={styles.skillFooter}>
              <Text style={styles.skillPrice}>$25/hour</Text>
              <Button
                title="Book Session"
                onPress={() => console.log('Book session')}
                size="small"
                variant="primary"
              />
            </View>
          </View>
        </Card>
      </ScrollView>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background.secondary,
  },
  scrollView: {
    flex: 1,
  },
  title: {
    ...typography.h1,
    color: colors.text.primary,
    textAlign: 'center',
    marginVertical: spacing.xl,
  },
  sectionTitle: {
    ...typography.h3,
    color: colors.text.primary,
    marginBottom: spacing.lg,
  },
  subTitle: {
    ...typography.subtitle1,
    color: colors.text.secondary,
    marginBottom: spacing.sm,
  },
  buttonRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: spacing.md,
  },
  button: {
    flex: 1,
    marginHorizontal: spacing.xs,
  },
  fullButton: {
    marginTop: spacing.sm,
  },
  avatarRow: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    alignItems: 'center',
    marginBottom: spacing.lg,
  },
  badgeRow: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    alignItems: 'center',
    marginBottom: spacing.lg,
  },
  cardContent: {
    ...typography.body1,
    color: colors.text.primary,
    textAlign: 'center',
  },
  loadingSection: {
    alignItems: 'center',
  },
  loadingDemo: {
    marginBottom: spacing.lg,
  },
  skeletonDemo: {
    width: '100%',
    marginTop: spacing.lg,
  },
  skillCard: {
    padding: spacing.md,
  },
  skillHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: spacing.md,
  },
  skillInfo: {
    flex: 1,
    marginLeft: spacing.md,
  },
  skillTitle: {
    ...typography.subtitle1,
    color: colors.text.primary,
    fontWeight: '600',
  },
  skillInstructor: {
    ...typography.body2,
    color: colors.text.secondary,
  },
  skillDescription: {
    ...typography.body2,
    color: colors.text.secondary,
    lineHeight: 20,
    marginBottom: spacing.lg,
  },
  skillFooter: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  skillPrice: {
    ...typography.subtitle1,
    color: colors.primary[500],
    fontWeight: '600',
  },
});

export default UIShowcaseScreen;
