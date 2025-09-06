import React from 'react';
import { createDrawerNavigator } from '@react-navigation/drawer';
import { View, Text, StyleSheet } from 'react-native';
import { DrawerParamList } from './navigationTypes';
import { colors } from '@styles/theme';

// Import the main tab navigator
import MainTabNavigator from './MainTabNavigator';

const Drawer = createDrawerNavigator<DrawerParamList>();

// Placeholder screens for drawer-only items
const SettingsScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>⚙️ Settings</Text>
    <Text style={styles.subtitle}>App preferences and configuration</Text>
    <Text style={styles.description}>
      Account settings, notifications, privacy, language, and more.
    </Text>
  </View>
);

const HelpScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>❓ Help</Text>
    <Text style={styles.subtitle}>Get help and support</Text>
    <Text style={styles.description}>
      Frequently asked questions, tutorials, and contact support.
    </Text>
  </View>
);

const AboutScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>ℹ️ About</Text>
    <Text style={styles.subtitle}>About SkillSwap</Text>
    <Text style={styles.description}>
      Learn more about our mission, team, and how SkillSwap works.
    </Text>
  </View>
);

const LogoutScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>👋 Logout</Text>
    <Text style={styles.subtitle}>Sign out of your account</Text>
    <Text style={styles.description}>
      You'll be redirected to the login screen.
    </Text>
  </View>
);

const DrawerNavigator: React.FC = () => {
  return (
    <Drawer.Navigator
      initialRouteName="MainTabs"
      screenOptions={{
        headerStyle: {
          backgroundColor: colors.primary[500],
        },
        headerTintColor: colors.background.primary,
        headerTitleStyle: {
          fontWeight: '600',
          fontSize: 18,
        },
        drawerStyle: {
          backgroundColor: colors.background.primary,
          width: 280,
        },
        drawerActiveTintColor: colors.primary[500],
        drawerInactiveTintColor: colors.text.secondary,
        drawerLabelStyle: {
          fontWeight: '500',
          fontSize: 16,
        },
        drawerItemStyle: {
          marginVertical: 4,
        },
      }}>
      <Drawer.Screen
        name="MainTabs"
        component={MainTabNavigator}
        options={{
          title: '🏠 Home',
          headerShown: false, // MainTabNavigator handles its own headers
        }}
      />
      <Drawer.Screen
        name="Settings"
        component={SettingsScreen}
        options={{
          title: '⚙️ Settings',
        }}
      />
      <Drawer.Screen
        name="Help"
        component={HelpScreen}
        options={{
          title: '❓ Help & Support',
        }}
      />
      <Drawer.Screen
        name="About"
        component={AboutScreen}
        options={{
          title: 'ℹ️ About SkillSwap',
        }}
      />
      <Drawer.Screen
        name="Logout"
        component={LogoutScreen}
        options={{
          title: '👋 Sign Out',
        }}
      />
    </Drawer.Navigator>
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

export default DrawerNavigator;
