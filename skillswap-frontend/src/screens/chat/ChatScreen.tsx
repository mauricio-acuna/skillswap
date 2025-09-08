import React, { useState, useEffect, useRef } from 'react';
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  TextInput,
  TouchableOpacity,
  Alert,
  KeyboardAvoidingView,
  Platform,
} from 'react-native';
import { useNavigation, useRoute, RouteProp } from '@react-navigation/native';
import { StackNavigationProp } from '@react-navigation/stack';
import { Ionicons } from '@expo/vector-icons';

import { MainStackParamList } from '../../navigation/navigationTypes';
import { Avatar } from '../../components/ui/Avatar';
import { PlatformUtils } from '../../utils/platformUtils';

type ChatScreenRouteProp = RouteProp<MainStackParamList, 'Chat'>;
type ChatScreenNavigationProp = StackNavigationProp<MainStackParamList, 'Chat'>;

interface Message {
  id: string;
  text: string;
  senderId: string;
  recipientId: string;
  timestamp: Date;
  status: 'sent' | 'delivered' | 'read';
  type: 'text' | 'image' | 'file';
}

const ChatScreen: React.FC = () => {
  const navigation = useNavigation<ChatScreenNavigationProp>();
  const route = useRoute<ChatScreenRouteProp>();
  const flatListRef = useRef<FlatList>(null);
  
  const { recipientId, recipientName } = route.params;
  const currentUserId = 'current_user_123'; // TODO: Get from auth context

  // State management
  const [messages, setMessages] = useState<Message[]>([]);
  const [newMessage, setNewMessage] = useState('');
  const [loading, setLoading] = useState(false);
  const [isTyping, setIsTyping] = useState(false);

  useEffect(() => {
    loadMessages();
    
    // Set up header with recipient info
    navigation.setOptions({
      headerTitle: () => (
        <View style={styles.headerTitle}>
          <Avatar size="small" name={recipientName} />
          <View style={styles.headerTitleText}>
            <Text style={styles.headerName}>{recipientName}</Text>
            <Text style={styles.headerStatus}>Online</Text>
          </View>
        </View>
      ),
      headerRight: () => (
        <TouchableOpacity 
          style={styles.headerButton}
          onPress={() => Alert.alert('Info', 'User profile coming soon')}
        >
          <Ionicons name="information-circle-outline" size={24} color="#007AFF" />
        </TouchableOpacity>
      ),
    });
  }, [recipientName, navigation]);

  const loadMessages = async () => {
    setLoading(true);
    try {
      // TODO: Replace with actual API call
      await new Promise(resolve => setTimeout(resolve, 500));
      
      // Mock messages data
      const mockMessages: Message[] = [
        {
          id: '1',
          text: 'Hi! I saw your React Native skill and I\'m interested in learning.',
          senderId: recipientId,
          recipientId: currentUserId,
          timestamp: new Date(Date.now() - 60 * 60 * 1000), // 1 hour ago
          status: 'read',
          type: 'text',
        },
        {
          id: '2',
          text: 'Hello! I\'d be happy to help you learn React Native. What\'s your current experience level?',
          senderId: currentUserId,
          recipientId: recipientId,
          timestamp: new Date(Date.now() - 50 * 60 * 1000), // 50 minutes ago
          status: 'read',
          type: 'text',
        },
        {
          id: '3',
          text: 'I\'m a complete beginner, but I have some JavaScript experience.',
          senderId: recipientId,
          recipientId: currentUserId,
          timestamp: new Date(Date.now() - 45 * 60 * 1000), // 45 minutes ago
          status: 'read',
          type: 'text',
        },
        {
          id: '4',
          text: 'Perfect! That\'s a great foundation. We can start with the basics and build from there. When would you like to schedule our first session?',
          senderId: currentUserId,
          recipientId: recipientId,
          timestamp: new Date(Date.now() - 40 * 60 * 1000), // 40 minutes ago
          status: 'read',
          type: 'text',
        },
        {
          id: '5',
          text: 'How about this weekend? I\'m free on Saturday afternoon.',
          senderId: recipientId,
          recipientId: currentUserId,
          timestamp: new Date(Date.now() - 30 * 60 * 1000), // 30 minutes ago
          status: 'read',
          type: 'text',
        },
        {
          id: '6',
          text: 'Great! Looking forward to our React Native session tomorrow.',
          senderId: recipientId,
          recipientId: currentUserId,
          timestamp: new Date(Date.now() - 10 * 60 * 1000), // 10 minutes ago
          status: 'delivered',
          type: 'text',
        },
      ];
      
      setMessages(mockMessages.reverse()); // Newest at bottom
    } catch (error) {
      console.error('Error loading messages:', error);
      Alert.alert('Error', 'Failed to load messages');
    } finally {
      setLoading(false);
    }
  };

  const sendMessage = async () => {
    if (!newMessage.trim()) return;

    const messageText = newMessage.trim();
    setNewMessage('');

    const tempMessage: Message = {
      id: Date.now().toString(),
      text: messageText,
      senderId: currentUserId,
      recipientId: recipientId,
      timestamp: new Date(),
      status: 'sent',
      type: 'text',
    };

    setMessages(prev => [...prev, tempMessage]);

    // Scroll to bottom
    setTimeout(() => {
      flatListRef.current?.scrollToEnd({ animated: true });
    }, 100);

    try {
      // TODO: Send message to API
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      // Update message status to delivered
      setMessages(prev => 
        prev.map(msg => 
          msg.id === tempMessage.id 
            ? { ...msg, status: 'delivered' as const }
            : msg
        )
      );
    } catch (error) {
      console.error('Error sending message:', error);
      Alert.alert('Error', 'Failed to send message');
      
      // Remove failed message
      setMessages(prev => prev.filter(msg => msg.id !== tempMessage.id));
    }
  };

  const formatMessageTime = (date: Date) => {
    return date.toLocaleTimeString('en-US', {
      hour: 'numeric',
      minute: '2-digit',
      hour12: true,
    });
  };

  const formatMessageDate = (date: Date) => {
    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(yesterday.getDate() - 1);

    if (date.toDateString() === today.toDateString()) {
      return 'Today';
    } else if (date.toDateString() === yesterday.toDateString()) {
      return 'Yesterday';
    } else {
      return date.toLocaleDateString('en-US', {
        month: 'short',
        day: 'numeric',
      });
    }
  };

  const renderMessageStatus = (status: Message['status']) => {
    switch (status) {
      case 'sent':
        return <Ionicons name="checkmark" size={12} color="#666" />;
      case 'delivered':
        return <Ionicons name="checkmark-done" size={12} color="#666" />;
      case 'read':
        return <Ionicons name="checkmark-done" size={12} color="#007AFF" />;
      default:
        return null;
    }
  };

  const renderMessage = ({ item, index }: { item: Message; index: number }) => {
    const isFromCurrentUser = item.senderId === currentUserId;
    const previousMessage = index > 0 ? messages[index - 1] : null;
    const showDate = !previousMessage || 
      formatMessageDate(item.timestamp) !== formatMessageDate(previousMessage.timestamp);

    return (
      <View>
        {showDate && (
          <View style={styles.dateSeparator}>
            <Text style={styles.dateText}>{formatMessageDate(item.timestamp)}</Text>
          </View>
        )}
        
        <View style={[
          styles.messageContainer,
          isFromCurrentUser ? styles.sentMessageContainer : styles.receivedMessageContainer,
        ]}>
          <View style={[
            styles.messageBubble,
            isFromCurrentUser ? styles.sentMessageBubble : styles.receivedMessageBubble,
          ]}>
            <Text style={[
              styles.messageText,
              isFromCurrentUser ? styles.sentMessageText : styles.receivedMessageText,
            ]}>
              {item.text}
            </Text>
          </View>
          
          <View style={[
            styles.messageFooter,
            isFromCurrentUser ? styles.sentMessageFooter : styles.receivedMessageFooter,
          ]}>
            <Text style={styles.messageTime}>
              {formatMessageTime(item.timestamp)}
            </Text>
            {isFromCurrentUser && (
              <View style={styles.messageStatusContainer}>
                {renderMessageStatus(item.status)}
              </View>
            )}
          </View>
        </View>
      </View>
    );
  };

  const renderInputToolbar = () => (
    <View style={styles.inputToolbar}>
      <View style={styles.inputContainer}>
        <TextInput
          style={styles.textInput}
          value={newMessage}
          onChangeText={setNewMessage}
          placeholder="Type a message..."
          placeholderTextColor="#999"
          multiline
          maxLength={1000}
          returnKeyType="default"
          blurOnSubmit={false}
        />
        
        <TouchableOpacity
          style={[
            styles.sendButton,
            newMessage.trim().length > 0 && styles.sendButtonActive,
          ]}
          onPress={sendMessage}
          disabled={newMessage.trim().length === 0}
        >
          <Ionicons 
            name="send" 
            size={20} 
            color={newMessage.trim().length > 0 ? '#FFFFFF' : '#999'} 
          />
        </TouchableOpacity>
      </View>
    </View>
  );

  return (
    <KeyboardAvoidingView
      style={styles.container}
      behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
      keyboardVerticalOffset={Platform.OS === 'ios' ? 90 : 0}
    >
      <FlatList
        ref={flatListRef}
        data={messages}
        renderItem={renderMessage}
        keyExtractor={(item) => item.id}
        style={styles.messagesList}
        contentContainerStyle={styles.messagesContainer}
        showsVerticalScrollIndicator={false}
        onContentSizeChange={() => flatListRef.current?.scrollToEnd({ animated: false })}
      />
      
      {isTyping && (
        <View style={styles.typingIndicator}>
          <Text style={styles.typingText}>{recipientName} is typing...</Text>
        </View>
      )}
      
      {renderInputToolbar()}
    </KeyboardAvoidingView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F5F5F5',
  },
  headerTitle: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  headerTitleText: {
    marginLeft: 8,
  },
  headerName: {
    fontSize: 16,
    fontWeight: '600',
    color: '#1A1A1A',
  },
  headerStatus: {
    fontSize: 12,
    color: '#4CAF50',
  },
  headerButton: {
    padding: 8,
    marginRight: 8,
  },
  messagesList: {
    flex: 1,
  },
  messagesContainer: {
    padding: 16,
    paddingBottom: 8,
  },
  dateSeparator: {
    alignItems: 'center',
    marginVertical: 16,
  },
  dateText: {
    fontSize: 12,
    color: '#999',
    backgroundColor: '#FFFFFF',
    paddingHorizontal: 12,
    paddingVertical: 4,
    borderRadius: 12,
    overflow: 'hidden',
  },
  messageContainer: {
    marginBottom: 8,
    maxWidth: '80%',
  },
  sentMessageContainer: {
    alignSelf: 'flex-end',
    alignItems: 'flex-end',
  },
  receivedMessageContainer: {
    alignSelf: 'flex-start',
    alignItems: 'flex-start',
  },
  messageBubble: {
    borderRadius: 18,
    paddingHorizontal: 16,
    paddingVertical: 10,
    marginBottom: 2,
  },
  sentMessageBubble: {
    backgroundColor: '#007AFF',
    borderBottomRightRadius: 4,
  },
  receivedMessageBubble: {
    backgroundColor: '#FFFFFF',
    borderBottomLeftRadius: 4,
    ...(PlatformUtils.isIOS ? {
      shadowColor: '#000',
      shadowOffset: { width: 0, height: 1 },
      shadowOpacity: 0.1,
      shadowRadius: 1,
    } : {
      elevation: 1,
    }),
  },
  messageText: {
    fontSize: 16,
    lineHeight: 20,
  },
  sentMessageText: {
    color: '#FFFFFF',
  },
  receivedMessageText: {
    color: '#1A1A1A',
  },
  messageFooter: {
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: 2,
  },
  sentMessageFooter: {
    justifyContent: 'flex-end',
  },
  receivedMessageFooter: {
    justifyContent: 'flex-start',
  },
  messageTime: {
    fontSize: 11,
    color: '#999',
  },
  messageStatusContainer: {
    marginLeft: 4,
  },
  typingIndicator: {
    padding: 16,
    paddingVertical: 8,
  },
  typingText: {
    fontSize: 14,
    color: '#666',
    fontStyle: 'italic',
  },
  inputToolbar: {
    backgroundColor: '#FFFFFF',
    paddingHorizontal: 16,
    paddingVertical: 8,
    borderTopWidth: 1,
    borderTopColor: '#E0E0E0',
  },
  inputContainer: {
    flexDirection: 'row',
    alignItems: 'flex-end',
    backgroundColor: '#F8F8F8',
    borderRadius: 20,
    borderWidth: 1,
    borderColor: '#E0E0E0',
    paddingHorizontal: 12,
    paddingVertical: 8,
    minHeight: 40,
  },
  textInput: {
    flex: 1,
    fontSize: 16,
    color: '#1A1A1A',
    maxHeight: 100,
    paddingVertical: 0,
    marginRight: 8,
  },
  sendButton: {
    width: 32,
    height: 32,
    borderRadius: 16,
    backgroundColor: '#E0E0E0',
    justifyContent: 'center',
    alignItems: 'center',
  },
  sendButtonActive: {
    backgroundColor: '#007AFF',
  },
});

export default ChatScreen;
