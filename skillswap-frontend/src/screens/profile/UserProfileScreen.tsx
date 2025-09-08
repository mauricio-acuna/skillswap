import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  Alert,
  TextInput,
  Image,
  Platform,
} from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { StackNavigationProp } from '@react-navigation/stack';
import { Ionicons } from '@expo/vector-icons';

import { MainStackParamList } from '../../navigation/navigationTypes';
import { Button } from '../../components/ui/Button';
import { Card } from '../../components/ui/Card';
import { Avatar } from '../../components/ui/Avatar';
import { Badge } from '../../components/ui/Badge';
import { userService, UserProfile as ServiceUserProfile } from '../../services';
import { PlatformUtils } from '../../utils/platformUtils';

type UserProfileScreenNavigationProp = StackNavigationProp<MainStackParamList, 'UserProfile'>;

interface UserProfile {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  bio: string;
  location: string;
  profilePicture: string | null;
  rating: number;
  totalSessions: number;
  joinDate: Date;
  skills: UserSkill[];
  languages: string[];
  availability: string;
}

interface UserSkill {
  id: string;
  name: string;
  level: 'Beginner' | 'Intermediate' | 'Advanced' | 'Expert';
  category: string;
  isTeaching: boolean;
  isLearning: boolean;
  yearsOfExperience?: number;
}

