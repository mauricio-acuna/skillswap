import React from 'react';
import { createStackNavigator } from '@react-navigation/stack';
import { View, Text, StyleSheet } from 'react-native';
import { ExploreStackParamList } from '../navigationTypes';
import { colors } from '@styles/theme';

const Stack = createStackNavigator<ExploreStackParamList>();

// Placeholder screens
const ExploreScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>🔍 Explore</Text>
    <Text style={styles.subtitle}>Discover new skills and people</Text>
    <Text style={styles.description}>
      Browse skills, explore categories, and find interesting people to learn from.
    </Text>
  </View>
);

const SkillCategoriesScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>📚 Skill Categories</Text>
    <Text style={styles.subtitle}>Explore by category</Text>
    <Text style={styles.description}>
      Technology, Art, Music, Language, Sports, Cooking, and more...
    </Text>
  </View>
);

const SkillDetailScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>🎯 Skill Detail</Text>
    <Text style={styles.subtitle}>Learn more about this skill</Text>
    <Text style={styles.description}>
      Detailed information, available teachers, difficulty level, and more.
    </Text>
  </View>
);

const UserProfileScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>👤 User Profile</Text>
    <Text style={styles.subtitle}>View member profile</Text>
    <Text style={styles.description}>
      Skills, experience, ratings, and available session times.
    </Text>
  </View>
);

const ExploreStackNavigator: React.FC = () => {
  return (
    <Stack.Navigator
      initialRouteName="Explore"
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
        name="Explore" 
        component={ExploreScreen}
        options={{
          title: 'Explore Skills',
        }}
      />
      <Stack.Screen 
        name="SkillCategories" 
        component={SkillCategoriesScreen}
        options={{
          title: 'Categories',
        }}
      />
      <Stack.Screen 
        name="SkillDetail" 
        component={SkillDetailScreen}
        options={{
          title: 'Skill Details',
        }}
      />
      <Stack.Screen 
        name="UserProfile" 
        component={UserProfileScreen}
        options={{
          title: 'User Profile',
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

export default ExploreStackNavigator;
