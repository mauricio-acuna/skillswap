import React from 'react';
import { createStackNavigator } from '@react-navigation/stack';
import { View, Text, StyleSheet } from 'react-native';
import { SessionsStackParamList } from '../navigationTypes';
import { colors } from '@styles/theme';

const Stack = createStackNavigator<SessionsStackParamList>();

// Placeholder screens
const SessionsScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>📞 Sessions</Text>
    <Text style={styles.subtitle}>Your skill exchange sessions</Text>
    <Text style={styles.description}>
      Upcoming sessions, past sessions, and session history with ratings.
    </Text>
  </View>
);

const SessionDetailScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>📋 Session Details</Text>
    <Text style={styles.subtitle}>Review session information</Text>
    <Text style={styles.description}>
      Date, time, participants, skills to be exchanged, and session notes.
    </Text>
  </View>
);

const ScheduleSessionScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>📅 Schedule Session</Text>
    <Text style={styles.subtitle}>Plan your skill exchange</Text>
    <Text style={styles.description}>
      Select date, time, duration, and confirm details with your match.
    </Text>
  </View>
);

const JoinSessionScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>🚀 Join Session</Text>
    <Text style={styles.subtitle}>Prepare for your session</Text>
    <Text style={styles.description}>
      Check your connection, test camera and microphone before starting.
    </Text>
  </View>
);

const ActiveSessionScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>🎥 Live Session</Text>
    <Text style={styles.subtitle}>You're in a session</Text>
    <Text style={styles.description}>
      Video call interface with controls for mute, camera, screen share, and chat.
    </Text>
  </View>
);

const SessionFeedbackScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>⭐ Rate Session</Text>
    <Text style={styles.subtitle}>How was your experience?</Text>
    <Text style={styles.description}>
      Rate your session, leave feedback, and help improve the community.
    </Text>
  </View>
);

const VideoCallLobbyScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>📹 Video Lobby</Text>
    <Text style={styles.subtitle}>Preparing video call</Text>
    <Text style={styles.description}>
      Test your camera and microphone settings before joining the session.
    </Text>
  </View>
);

const VideoCallScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>📹 Video Call</Text>
    <Text style={styles.subtitle}>Live video session</Text>
    <Text style={styles.description}>
      Full-screen video call with controls and chat functionality.
    </Text>
  </View>
);

const CallEndedScreen = () => (
  <View style={styles.container}>
    <Text style={styles.title}>✅ Call Ended</Text>
    <Text style={styles.subtitle}>Session completed</Text>
    <Text style={styles.description}>
      Session summary, duration, and option to provide feedback.
    </Text>
  </View>
);

const SessionsStackNavigator: React.FC = () => {
  return (
    <Stack.Navigator
      initialRouteName="Sessions"
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
        name="Sessions" 
        component={SessionsScreen}
        options={{
          title: 'My Sessions',
        }}
      />
      <Stack.Screen 
        name="SessionDetail" 
        component={SessionDetailScreen}
        options={{
          title: 'Session Details',
        }}
      />
      <Stack.Screen 
        name="ScheduleSession" 
        component={ScheduleSessionScreen}
        options={{
          title: 'Schedule Session',
        }}
      />
      <Stack.Screen 
        name="JoinSession" 
        component={JoinSessionScreen}
        options={{
          title: 'Join Session',
        }}
      />
      <Stack.Screen 
        name="ActiveSession" 
        component={ActiveSessionScreen}
        options={{
          headerShown: false, // Full screen video interface
        }}
      />
      <Stack.Screen 
        name="SessionFeedback" 
        component={SessionFeedbackScreen}
        options={{
          title: 'Rate Session',
          headerLeft: () => null, // Prevent going back until feedback is given
        }}
      />
      <Stack.Screen 
        name="VideoCallLobby" 
        component={VideoCallLobbyScreen}
        options={{
          title: 'Video Setup',
        }}
      />
      <Stack.Screen 
        name="VideoCall" 
        component={VideoCallScreen}
        options={{
          headerShown: false, // Full screen video call
        }}
      />
      <Stack.Screen 
        name="CallEnded" 
        component={CallEndedScreen}
        options={{
          title: 'Call Ended',
          headerLeft: () => null, // Prevent going back
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

export default SessionsStackNavigator;