const UserProfileScreen: React.FC = () => {
  const navigation = useNavigation<UserProfileScreenNavigationProp>();
  
  // State management
  const [isEditing, setIsEditing] = useState(false);
  const [loading, setLoading] = useState(false);
  const [activeTab, setActiveTab] = useState<'overview' | 'skills' | 'reviews'>('overview');
  const [userProfile, setUserProfile] = useState<UserProfile>({
    id: 'user_123',
    firstName: 'John',
    lastName: 'Doe',
    email: 'john.doe@example.com',
    phone: '+1 (555) 123-4567',
    bio: 'Passionate software developer with 5+ years of experience. Love teaching React Native and learning new technologies!',
    location: 'San Francisco, CA',
    profilePicture: null,
    rating: 4.8,
    totalSessions: 156,
    joinDate: new Date('2023-01-15'),
    skills: [
      {
        id: '1',
        name: 'React Native',
        level: 'Expert',
        category: 'Programming',
        isTeaching: true,
        isLearning: false,
        yearsOfExperience: 5,
      },
      {
        id: '2',
        name: 'JavaScript',
        level: 'Expert',
        category: 'Programming',
        isTeaching: true,
        isLearning: false,
        yearsOfExperience: 7,
      },
      {
        id: '3',
        name: 'Spanish',
        level: 'Intermediate',
        category: 'Languages',
        isTeaching: false,
        isLearning: true,
      },
      {
        id: '4',
        name: 'Guitar',
        level: 'Beginner',
        category: 'Music',
        isTeaching: false,
        isLearning: true,
      },
    ],
    languages: ['English (Native)', 'Spanish (Intermediate)'],
    availability: 'Weekdays 6-9 PM, Weekends 10 AM-4 PM',
  });

  const [editedProfile, setEditedProfile] = useState<UserProfile>(userProfile);

  useEffect(() => {
    loadUserProfile();
  }, []);

  const loadUserProfile = async () => {
    setLoading(true);
    try {
      const profile = await userService.getCurrentUserProfile();
      if (profile) {
        // Map service profile to local interface
        const mappedProfile: UserProfile = {
          id: profile.id,
          firstName: profile.firstName,
          lastName: profile.lastName,
          email: profile.email,
          phone: profile.phone || '',
          bio: profile.bio || '',
          location: profile.location || '',
          profilePicture: profile.profilePicture || null,
          rating: profile.rating,
          totalSessions: profile.totalSessions,
          joinDate: new Date(profile.joinDate),
          skills: profile.skills || [], // Use the skills from service
          languages: profile.languages || [],
          availability: 'Available' // Simplified for local interface
        };
        setUserProfile(mappedProfile);
      }
    } catch (error) {
      console.error('Error loading user profile:', error);
      Alert.alert('Error', 'Failed to load profile');
    } finally {
      setLoading(false);
    }
  };

  const handleEditProfile = () => {
    setEditedProfile({ ...userProfile });
    setIsEditing(true);
  };

  const handleSaveProfile = async () => {
    setLoading(true);
    try {
      // TODO: Implement actual API call to save profile
      await new Promise(resolve => setTimeout(resolve, 1000));
      setUserProfile(editedProfile);
      setIsEditing(false);
      Alert.alert('Success', 'Profile updated successfully!');
    } catch (error) {
      console.error('Error saving profile:', error);
      Alert.alert('Error', 'Failed to update profile');
    } finally {
      setLoading(false);
    }
  };

  const handleCancelEdit = () => {
    setEditedProfile(userProfile);
    setIsEditing(false);
  };

  const handleChangeProfilePicture = () => {
    // TODO: Implement image picker
    Alert.alert(
      'Change Profile Picture',
      'This feature will be implemented with image picker',
      [
        { text: 'Camera', onPress: () => console.log('Camera selected') },
        { text: 'Gallery', onPress: () => console.log('Gallery selected') },
        { text: 'Cancel', style: 'cancel' },
      ]
    );
  };

  const handleAddSkill = () => {
    // TODO: Navigate to add skill screen or show modal
    Alert.alert('Add Skill', 'This will open the add skill interface');
  };

  const handleSkillToggle = (skillId: string, field: 'isTeaching' | 'isLearning') => {
    const updatedSkills = editedProfile.skills.map(skill =>
      skill.id === skillId ? { ...skill, [field]: !skill[field] } : skill
    );
    setEditedProfile(prev => ({ ...prev, skills: updatedSkills }));
  };

  const formatJoinDate = (date: Date) => {
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
    });
  };

  const renderStars = (rating: number) => {
    const stars = [];
    const fullStars = Math.floor(rating);
    const hasHalfStar = rating % 1 !== 0;

    for (let i = 0; i < fullStars; i++) {
      stars.push(
        <Ionicons key={i} name="star" size={16} color="#FFD700" />
      );
    }

    if (hasHalfStar) {
      stars.push(
        <Ionicons key="half" name="star-half" size={16} color="#FFD700" />
      );
    }

    const emptyStars = 5 - Math.ceil(rating);
    for (let i = 0; i < emptyStars; i++) {
      stars.push(
        <Ionicons key={`empty-${i}`} name="star-outline" size={16} color="#FFD700" />
      );
    }

    return stars;
  };

  const renderOverviewTab = () => (
    <View>
      {/* Basic Info */}
      <Card style={styles.sectionCard}>
        <Text style={styles.sectionTitle}>Basic Information</Text>
        
        {isEditing ? (
          <View>
            <View style={styles.editRow}>
              <Text style={styles.editLabel}>First Name</Text>
              <TextInput
                style={styles.editInput}
                value={editedProfile.firstName}
                onChangeText={(text) => setEditedProfile(prev => ({ ...prev, firstName: text }))}
                placeholder="First Name"
              />
            </View>
            
            <View style={styles.editRow}>
              <Text style={styles.editLabel}>Last Name</Text>
              <TextInput
                style={styles.editInput}
                value={editedProfile.lastName}
                onChangeText={(text) => setEditedProfile(prev => ({ ...prev, lastName: text }))}
                placeholder="Last Name"
              />
            </View>
            
            <View style={styles.editRow}>
              <Text style={styles.editLabel}>Email</Text>
              <TextInput
                style={styles.editInput}
                value={editedProfile.email}
                onChangeText={(text) => setEditedProfile(prev => ({ ...prev, email: text }))}
                placeholder="Email"
                keyboardType="email-address"
                autoCapitalize="none"
              />
            </View>
            
            <View style={styles.editRow}>
              <Text style={styles.editLabel}>Phone</Text>
              <TextInput
                style={styles.editInput}
                value={editedProfile.phone}
                onChangeText={(text) => setEditedProfile(prev => ({ ...prev, phone: text }))}
                placeholder="Phone"
                keyboardType="phone-pad"
              />
            </View>
            
            <View style={styles.editRow}>
              <Text style={styles.editLabel}>Location</Text>
              <TextInput
                style={styles.editInput}
                value={editedProfile.location}
                onChangeText={(text) => setEditedProfile(prev => ({ ...prev, location: text }))}
                placeholder="Location"
              />
            </View>
            
            <View style={styles.editRow}>
              <Text style={styles.editLabel}>Bio</Text>
              <TextInput
                style={[styles.editInput, styles.bioInput]}
                value={editedProfile.bio}
                onChangeText={(text) => setEditedProfile(prev => ({ ...prev, bio: text }))}
                placeholder="Tell us about yourself..."
                multiline
                numberOfLines={4}
                textAlignVertical="top"
              />
            </View>
          </View>
        ) : (
          <View>
            <View style={styles.infoRow}>
              <Ionicons name="mail-outline" size={20} color="#666" />
              <Text style={styles.infoText}>{userProfile.email}</Text>
            </View>
            <View style={styles.infoRow}>
              <Ionicons name="call-outline" size={20} color="#666" />
              <Text style={styles.infoText}>{userProfile.phone}</Text>
            </View>
            <View style={styles.infoRow}>
              <Ionicons name="location-outline" size={20} color="#666" />
              <Text style={styles.infoText}>{userProfile.location}</Text>
            </View>
            <View style={styles.bioContainer}>
              <Text style={styles.bioText}>{userProfile.bio}</Text>
            </View>
          </View>
        )}
      </Card>

      {/* Stats */}
      <Card style={styles.sectionCard}>
        <Text style={styles.sectionTitle}>Statistics</Text>
        <View style={styles.statsContainer}>
          <View style={styles.statItem}>
            <Text style={styles.statNumber}>{userProfile.totalSessions}</Text>
            <Text style={styles.statLabel}>Sessions</Text>
          </View>
          <View style={styles.statItem}>
            <View style={styles.ratingContainer}>
              <Text style={styles.statNumber}>{userProfile.rating}</Text>
              <View style={styles.starsContainer}>
                {renderStars(userProfile.rating)}
              </View>
            </View>
            <Text style={styles.statLabel}>Rating</Text>
          </View>
          <View style={styles.statItem}>
            <Text style={styles.statNumber}>{formatJoinDate(userProfile.joinDate)}</Text>
            <Text style={styles.statLabel}>Member Since</Text>
          </View>
        </View>
      </Card>

      {/* Languages */}
      <Card style={styles.sectionCard}>
        <Text style={styles.sectionTitle}>Languages</Text>
        <View style={styles.languagesContainer}>
          {userProfile.languages.map((language, index) => (
            <Badge
              key={index}
              label={language}
              variant="secondary"
              style={styles.languageBadge}
            />
          ))}
        </View>
      </Card>
    </View>
  );

  const renderSkillsTab = () => (
    <View>
      <Card style={styles.sectionCard}>
        <View style={styles.sectionHeader}>
          <Text style={styles.sectionTitle}>My Skills</Text>
          <TouchableOpacity onPress={handleAddSkill} style={styles.addButton}>
            <Ionicons name="add-circle-outline" size={24} color="#007AFF" />
          </TouchableOpacity>
        </View>

        {userProfile.skills.map((skill) => (
          <View key={skill.id} style={styles.skillItem}>
            <View style={styles.skillHeader}>
              <Text style={styles.skillName}>{skill.name}</Text>
              <Badge label={skill.level} variant="info" />
            </View>
            
            <Text style={styles.skillCategory}>{skill.category}</Text>
            
            {skill.yearsOfExperience && (
              <Text style={styles.skillExperience}>
                {skill.yearsOfExperience} years of experience
              </Text>
            )}
            
            <View style={styles.skillActions}>
              <TouchableOpacity
                style={[
                  styles.skillToggle,
                  skill.isTeaching && styles.skillToggleActive,
                ]}
                onPress={() => handleSkillToggle(skill.id, 'isTeaching')}
                disabled={!isEditing}
              >
                <Ionicons 
                  name={skill.isTeaching ? "checkmark-circle" : "checkmark-circle-outline"} 
                  size={20} 
                  color={skill.isTeaching ? "#4CAF50" : "#666"} 
                />
                <Text style={[
                  styles.skillToggleText,
                  skill.isTeaching && styles.skillToggleTextActive,
                ]}>
                  Teaching
                </Text>
              </TouchableOpacity>
              
              <TouchableOpacity
                style={[
                  styles.skillToggle,
                  skill.isLearning && styles.skillToggleActive,
                ]}
                onPress={() => handleSkillToggle(skill.id, 'isLearning')}
                disabled={!isEditing}
              >
                <Ionicons 
                  name={skill.isLearning ? "checkmark-circle" : "checkmark-circle-outline"} 
                  size={20} 
                  color={skill.isLearning ? "#2196F3" : "#666"} 
                />
                <Text style={[
                  styles.skillToggleText,
                  skill.isLearning && styles.skillToggleTextActive,
                ]}>
                  Learning
                </Text>
              </TouchableOpacity>
            </View>
          </View>
        ))}
      </Card>
    </View>
  );

  const renderReviewsTab = () => (
    <Card style={styles.sectionCard}>
      <Text style={styles.sectionTitle}>Recent Reviews</Text>
      <View style={styles.reviewsPlaceholder}>
        <Ionicons name="star-outline" size={48} color="#DDD" />
        <Text style={styles.placeholderText}>
          Reviews will be displayed here once implemented
        </Text>
      </View>
    </Card>
  );

  return (
    <View style={styles.container}>
      <ScrollView style={styles.scrollView} showsVerticalScrollIndicator={false}>
        {/* Header */}
        <Card style={styles.headerCard}>
          <View style={styles.profileHeader}>
            <TouchableOpacity
              onPress={isEditing ? handleChangeProfilePicture : undefined}
              disabled={!isEditing}
            >
              <Avatar
                size="xl"
                source={userProfile.profilePicture ? { uri: userProfile.profilePicture } : undefined}
                name={`${userProfile.firstName} ${userProfile.lastName}`}
                style={isEditing ? styles.editableAvatar : undefined}
              />
              {isEditing && (
                <View style={styles.avatarEditOverlay}>
                  <Ionicons name="camera" size={20} color="#FFF" />
                </View>
              )}
            </TouchableOpacity>
            
            <View style={styles.profileInfo}>
              <Text style={styles.profileName}>
                {userProfile.firstName} {userProfile.lastName}
              </Text>
              <View style={styles.ratingRow}>
                <View style={styles.starsContainer}>
                  {renderStars(userProfile.rating)}
                </View>
                <Text style={styles.ratingText}>({userProfile.rating})</Text>
              </View>
            </View>
            
            <TouchableOpacity
              style={styles.editButton}
              onPress={isEditing ? handleSaveProfile : handleEditProfile}
              disabled={loading}
            >
              <Ionicons 
                name={isEditing ? "checkmark" : "pencil"} 
                size={20} 
                color="#007AFF" 
              />
            </TouchableOpacity>
          </View>
          
          {isEditing && (
            <View style={styles.editActions}>
              <Button
                title="Cancel"
                onPress={handleCancelEdit}
                variant="secondary"
                style={styles.editActionButton}
              />
              <Button
                title="Save"
                onPress={handleSaveProfile}
                loading={loading}
                style={styles.editActionButton}
              />
            </View>
          )}
        </Card>

        {/* Tab Navigation */}
        <Card style={styles.tabCard}>
          <View style={styles.tabContainer}>
            {[
              { key: 'overview', label: 'Overview', icon: 'person-outline' },
              { key: 'skills', label: 'Skills', icon: 'library-outline' },
              { key: 'reviews', label: 'Reviews', icon: 'star-outline' },
            ].map((tab) => (
              <TouchableOpacity
                key={tab.key}
                style={[
                  styles.tab,
                  activeTab === tab.key && styles.activeTab,
                ]}
                onPress={() => setActiveTab(tab.key as any)}
              >
                <Ionicons
                  name={tab.icon as any}
                  size={20}
                  color={activeTab === tab.key ? '#007AFF' : '#666'}
                />
                <Text style={[
                  styles.tabText,
                  activeTab === tab.key && styles.activeTabText,
                ]}>
                  {tab.label}
                </Text>
              </TouchableOpacity>
            ))}
          </View>
        </Card>

        {/* Tab Content */}
        {activeTab === 'overview' && renderOverviewTab()}
        {activeTab === 'skills' && renderSkillsTab()}
        {activeTab === 'reviews' && renderReviewsTab()}
      </ScrollView>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F5F5F5',
  },
  scrollView: {
    flex: 1,
    padding: 16,
  },
  headerCard: {
    marginBottom: 16,
  },
  profileHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 12,
  },
  editableAvatar: {
    opacity: 0.8,
  },
  avatarEditOverlay: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: 'rgba(0, 0, 0, 0.3)',
    borderRadius: 40,
    justifyContent: 'center',
    alignItems: 'center',
  },
  profileInfo: {
    flex: 1,
    marginLeft: 16,
  },
  profileName: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#1A1A1A',
    marginBottom: 4,
  },
  ratingRow: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  starsContainer: {
    flexDirection: 'row',
    marginRight: 8,
  },
  ratingText: {
    fontSize: 14,
    color: '#666',
  },
  editButton: {
    padding: 8,
  },
  editActions: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginTop: 12,
    gap: 12,
  },
  editActionButton: {
    flex: 1,
  },
  tabCard: {
    marginBottom: 16,
  },
  tabContainer: {
    flexDirection: 'row',
  },
  tab: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 12,
    paddingHorizontal: 8,
  },
  activeTab: {
    backgroundColor: '#E3F2FD',
    borderRadius: 8,
  },
  tabText: {
    fontSize: 14,
    color: '#666',
    marginLeft: 4,
  },
  activeTabText: {
    color: '#007AFF',
    fontWeight: '500',
  },
  sectionCard: {
    marginBottom: 16,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: '600',
    color: '#1A1A1A',
    marginBottom: 12,
  },
  sectionHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 12,
  },
  addButton: {
    padding: 4,
  },
  infoRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 8,
  },
  infoText: {
    fontSize: 16,
    color: '#333',
    marginLeft: 12,
  },
  bioContainer: {
    marginTop: 12,
    padding: 12,
    backgroundColor: '#F8F8F8',
    borderRadius: 8,
  },
  bioText: {
    fontSize: 16,
    color: '#333',
    lineHeight: 22,
  },
  editRow: {
    marginBottom: 16,
  },
  editLabel: {
    fontSize: 14,
    fontWeight: '500',
    color: '#666',
    marginBottom: 4,
  },
  editInput: {
    borderWidth: 1,
    borderColor: '#E0E0E0',
    borderRadius: 8,
    padding: 12,
    fontSize: 16,
    backgroundColor: '#FFF',
  },
  bioInput: {
    height: 100,
    textAlignVertical: 'top',
  },
  statsContainer: {
    flexDirection: 'row',
    justifyContent: 'space-around',
  },
  statItem: {
    alignItems: 'center',
  },
  statNumber: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#007AFF',
    marginBottom: 4,
  },
  statLabel: {
    fontSize: 12,
    color: '#666',
    textAlign: 'center',
  },
  ratingContainer: {
    alignItems: 'center',
  },
  languagesContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
  },
  languageBadge: {
    marginBottom: 4,
  },
  skillItem: {
    padding: 12,
    backgroundColor: '#F8F8F8',
    borderRadius: 8,
    marginBottom: 8,
  },
  skillHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 4,
  },
  skillName: {
    fontSize: 16,
    fontWeight: '600',
    color: '#1A1A1A',
  },
  skillCategory: {
    fontSize: 14,
    color: '#666',
    marginBottom: 4,
  },
  skillExperience: {
    fontSize: 12,
    color: '#666',
    marginBottom: 8,
  },
  skillActions: {
    flexDirection: 'row',
    gap: 12,
  },
  skillToggle: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingVertical: 4,
    paddingHorizontal: 8,
    borderRadius: 6,
    backgroundColor: '#FFF',
  },
  skillToggleActive: {
    backgroundColor: '#E8F5E8',
  },
  skillToggleText: {
    fontSize: 12,
    color: '#666',
    marginLeft: 4,
  },
  skillToggleTextActive: {
    color: '#4CAF50',
    fontWeight: '500',
  },
  reviewsPlaceholder: {
    alignItems: 'center',
    padding: 40,
  },
  placeholderText: {
    fontSize: 16,
    color: '#999',
    textAlign: 'center',
    marginTop: 12,
  },
});

export default UserProfileScreen;
