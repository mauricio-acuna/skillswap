import { apiClient, ApiResponse } from './apiClient';

// Types for messaging
export interface Message {
  id: string;
  conversationId: string;
  senderId: string;
  recipientId: string;
  content: string;
  type: 'text' | 'image' | 'file' | 'system';
  timestamp: string;
  status: 'sent' | 'delivered' | 'read';
  metadata?: {
    fileName?: string;
    fileSize?: number;
    imageUrl?: string;
    systemMessageType?: string;
  };
}

export interface Conversation {
  id: string;
  participants: string[];
  participantDetails: {
    [userId: string]: {
      id: string;
      name: string;
      avatar: string | null;
      isOnline: boolean;
      lastSeen?: string;
    };
  };
  lastMessage?: Message;
  unreadCount: number;
  type: 'direct' | 'group';
  skillContext?: {
    skillId: string;
    skillTitle: string;
  };
  createdAt: string;
  updatedAt: string;
}

export interface SendMessageRequest {
  conversationId?: string; // Optional for new conversations
  recipientId: string;
  content: string;
  type: 'text' | 'image' | 'file';
  skillContext?: {
    skillId: string;
    skillTitle: string;
  };
}

export interface CreateConversationRequest {
  recipientId: string;
  initialMessage?: string;
  skillContext?: {
    skillId: string;
    skillTitle: string;
  };
}

class MessagingService {
  async getConversations(userId: string): Promise<Conversation[]> {
    try {
      const response = await apiClient.get<Conversation[]>(`/users/${userId}/conversations`);
      
      if (response.success) {
        return response.data;
      }

      return this.getMockConversations(userId);
    } catch (error) {
      console.error('Error fetching conversations:', error);
      return this.getMockConversations(userId);
    }
  }

  async getConversationById(conversationId: string): Promise<Conversation | null> {
    try {
      const response = await apiClient.get<Conversation>(`/conversations/${conversationId}`);
      
      if (response.success) {
        return response.data;
      }

      return this.getMockConversationById(conversationId);
    } catch (error) {
      console.error('Error fetching conversation:', error);
      return this.getMockConversationById(conversationId);
    }
  }

  async getMessages(
    conversationId: string,
    page: number = 1,
    limit: number = 50
  ): Promise<Message[]> {
    try {
      const params = new URLSearchParams({
        page: page.toString(),
        limit: limit.toString(),
      });

      const response = await apiClient.get<Message[]>(
        `/conversations/${conversationId}/messages?${params.toString()}`
      );
      
      if (response.success) {
        return response.data;
      }

      return this.getMockMessages(conversationId);
    } catch (error) {
      console.error('Error fetching messages:', error);
      return this.getMockMessages(conversationId);
    }
  }

  async sendMessage(messageData: SendMessageRequest): Promise<Message | null> {
    try {
      const response = await apiClient.post<Message>('/messages', messageData);
      
      if (response.success) {
        return response.data;
      }

      // Create mock message for development
      return this.createMockMessage(messageData);
    } catch (error) {
      console.error('Error sending message:', error);
      // Return mock message as fallback
      return this.createMockMessage(messageData);
    }
  }

  async createConversation(conversationData: CreateConversationRequest): Promise<Conversation | null> {
    try {
      const response = await apiClient.post<Conversation>('/conversations', conversationData);
      
      if (response.success) {
        return response.data;
      }

      return this.createMockConversation(conversationData);
    } catch (error) {
      console.error('Error creating conversation:', error);
      return this.createMockConversation(conversationData);
    }
  }

  async markMessageAsRead(messageId: string): Promise<boolean> {
    try {
      const response = await apiClient.patch(`/messages/${messageId}/read`);
      return response.success;
    } catch (error) {
      console.error('Error marking message as read:', error);
      return false;
    }
  }

  async markConversationAsRead(conversationId: string): Promise<boolean> {
    try {
      const response = await apiClient.patch(`/conversations/${conversationId}/read`);
      return response.success;
    } catch (error) {
      console.error('Error marking conversation as read:', error);
      return false;
    }
  }

  async deleteMessage(messageId: string): Promise<boolean> {
    try {
      const response = await apiClient.delete(`/messages/${messageId}`);
      return response.success;
    } catch (error) {
      console.error('Error deleting message:', error);
      return false;
    }
  }

  async searchMessages(query: string, conversationId?: string): Promise<Message[]> {
    try {
      const params = new URLSearchParams({
        query,
        ...(conversationId && { conversationId }),
      });

      const response = await apiClient.get<Message[]>(`/messages/search?${params.toString()}`);
      
      if (response.success) {
        return response.data;
      }

      return [];
    } catch (error) {
      console.error('Error searching messages:', error);
      return [];
    }
  }

