import React from 'react';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { MainTabParamList } from './navigationTypes';
import { colors } from '@styles/theme';

// Import stack navigators
import HomeStackNavigator from './stacks/HomeStackNavigator';
import ExploreStackNavigator from './stacks/ExploreStackNavigator';
import MatchesStackNavigator from './stacks/MatchesStackNavigator';
import SessionsStackNavigator from './stacks/SessionsStackNavigator';
import ProfileStackNavigator from './stacks/ProfileStackNavigator';

// Icon component (will be replaced with react-native-vector-icons)
const TabIcon = ({ name, focused }: { name: string; focused: boolean }) => {
  return null; // Placeholder until icons are set up
};

const Tab = createBottomTabNavigator<MainTabParamList>();

const MainTabNavigator: React.FC = () => {
  return (
    <Tab.Navigator
      initialRouteName="HomeTab"
      screenOptions={{
        headerShown: false, // Each stack will handle its own headers
        tabBarStyle: {
          backgroundColor: colors.background.primary,
          borderTopColor: colors.border.primary,
          borderTopWidth: 1,
          paddingTop: 8,
          paddingBottom: 8,
          height: 60,
        },
        tabBarActiveTintColor: colors.primary[500],
        tabBarInactiveTintColor: colors.text.secondary,
        tabBarLabelStyle: {
          fontSize: 12,
          fontWeight: '500',
          marginTop: 4,
        },
      }}>
      <Tab.Screen
        name="HomeTab"
        component={HomeStackNavigator}
        options={{
          title: 'Home',
          tabBarIcon: ({ focused }) => (
            <TabIcon name="home" focused={focused} />
          ),
        }}
      />
      <Tab.Screen
        name="ExploreTab"
        component={ExploreStackNavigator}
        options={{
          title: 'Explore',
          tabBarIcon: ({ focused }) => (
            <TabIcon name="explore" focused={focused} />
          ),
        }}
      />
      <Tab.Screen
        name="MatchesTab"
        component={MatchesStackNavigator}
        options={{
          title: 'Matches',
          tabBarIcon: ({ focused }) => (
            <TabIcon name="people" focused={focused} />
          ),
          tabBarBadge: undefined, // Will show match request count
        }}
      />
      <Tab.Screen
        name="SessionsTab"
        component={SessionsStackNavigator}
        options={{
          title: 'Sessions',
          tabBarIcon: ({ focused }) => (
            <TabIcon name="video-call" focused={focused} />
          ),
          tabBarBadge: undefined, // Will show upcoming session count
        }}
      />
      <Tab.Screen
        name="ProfileTab"
        component={ProfileStackNavigator}
        options={{
          title: 'Profile',
          tabBarIcon: ({ focused }) => (
            <TabIcon name="person" focused={focused} />
          ),
        }}
      />
    </Tab.Navigator>
  );
};

export default MainTabNavigator;
