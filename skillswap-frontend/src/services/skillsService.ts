import { apiClient, ApiResponse, PaginatedResponse } from './apiClient';

// Types for skills
export interface Skill {
  id: string;
  title: string;
  description: string;
  category: string;
  level: 'Beginner' | 'Intermediate' | 'Advanced' | 'Expert';
  price: number;
  duration: number; // in minutes
  instructor: {
    id: string;
    name: string;
    avatar: string | null;
    rating: number;
    totalReviews: number;
    verifiedInstructor: boolean;
  };
  images: string[];
  tags: string[];
  rating: number;
  totalReviews: number;
  totalStudents: number;
  isBookmarked: boolean;
  availability: 'available' | 'busy' | 'offline';
  featured: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface SkillFilter {
  category?: string;
  level?: string;
  priceMin?: number;
  priceMax?: number;
  rating?: number;
  search?: string;
  location?: string;
  availability?: 'available' | 'busy' | 'offline';
}

export interface CreateSkillRequest {
  title: string;
  description: string;
  category: string;
  level: 'Beginner' | 'Intermediate' | 'Advanced' | 'Expert';
  price: number;
  duration: number;
  tags: string[];
  images?: string[];
}

class SkillsService {
  async getSkills(
    page: number = 1,
    limit: number = 20,
    filters?: SkillFilter
  ): Promise<PaginatedResponse<Skill>> {
    try {
      const params = new URLSearchParams({
        page: page.toString(),
        limit: limit.toString(),
        ...(filters?.category && { category: filters.category }),
        ...(filters?.level && { level: filters.level }),
        ...(filters?.priceMin && { priceMin: filters.priceMin.toString() }),
        ...(filters?.priceMax && { priceMax: filters.priceMax.toString() }),
        ...(filters?.rating && { rating: filters.rating.toString() }),
        ...(filters?.search && { search: filters.search }),
        ...(filters?.location && { location: filters.location }),
        ...(filters?.availability && { availability: filters.availability }),
      });

      const response = await apiClient.get<PaginatedResponse<Skill>>(
        `/skills?${params.toString()}`
      );

      if (response.success) {
        return response.data;
      }

      // Return mock data if API fails or in development
      return this.getMockSkills(page, limit, filters);
    } catch (error) {
      console.error('Error fetching skills:', error);
      // Return mock data as fallback
      return this.getMockSkills(page, limit, filters);
    }
  }

  async getSkillById(skillId: string): Promise<Skill | null> {
    try {
      const response = await apiClient.get<Skill>(`/skills/${skillId}`);
      
      if (response.success) {
        return response.data;
      }

      return this.getMockSkillById(skillId);
    } catch (error) {
      console.error('Error fetching skill by ID:', error);
      return this.getMockSkillById(skillId);
    }
  }

  async createSkill(skillData: CreateSkillRequest): Promise<Skill | null> {
    try {
      const response = await apiClient.post<Skill>('/skills', skillData);
      
      if (response.success) {
        return response.data;
      }

      throw new Error(response.error || 'Failed to create skill');
    } catch (error) {
      console.error('Error creating skill:', error);
      throw error;
    }
  }

  async updateSkill(skillId: string, skillData: Partial<CreateSkillRequest>): Promise<Skill | null> {
    try {
      const response = await apiClient.put<Skill>(`/skills/${skillId}`, skillData);
      
      if (response.success) {
        return response.data;
      }

      throw new Error(response.error || 'Failed to update skill');
    } catch (error) {
      console.error('Error updating skill:', error);
      throw error;
    }
  }

  async deleteSkill(skillId: string): Promise<boolean> {
    try {
      const response = await apiClient.delete(`/skills/${skillId}`);
      return response.success;
    } catch (error) {
      console.error('Error deleting skill:', error);
      return false;
    }
  }

  async toggleBookmark(skillId: string): Promise<boolean> {
    try {
      const response = await apiClient.post(`/skills/${skillId}/bookmark`);
      return response.success;
    } catch (error) {
      console.error('Error toggling bookmark:', error);
      return false;
    }
  }

  async getCategories(): Promise<string[]> {
    try {
      const response = await apiClient.get<string[]>('/skills/categories');
      
      if (response.success) {
        return response.data;
      }

      return this.getMockCategories();
    } catch (error) {
      console.error('Error fetching categories:', error);
      return this.getMockCategories();
    }
  }

