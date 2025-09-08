import { apiClient, ApiResponse } from './apiClient';

// Types for user profiles
export interface UserProfile {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  bio?: string;
  location?: string;
  profilePicture?: string;
  rating: number;
  totalReviews: number;
  totalSessions: number;
  totalStudents: number;
  joinDate: string;
  skills: UserSkill[];
  languages: string[];
  availability: UserAvailability;
  preferences: UserPreferences;
  socialLinks?: SocialLinks;
  verificationStatus: {
    email: boolean;
    phone: boolean;
    identity: boolean;
  };
  isOnline: boolean;
  lastSeen?: string;
}

export interface UserSkill {
  id: string;
  name: string;
  level: 'Beginner' | 'Intermediate' | 'Advanced' | 'Expert';
  category: string;
  isTeaching: boolean;
  isLearning: boolean;
  yearsOfExperience?: number;
  certifications?: string[];
  description?: string;
  price?: number; // for teaching
}

export interface UserAvailability {
  timezone: string;
  schedule: {
    [day: string]: {
      isAvailable: boolean;
      timeSlots: Array<{
        start: string; // HH:MM format
        end: string;   // HH:MM format
      }>;
    };
  };
  blackoutDates: string[]; // ISO date strings
}

export interface UserPreferences {
  language: string;
  currency: string;
  notifications: {
    email: boolean;
    push: boolean;
    sms: boolean;
  };
  privacy: {
    showEmail: boolean;
    showPhone: boolean;
    showLocation: boolean;
    showOnlineStatus: boolean;
  };
  matchmaking: {
    maxDistance: number; // in km
    ageRange: {
      min: number;
      max: number;
    };
    preferredSkillLevels: string[];
  };
}

export interface SocialLinks {
  linkedin?: string;
  github?: string;
  twitter?: string;
  instagram?: string;
  website?: string;
}

export interface UpdateProfileRequest {
  firstName?: string;
  lastName?: string;
  bio?: string;
  location?: string;
  phone?: string;
  languages?: string[];
  availability?: UserAvailability;
  preferences?: Partial<UserPreferences>;
  socialLinks?: SocialLinks;
}

export interface UpdateSkillRequest {
  name?: string;
  level?: UserSkill['level'];
  category?: string;
  isTeaching?: boolean;
  isLearning?: boolean;
  yearsOfExperience?: number;
  description?: string;
  price?: number;
}

class UserService {
  async getUserProfile(userId: string): Promise<UserProfile | null> {
    try {
      const response = await apiClient.get<UserProfile>(`/users/${userId}`);
      
      if (response.success) {
        return response.data;
      }

      return this.getMockUserProfile(userId);
    } catch (error) {
      console.error('Error fetching user profile:', error);
      return this.getMockUserProfile(userId);
    }
  }

  async getCurrentUserProfile(): Promise<UserProfile | null> {
    try {
      const response = await apiClient.get<UserProfile>('/users/me');
      
      if (response.success) {
        return response.data;
      }

      return this.getMockCurrentUserProfile();
    } catch (error) {
      console.error('Error fetching current user profile:', error);
      return this.getMockCurrentUserProfile();
    }
  }

  async updateProfile(profileData: UpdateProfileRequest): Promise<UserProfile | null> {
    try {
      const response = await apiClient.put<UserProfile>('/users/me', profileData);
      
      if (response.success) {
        return response.data;
      }

      // Mock successful update for development
      const currentProfile = await this.getCurrentUserProfile();
      if (currentProfile) {
        const updatedProfile: UserProfile = { 
          ...currentProfile, 
          ...profileData,
          preferences: {
            ...currentProfile.preferences,
            ...profileData.preferences,
            language: profileData.preferences?.language || currentProfile.preferences.language
          }
        };
        return updatedProfile;
      }

      throw new Error(response.error || 'Failed to update profile');
    } catch (error) {
      console.error('Error updating profile:', error);
      throw error;
    }
  }

  async uploadProfilePicture(imageUri: string): Promise<string | null> {
    try {
      // Create FormData for file upload
      const formData = new FormData();
      formData.append('profilePicture', {
        uri: imageUri,
        type: 'image/jpeg',
        name: 'profile.jpg',
      } as any);

      const response = await fetch(`${apiClient.baseURL}/users/me/profile-picture`, {
        method: 'POST',
        body: formData,
        headers: {
          'Content-Type': 'multipart/form-data',
          // Authorization header would be added by apiClient if available
        },
      });

      if (response.ok) {
        const data = await response.json();
        return data.profilePictureUrl;
      }

      // Mock successful upload for development
      return `https://mock-api.skillswap.com/uploads/profile_${Date.now()}.jpg`;
    } catch (error) {
      console.error('Error uploading profile picture:', error);
      return null;
    }
  }

  async addSkill(skillData: Omit<UserSkill, 'id'>): Promise<UserSkill | null> {
    try {
      const response = await apiClient.post<UserSkill>('/users/me/skills', skillData);
      
      if (response.success) {
        return response.data;
      }

      // Mock successful skill addition
      return {
        id: `skill_${Date.now()}`,
        ...skillData,
      };
    } catch (error) {
      console.error('Error adding skill:', error);
      return null;
    }
  }

  async updateSkill(skillId: string, skillData: UpdateSkillRequest): Promise<UserSkill | null> {
    try {
      const response = await apiClient.put<UserSkill>(`/users/me/skills/${skillId}`, skillData);
      
      if (response.success) {
        return response.data;
      }

      throw new Error(response.error || 'Failed to update skill');
    } catch (error) {
      console.error('Error updating skill:', error);
      throw error;
    }
  }

