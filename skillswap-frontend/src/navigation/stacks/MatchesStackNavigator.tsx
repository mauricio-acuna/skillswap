import React from 'react';
import { createStackNavigator } from '@react-navigation/stack';
import { View, Text, StyleSheet } from 'react-native';
import { MatchesStackParamList } from '../navigationTypes';
import { colors } from '@styles/theme';

const Stack = createStackNavigator<MatchesStackParamList>();

// Placeholder screens
const MatchesScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>🤝 Matches</Text>
    <Text style={styles.subtitle}>Your skill exchange connections</Text>
    <Text style={styles.description}>
      Current matches, pending requests, and people you can learn from or teach.
    </Text>
  </View>
);

const DiscoverMatchesScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>✨ Discover</Text>
    <Text style={styles.subtitle}>Find new learning partners</Text>
    <Text style={styles.description}>
      Swipe through potential matches based on your skills and interests.
    </Text>
  </View>
);

const MatchDetailScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>📋 Match Details</Text>
    <Text style={styles.subtitle}>Review this connection</Text>
    <Text style={styles.description}>
      See shared skills, compatibility score, and available session times.
    </Text>
  </View>
);

const SendMatchRequestScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>📤 Send Request</Text>
    <Text style={styles.subtitle}>Connect with this person</Text>
    <Text style={styles.description}>
      Send a personalized message explaining what you'd like to learn or teach.
    </Text>
  </View>
);

const MatchRequestsScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>📬 Requests</Text>
    <Text style={styles.subtitle}>Pending match requests</Text>
    <Text style={styles.description}>
      People who want to connect with you for skill exchange.
    </Text>
  </View>
);

const MatchingPreferencesScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>⚙️ Preferences</Text>
    <Text style={styles.subtitle}>Customize your matching</Text>
    <Text style={styles.description}>
      Set your preferences for matching: location, skill level, age range, etc.
    </Text>
  </View>
);

const MatchesStackNavigator: React.FC = () => {
  return (
    <Stack.Navigator
      initialRouteName="Matches"
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
        name="Matches" 
        component={MatchesScreen}
        options={{
          title: 'My Matches',
        }}
      />
      <Stack.Screen 
        name="DiscoverMatches" 
        component={DiscoverMatchesScreen}
        options={{
          title: 'Discover People',
        }}
      />
      <Stack.Screen 
        name="MatchDetail" 
        component={MatchDetailScreen}
        options={{
          title: 'Match Details',
        }}
      />
      <Stack.Screen 
        name="SendMatchRequest" 
        component={SendMatchRequestScreen}
        options={{
          title: 'Send Request',
        }}
      />
      <Stack.Screen 
        name="MatchRequests" 
        component={MatchRequestsScreen}
        options={{
          title: 'Match Requests',
        }}
      />
      <Stack.Screen 
        name="MatchingPreferences" 
        component={MatchingPreferencesScreen}
        options={{
          title: 'Matching Settings',
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

export default MatchesStackNavigator;