  // Mock data methods for development/fallback
  private getMockSkills(
    page: number = 1,
    limit: number = 20,
    filters?: SkillFilter
  ): PaginatedResponse<Skill> {
    const mockSkills: Skill[] = [
      {
        id: '1',
        title: 'React Native Development',
        description: 'Learn to build cross-platform mobile apps with React Native. Perfect for beginners who want to get into mobile development.',
        category: 'Programming',
        level: 'Intermediate',
        price: 45,
        duration: 60,
        instructor: {
          id: 'instructor_1',
          name: 'Sarah Johnson',
          avatar: null,
          rating: 4.9,
          totalReviews: 156,
          verifiedInstructor: true,
        },
        images: [],
        tags: ['React', 'Mobile', 'JavaScript', 'Cross-platform'],
        rating: 4.8,
        totalReviews: 89,
        totalStudents: 245,
        isBookmarked: false,
        availability: 'available',
        featured: true,
        createdAt: '2024-01-15T10:00:00Z',
        updatedAt: '2024-01-20T15:30:00Z',
      },
      {
        id: '2',
        title: 'Spanish Conversation',
        description: 'Improve your Spanish speaking skills through engaging conversations about daily life, culture, and current events.',
        category: 'Languages',
        level: 'Beginner',
        price: 30,
        duration: 45,
        instructor: {
          id: 'instructor_2',
          name: 'Carlos Rodriguez',
          avatar: null,
          rating: 4.7,
          totalReviews: 203,
          verifiedInstructor: true,
        },
        images: [],
        tags: ['Spanish', 'Conversation', 'Culture', 'Speaking'],
        rating: 4.6,
        totalReviews: 127,
        totalStudents: 189,
        isBookmarked: true,
        availability: 'available',
        featured: false,
        createdAt: '2024-01-10T09:00:00Z',
        updatedAt: '2024-01-25T11:15:00Z',
      },
      {
        id: '3',
        title: 'Guitar Basics',
        description: 'Start your musical journey with guitar fundamentals. Learn chords, strumming patterns, and your first songs.',
        category: 'Music',
        level: 'Beginner',
        price: 35,
        duration: 60,
        instructor: {
          id: 'instructor_3',
          name: 'Mike Chen',
          avatar: null,
          rating: 4.8,
          totalReviews: 98,
          verifiedInstructor: false,
        },
        images: [],
        tags: ['Guitar', 'Music', 'Chords', 'Beginner'],
        rating: 4.7,
        totalReviews: 76,
        totalStudents: 134,
        isBookmarked: false,
        availability: 'available',
        featured: false,
        createdAt: '2024-01-12T14:00:00Z',
        updatedAt: '2024-01-22T16:45:00Z',
      },
    ];

    // Apply filters if provided
    let filteredSkills = mockSkills;
    
    if (filters?.search) {
      const searchLower = filters.search.toLowerCase();
      filteredSkills = filteredSkills.filter(skill =>
        skill.title.toLowerCase().includes(searchLower) ||
        skill.description.toLowerCase().includes(searchLower) ||
        skill.tags.some(tag => tag.toLowerCase().includes(searchLower))
      );
    }

    if (filters?.category) {
      filteredSkills = filteredSkills.filter(skill => skill.category === filters.category);
    }

    if (filters?.level) {
      filteredSkills = filteredSkills.filter(skill => skill.level === filters.level);
    }

    // Pagination
    const startIndex = (page - 1) * limit;
    const endIndex = startIndex + limit;
    const paginatedSkills = filteredSkills.slice(startIndex, endIndex);

    return {
      items: paginatedSkills,
      totalItems: filteredSkills.length,
      totalPages: Math.ceil(filteredSkills.length / limit),
      currentPage: page,
      hasNext: endIndex < filteredSkills.length,
      hasPrevious: page > 1,
    };
  }

  private getMockSkillById(skillId: string): Skill | null {
    const mockSkills = this.getMockSkills().items;
    return mockSkills.find(skill => skill.id === skillId) || null;
  }

  private getMockCategories(): string[] {
    return [
      'Programming',
      'Languages',
      'Music',
      'Arts & Crafts',
      'Cooking',
      'Sports & Fitness',
      'Business',
      'Photography',
      'Writing',
      'Design',
    ];
  }
}

export const skillsService = new SkillsService();
