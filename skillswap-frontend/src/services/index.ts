// Central export file for all services
export { apiClient, ApiError } from './apiClient';
export type { ApiResponse, PaginatedResponse } from './apiClient';

export { skillsService } from './skillsService';
export type { 
  Skill, 
  SkillFilter, 
  CreateSkillRequest 
} from './skillsService';

export { bookingService } from './bookingService';
export type { 
  Booking, 
  BookingRequest, 
  TimeSlot, 
  AvailabilityRequest 
} from './bookingService';

export { messagingService } from './messagingService';
export type { 
  Message, 
  Conversation, 
  SendMessageRequest, 
  CreateConversationRequest 
} from './messagingService';

export { userService } from './userService';
export type { 
  UserProfile, 
  UserSkill, 
  UserAvailability, 
  UserPreferences, 
  UpdateProfileRequest,
  UpdateSkillRequest 
} from './userService';