  // Mock data methods for development/fallback
  private getMockConversations(userId: string): Conversation[] {
    return [
      {
        id: 'conv_1',
        participants: [userId, 'user_1'],
        participantDetails: {
          [userId]: {
            id: userId,
            name: 'You',
            avatar: null,
            isOnline: true,
          },
          user_1: {
            id: 'user_1',
            name: 'Sarah Johnson',
            avatar: null,
            isOnline: true,
            lastSeen: new Date(Date.now() - 5 * 60 * 1000).toISOString(), // 5 minutes ago
          },
        },
        lastMessage: {
          id: 'msg_1',
          conversationId: 'conv_1',
          senderId: 'user_1',
          recipientId: userId,
          content: 'Great! Looking forward to our React Native session tomorrow.',
          type: 'text',
          timestamp: new Date(Date.now() - 10 * 60 * 1000).toISOString(), // 10 minutes ago
          status: 'delivered',
        },
        unreadCount: 2,
        type: 'direct',
        skillContext: {
          skillId: 'skill_1',
          skillTitle: 'React Native Development',
        },
        createdAt: '2024-02-01T10:00:00Z',
        updatedAt: new Date(Date.now() - 10 * 60 * 1000).toISOString(),
      },
      {
        id: 'conv_2',
        participants: [userId, 'user_2'],
        participantDetails: {
          [userId]: {
            id: userId,
            name: 'You',
            avatar: null,
            isOnline: true,
          },
          user_2: {
            id: 'user_2',
            name: 'Mike Chen',
            avatar: null,
            isOnline: false,
            lastSeen: new Date(Date.now() - 2 * 60 * 60 * 1000).toISOString(), // 2 hours ago
          },
        },
        lastMessage: {
          id: 'msg_2',
          conversationId: 'conv_2',
          senderId: 'user_2',
          recipientId: userId,
          content: 'Thanks for the JavaScript tips! Very helpful.',
          type: 'text',
          timestamp: new Date(Date.now() - 2 * 60 * 60 * 1000).toISOString(), // 2 hours ago
          status: 'read',
        },
        unreadCount: 0,
        type: 'direct',
        skillContext: {
          skillId: 'skill_2',
          skillTitle: 'JavaScript Fundamentals',
        },
        createdAt: '2024-01-28T14:00:00Z',
        updatedAt: new Date(Date.now() - 2 * 60 * 60 * 1000).toISOString(),
      },
    ];
  }

  private getMockConversationById(conversationId: string): Conversation | null {
    const conversations = this.getMockConversations('current_user_123');
    return conversations.find(conv => conv.id === conversationId) || null;
  }

  private getMockMessages(conversationId: string): Message[] {
    const currentUserId = 'current_user_123';
    const recipientId = conversationId === 'conv_1' ? 'user_1' : 'user_2';

    return [
      {
        id: 'msg_1',
        conversationId,
        senderId: recipientId,
        recipientId: currentUserId,
        content: 'Hi! I saw your React Native skill and I\'m interested in learning.',
        type: 'text',
        timestamp: new Date(Date.now() - 60 * 60 * 1000).toISOString(), // 1 hour ago
        status: 'read',
      },
      {
        id: 'msg_2',
        conversationId,
        senderId: currentUserId,
        recipientId: recipientId,
        content: 'Hello! I\'d be happy to help you learn React Native. What\'s your current experience level?',
        type: 'text',
        timestamp: new Date(Date.now() - 50 * 60 * 1000).toISOString(), // 50 minutes ago
        status: 'read',
      },
      {
        id: 'msg_3',
        conversationId,
        senderId: recipientId,
        recipientId: currentUserId,
        content: 'I\'m a complete beginner, but I have some JavaScript experience.',
        type: 'text',
        timestamp: new Date(Date.now() - 45 * 60 * 1000).toISOString(), // 45 minutes ago
        status: 'read',
      },
      {
        id: 'msg_4',
        conversationId,
        senderId: currentUserId,
        recipientId: recipientId,
        content: 'Perfect! That\'s a great foundation. We can start with the basics and build from there. When would you like to schedule our first session?',
        type: 'text',
        timestamp: new Date(Date.now() - 40 * 60 * 1000).toISOString(), // 40 minutes ago
        status: 'read',
      },
      {
        id: 'msg_5',
        conversationId,
        senderId: recipientId,
        recipientId: currentUserId,
        content: 'How about this weekend? I\'m free on Saturday afternoon.',
        type: 'text',
        timestamp: new Date(Date.now() - 30 * 60 * 1000).toISOString(), // 30 minutes ago
        status: 'read',
      },
      {
        id: 'msg_6',
        conversationId,
        senderId: recipientId,
        recipientId: currentUserId,
        content: 'Great! Looking forward to our React Native session tomorrow.',
        type: 'text',
        timestamp: new Date(Date.now() - 10 * 60 * 1000).toISOString(), // 10 minutes ago
        status: 'delivered',
      },
    ];
  }

  private createMockMessage(messageData: SendMessageRequest): Message {
    return {
      id: `msg_${Date.now()}`,
      conversationId: messageData.conversationId || `conv_${Date.now()}`,
      senderId: 'current_user_123',
      recipientId: messageData.recipientId,
      content: messageData.content,
      type: messageData.type,
      timestamp: new Date().toISOString(),
      status: 'sent',
    };
  }

  private createMockConversation(conversationData: CreateConversationRequest): Conversation {
    const currentUserId = 'current_user_123';
    
    return {
      id: `conv_${Date.now()}`,
      participants: [currentUserId, conversationData.recipientId],
      participantDetails: {
        [currentUserId]: {
          id: currentUserId,
          name: 'You',
          avatar: null,
          isOnline: true,
        },
        [conversationData.recipientId]: {
          id: conversationData.recipientId,
          name: 'New Contact',
          avatar: null,
          isOnline: false,
        },
      },
      unreadCount: 0,
      type: 'direct',
      skillContext: conversationData.skillContext,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    };
  }
}

export const messagingService = new MessagingService();
