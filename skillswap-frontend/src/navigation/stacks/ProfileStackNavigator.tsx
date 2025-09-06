import React from 'react';
import { createStackNavigator } from '@react-navigation/stack';
import { View, Text, StyleSheet } from 'react-native';
import { ProfileStackParamList } from '../navigationTypes';
import { colors } from '@styles/theme';

const Stack = createStackNavigator<ProfileStackParamList>();

// Placeholder screens
const ProfileScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>👤 Profile</Text>
    <Text style={styles.subtitle}>Your SkillSwap profile</Text>
    <Text style={styles.description}>
      Manage your skills, view your stats, credits, and account settings.
    </Text>
  </View>
);

const ProfileSettingsScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>⚙️ Profile Settings</Text>
    <Text style={styles.subtitle}>Customize your profile</Text>
    <Text style={styles.description}>
      Update your photo, bio, contact information, and visibility preferences.
    </Text>
  </View>
);

const SkillsOverviewScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>🎯 My Skills</Text>
    <Text style={styles.subtitle}>Skills you offer and want to learn</Text>
    <Text style={styles.description}>
      Manage your skill portfolio, levels, and verification status.
    </Text>
  </View>
);

const AddSkillScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>➕ Add Skill</Text>
    <Text style={styles.subtitle}>Add a new skill to your profile</Text>
    <Text style={styles.description}>
      Choose from categories, set your level, and describe your experience.
    </Text>
  </View>
);

const SkillVerificationScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>✅ Verify Skill</Text>
    <Text style={styles.subtitle}>Get your skill verified</Text>
    <Text style={styles.description}>
      Upload certificates, portfolios, or complete skill assessments.
    </Text>
  </View>
);

const CreditsOverviewScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>💰 Credits</Text>
    <Text style={styles.subtitle}>Your SkillSwap credit balance</Text>
    <Text style={styles.description}>
      View balance, recent transactions, and ways to earn more credits.
    </Text>
  </View>
);

const CreditHistoryScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>📊 Credit History</Text>
    <Text style={styles.subtitle}>All your credit transactions</Text>
    <Text style={styles.description}>
      Detailed history of earned and spent credits with dates and reasons.
    </Text>
  </View>
);

const TransferCreditsScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>💸 Transfer Credits</Text>
    <Text style={styles.subtitle}>Send credits to other users</Text>
    <Text style={styles.description}>
      Transfer credits to friends or as appreciation for great sessions.
    </Text>
  </View>
);

const EarnCreditsScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>💎 Earn Credits</Text>
    <Text style={styles.subtitle}>Ways to earn more credits</Text>
    <Text style={styles.description}>
      Complete sessions, refer friends, verify skills, and participate in challenges.
    </Text>
  </View>
);

const NotificationSettingsScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>🔔 Notifications</Text>
    <Text style={styles.subtitle}>Manage your notification preferences</Text>
    <Text style={styles.description}>
      Choose what notifications you want to receive and how.
    </Text>
  </View>
);

const PrivacySettingsScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>🔒 Privacy & Data</Text>
    <Text style={styles.subtitle}>Control your privacy settings</Text>
    <Text style={styles.description}>
      GDPR compliance, data export, and privacy preferences.
    </Text>
  </View>
);

const LanguageSettingsScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>🌍 Language</Text>
    <Text style={styles.subtitle}>Choose your language</Text>
    <Text style={styles.description}>
      Available in English, Spanish, French, German, and Italian.
    </Text>
  </View>
);

const AboutScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>ℹ️ About</Text>
    <Text style={styles.subtitle}>About SkillSwap</Text>
    <Text style={styles.description}>
      Version info, terms of service, privacy policy, and contact information.
    </Text>
  </View>
);

const HelpScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>❓ Help & Support</Text>
    <Text style={styles.subtitle}>Get help when you need it</Text>
    <Text style={styles.description}>
      FAQ, contact support, tutorials, and community guidelines.
    </Text>
  </View>
);

const ProfileStackNavigator: React.FC = () => {
  return (
    <Stack.Navigator
      initialRouteName="Profile"
      screenOptions={{
        headerStyle: {
          backgroundColor: colors.primary[500],
        },
        headerTintColor: colors.background.primary,
        headerTitleStyle: {
          fontWeight: '600',
          fontSize: 18,
        },
        cardStyle: {
          backgroundColor: colors.background.primary,
        },
      }}>
      <Stack.Screen 
        name="Profile" 
        component={ProfileScreen}
        options={{
          title: 'My Profile',
        }}
      />
      <Stack.Screen 
        name="ProfileSettings" 
        component={ProfileSettingsScreen}
        options={{
          title: 'Profile Settings',
        }}
      />
      <Stack.Screen 
        name="SkillsOverview" 
        component={SkillsOverviewScreen}
        options={{
          title: 'My Skills',
        }}
      />
      <Stack.Screen 
        name="AddSkill" 
        component={AddSkillScreen}
        options={{
          title: 'Add Skill',
        }}
      />
      <Stack.Screen 
        name="SkillVerification" 
        component={SkillVerificationScreen}
        options={{
          title: 'Verify Skill',
        }}
      />
      <Stack.Screen 
        name="CreditsOverview" 
        component={CreditsOverviewScreen}
        options={{
          title: 'My Credits',
        }}
      />
      <Stack.Screen 
        name="CreditHistory" 
        component={CreditHistoryScreen}
        options={{
          title: 'Credit History',
        }}
      />
      <Stack.Screen 
        name="TransferCredits" 
        component={TransferCreditsScreen}
        options={{
          title: 'Transfer Credits',
        }}
      />
      <Stack.Screen 
        name="EarnCredits" 
        component={EarnCreditsScreen}
        options={{
          title: 'Earn Credits',
        }}
      />
      <Stack.Screen 
        name="NotificationSettings" 
        component={NotificationSettingsScreen}
        options={{
          title: 'Notifications',
        }}
      />
      <Stack.Screen 
        name="PrivacySettings" 
        component={PrivacySettingsScreen}
        options={{
          title: 'Privacy & Data',
        }}
      />
      <Stack.Screen 
        name="LanguageSettings" 
        component={LanguageSettingsScreen}
        options={{
          title: 'Language',
        }}
      />
      <Stack.Screen 
        name="About" 
        component={AboutScreen}
        options={{
          title: 'About SkillSwap',
        }}
      />
      <Stack.Screen 
        name="Help" 
        component={HelpScreen}
        options={{
          title: 'Help & Support',
        }}
      />
    </Stack.Navigator>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 24,
    backgroundColor: colors.background.primary,
  },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    color: colors.primary[500],
    textAlign: 'center',
    marginBottom: 8,
  },
  subtitle: {
    fontSize: 18,
    color: colors.text.primary,
    textAlign: 'center',
    marginBottom: 16,
    fontWeight: '500',
  },
  description: {
    fontSize: 16,
    color: colors.text.secondary,
    textAlign: 'center',
    lineHeight: 24,
    marginHorizontal: 32,
  },
});

export default ProfileStackNavigator;
