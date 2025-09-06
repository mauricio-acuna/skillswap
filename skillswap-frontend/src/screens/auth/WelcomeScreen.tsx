import React from 'react';
import {
  View,
  Text,
  StyleSheet,
  Image,
  SafeAreaView,
} from 'react-native';
import { AuthStackScreenProps } from '@navigation/navigationTypes';
import { PrimaryButton } from '@components/forms';
import { colors, typography, spacing } from '@styles/theme';

type WelcomeScreenProps = AuthStackScreenProps<'Welcome'>;

export const WelcomeScreen: React.FC<WelcomeScreenProps> = ({ navigation }) => {
  const handleGetStarted = () => {
    navigation.navigate('Register');
  };

  const handleSignIn = () => {
    navigation.navigate('Login');
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.content}>
        {/* Logo/Illustration Section */}
        <View style={styles.heroSection}>
          <View style={styles.logoContainer}>
            {/* TODO: Replace with actual logo/illustration */}
            <Text style={styles.logoEmoji}>🔄</Text>
          </View>
          
          <Text style={styles.title}>Welcome to SkillSwap</Text>
          <Text style={styles.subtitle}>
            Connect, Learn, and Share Skills with People Around the World
          </Text>
        </View>

        {/* Features Section */}
        <View style={styles.featuresSection}>
          <View style={styles.feature}>
            <Text style={styles.featureIcon}>🎯</Text>
            <Text style={styles.featureTitle}>Learn New Skills</Text>
            <Text style={styles.featureDescription}>
              Discover and master new skills from experienced community members
            </Text>
          </View>

          <View style={styles.feature}>
            <Text style={styles.featureIcon}>👥</Text>
            <Text style={styles.featureTitle}>Share Your Expertise</Text>
            <Text style={styles.featureDescription}>
              Teach others and earn credits while building meaningful connections
            </Text>
          </View>

          <View style={styles.feature}>
            <Text style={styles.featureIcon}>📞</Text>
            <Text style={styles.featureTitle}>Video Sessions</Text>
            <Text style={styles.featureDescription}>
              Participate in live video sessions for real-time skill exchange
            </Text>
          </View>
        </View>

        {/* Action Buttons */}
        <View style={styles.buttonSection}>
          <PrimaryButton
            title="Get Started"
            onPress={handleGetStarted}
            style={styles.primaryButton}
          />
          
          <PrimaryButton
            title="I Already Have an Account"
            onPress={handleSignIn}
            variant="outline"
            style={styles.secondaryButton}
          />
        </View>

        {/* Footer */}
        <View style={styles.footer}>
          <Text style={styles.footerText}>
            By continuing, you agree to our Terms of Service and Privacy Policy
          </Text>
        </View>
      </View>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background.primary,
  },
  content: {
    flex: 1,
    paddingHorizontal: spacing.lg,
    paddingVertical: spacing.xl,
  },
  heroSection: {
    flex: 0.4,
    alignItems: 'center',
    justifyContent: 'center',
  },
  logoContainer: {
    width: 120,
    height: 120,
    borderRadius: 60,
    backgroundColor: colors.primary[50],
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: spacing.xl,
    // Add subtle shadow
    shadowColor: colors.primary[500],
    shadowOffset: {
      width: 0,
      height: 4,
    },
    shadowOpacity: 0.1,
    shadowRadius: 8,
    elevation: 4,
  },
  logoEmoji: {
    fontSize: 48,
  },
  title: {
    ...typography.h1,
    color: colors.text.primary,
    textAlign: 'center',
    marginBottom: spacing.md,
    fontWeight: '700',
  },
  subtitle: {
    ...typography.body1,
    color: colors.text.secondary,
    textAlign: 'center',
    lineHeight: 24,
    paddingHorizontal: spacing.md,
  },
  featuresSection: {
    flex: 0.4,
    paddingVertical: spacing.lg,
  },
  feature: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    marginBottom: spacing.lg,
    paddingHorizontal: spacing.sm,
  },
  featureIcon: {
    fontSize: 24,
    marginRight: spacing.md,
    marginTop: 2,
  },
  featureTitle: {
    ...typography.subtitle1,
    color: colors.text.primary,
    fontWeight: '600',
    marginBottom: spacing.xs,
    flex: 1,
  },
  featureDescription: {
    ...typography.body2,
    color: colors.text.secondary,
    lineHeight: 20,
    flex: 1,
  },
  buttonSection: {
    flex: 0.15,
    justifyContent: 'center',
  },
  primaryButton: {
    marginBottom: spacing.md,
  },
  secondaryButton: {
    marginBottom: spacing.lg,
  },
  footer: {
    flex: 0.05,
    justifyContent: 'flex-end',
    alignItems: 'center',
  },
  footerText: {
    ...typography.caption,
    color: colors.text.secondary,
    textAlign: 'center',
    lineHeight: 16,
  },
});

export default WelcomeScreen;