  async removeSkill(skillId: string): Promise<boolean> {
    try {
      const response = await apiClient.delete(`/users/me/skills/${skillId}`);
      return response.success;
    } catch (error) {
      console.error('Error removing skill:', error);
      return false;
    }
  }

  async searchUsers(
    query: string,
    filters?: {
      skills?: string[];
      location?: string;
      rating?: number;
      isOnline?: boolean;
    }
  ): Promise<UserProfile[]> {
    try {
      const params = new URLSearchParams({
        query,
        ...(filters?.skills && { skills: filters.skills.join(',') }),
        ...(filters?.location && { location: filters.location }),
        ...(filters?.rating && { rating: filters.rating.toString() }),
        ...(filters?.isOnline !== undefined && { isOnline: filters.isOnline.toString() }),
      });

      const response = await apiClient.get<UserProfile[]>(`/users/search?${params.toString()}`);
      
      if (response.success) {
        return response.data;
      }

      return this.getMockSearchResults(query);
    } catch (error) {
      console.error('Error searching users:', error);
      return this.getMockSearchResults(query);
    }
  }

  async followUser(userId: string): Promise<boolean> {
    try {
      const response = await apiClient.post(`/users/${userId}/follow`);
      return response.success;
    } catch (error) {
      console.error('Error following user:', error);
      return false;
    }
  }

  async unfollowUser(userId: string): Promise<boolean> {
    try {
      const response = await apiClient.delete(`/users/${userId}/follow`);
      return response.success;
    } catch (error) {
      console.error('Error unfollowing user:', error);
      return false;
    }
  }

  // Mock data methods for development/fallback
  private getMockUserProfile(userId: string): UserProfile {
    return {
      id: userId,
      firstName: 'John',
      lastName: 'Doe',
      email: 'john.doe@example.com',
      phone: '+1 (555) 123-4567',
      bio: 'Passionate software developer with 5+ years of experience. Love teaching React Native and learning new technologies!',
      location: 'San Francisco, CA',
      profilePicture: undefined,
      rating: 4.8,
      totalReviews: 156,
      totalSessions: 234,
      totalStudents: 89,
      joinDate: '2023-01-15T10:00:00Z',
      skills: [
        {
          id: '1',
          name: 'React Native',
          level: 'Expert',
          category: 'Programming',
          isTeaching: true,
          isLearning: false,
          yearsOfExperience: 5,
          price: 45,
        },
        {
          id: '2',
          name: 'JavaScript',
          level: 'Expert',
          category: 'Programming',
          isTeaching: true,
          isLearning: false,
          yearsOfExperience: 7,
          price: 40,
        },
        {
          id: '3',
          name: 'Spanish',
          level: 'Intermediate',
          category: 'Languages',
          isTeaching: false,
          isLearning: true,
        },
      ],
      languages: ['English (Native)', 'Spanish (Intermediate)'],
      availability: {
        timezone: 'America/Los_Angeles',
        schedule: {
          monday: {
            isAvailable: true,
            timeSlots: [
              { start: '09:00', end: '17:00' },
            ],
          },
          tuesday: {
            isAvailable: true,
            timeSlots: [
              { start: '09:00', end: '17:00' },
            ],
          },
          wednesday: {
            isAvailable: true,
            timeSlots: [
              { start: '09:00', end: '17:00' },
            ],
          },
          thursday: {
            isAvailable: true,
            timeSlots: [
              { start: '09:00', end: '17:00' },
            ],
          },
          friday: {
            isAvailable: true,
            timeSlots: [
              { start: '09:00', end: '17:00' },
            ],
          },
          saturday: {
            isAvailable: true,
            timeSlots: [
              { start: '10:00', end: '16:00' },
            ],
          },
          sunday: {
            isAvailable: false,
            timeSlots: [],
          },
        },
        blackoutDates: [],
      },
      preferences: {
        language: 'en',
        currency: 'USD',
        notifications: {
          email: true,
          push: true,
          sms: false,
        },
        privacy: {
          showEmail: false,
          showPhone: false,
          showLocation: true,
          showOnlineStatus: true,
        },
        matchmaking: {
          maxDistance: 50,
          ageRange: {
            min: 18,
            max: 65,
          },
          preferredSkillLevels: ['Beginner', 'Intermediate'],
        },
      },
      verificationStatus: {
        email: true,
        phone: true,
        identity: false,
      },
      isOnline: userId === 'current_user_123',
      lastSeen: userId !== 'current_user_123' ? new Date(Date.now() - 30 * 60 * 1000).toISOString() : undefined,
    };
  }

  private getMockCurrentUserProfile(): UserProfile {
    return this.getMockUserProfile('current_user_123');
  }

  private getMockSearchResults(query: string): UserProfile[] {
    const allUsers = [
      this.getMockUserProfile('user_1'),
      this.getMockUserProfile('user_2'),
      this.getMockUserProfile('user_3'),
    ];

    // Simple mock search filtering
    return allUsers.filter(user =>
      user.firstName.toLowerCase().includes(query.toLowerCase()) ||
      user.lastName.toLowerCase().includes(query.toLowerCase()) ||
      user.skills.some(skill => skill.name.toLowerCase().includes(query.toLowerCase()))
    );
  }
}

export const userService = new UserService();
