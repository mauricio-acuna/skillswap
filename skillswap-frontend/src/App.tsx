import React from 'react';
import {
  SafeAreaView,
  StatusBar,
  StyleSheet,
  Text,
  View,
  ScrollView,
} from 'react-native';
import { colors } from './styles/theme';

const App: React.FC = () => {
  return (
    <SafeAreaView style={styles.container}>
      <StatusBar
        barStyle="dark-content"
        backgroundColor={colors.background.primary}
      />
      <ScrollView contentInsetAdjustmentBehavior="automatic">
        <View style={styles.content}>
          <Text style={styles.title}>🎯 SkillSwap</Text>
          <Text style={styles.subtitle}>React Native Mobile App</Text>
          
          <View style={styles.section}>
            <Text style={styles.sectionTitle}>✅ Project Initialization Complete</Text>
            <Text style={styles.description}>
              The SkillSwap frontend has been successfully initialized with:
            </Text>
            
            <View style={styles.featureList}>
              <Text style={styles.feature}>📱 React Native 0.72+ with TypeScript</Text>
              <Text style={styles.feature}>🎨 Complete UI Theme System</Text>
              <Text style={styles.feature}>🗂️ Redux Store Configuration</Text>
              <Text style={styles.feature}>📁 Professional Folder Structure</Text>
              <Text style={styles.feature}>⚙️ Development Tools Setup</Text>
              <Text style={styles.feature}>🌍 Internationalization Ready</Text>
            </View>
          </View>

          <View style={styles.section}>
            <Text style={styles.sectionTitle}>🚀 Next Steps</Text>
            <Text style={styles.description}>
              Ready for Sprint 1-2 implementation:
            </Text>
            <View style={styles.featureList}>
              <Text style={styles.feature}>🔐 Authentication Screens</Text>
              <Text style={styles.feature}>🧭 Navigation Setup</Text>
              <Text style={styles.feature}>📋 Basic Component Library</Text>
              <Text style={styles.feature}>🔌 API Client Configuration</Text>
            </View>
          </View>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background.primary,
  },
  content: {
    padding: 24,
  },
  title: {
    fontSize: 32,
    fontWeight: 'bold',
    color: colors.primary[500],
    textAlign: 'center',
    marginBottom: 8,
  },
  subtitle: {
    fontSize: 18,
    color: colors.text.secondary,
    textAlign: 'center',
    marginBottom: 32,
  },
  section: {
    marginBottom: 24,
    padding: 16,
    backgroundColor: colors.surface.secondary,
    borderRadius: 12,
  },
  sectionTitle: {
    fontSize: 20,
    fontWeight: '600',
    color: colors.text.primary,
    marginBottom: 12,
  },
  description: {
    fontSize: 16,
    color: colors.text.secondary,
    lineHeight: 24,
    marginBottom: 16,
  },
  featureList: {
    gap: 8,
  },
  feature: {
    fontSize: 14,
    color: colors.text.primary,
    lineHeight: 20,
    paddingLeft: 8,
  },
});

export default App;
