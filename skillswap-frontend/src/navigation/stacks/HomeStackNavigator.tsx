import React from 'react';
import { createStackNavigator } from '@react-navigation/stack';
import { View, Text, StyleSheet } from 'react-native';
import { HomeStackParamList } from '../navigationTypes';
import { colors } from '@styles/theme';

const Stack = createStackNavigator<HomeStackParamList>();

// Placeholder screens
const HomeScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>🏠 Home</Text>
    <Text style={styles.subtitle}>Welcome to your SkillSwap dashboard</Text>
    <Text style={styles.description}>
      Here you'll see your recent activity, upcoming sessions, and personalized recommendations.
    </Text>
  </View>
);

const NotificationsScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>🔔 Notifications</Text>
    <Text style={styles.subtitle}>Stay updated with your activity</Text>
    <Text style={styles.description}>
      Match requests, session reminders, and other important updates.
    </Text>
  </View>
);

const SettingsScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>⚙️ Settings</Text>
    <Text style={styles.subtitle}>Customize your experience</Text>
    <Text style={styles.description}>
      Privacy, notifications, language, and account preferences.
    </Text>
  </View>
);

const HomeStackNavigator: React.FC = () => {
  return (
    <Stack.Navigator
      initialRouteName="Home"
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
        name="Home" 
        component={HomeScreen}
        options={{
          title: 'SkillSwap',
          headerStyle: {
            backgroundColor: colors.primary[500],
          },
        }}
      />
      <Stack.Screen 
        name="Notifications" 
        component={NotificationsScreen}
        options={{
          title: 'Notifications',
        }}
      />
      <Stack.Screen 
        name="Settings" 
        component={SettingsScreen}
        options={{
          title: 'Settings',
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

export default HomeStackNavigator;
