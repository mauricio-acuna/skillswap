import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  Alert,
  Platform,
} from 'react-native';
import { RouteProp, useNavigation, useRoute } from '@react-navigation/native';
import { StackNavigationProp } from '@react-navigation/stack';
import DateTimePicker from '@react-native-community/datetimepicker';
import { Ionicons } from '@expo/vector-icons';

import { MainStackParamList } from '../../navigation/navigationTypes';
import { Button } from '../../components/ui/Button';
import { Card } from '../../components/ui/Card';
import { PlatformUtils } from '../../utils/platformUtils';
import { bookingService, BookingRequest } from '../../services';

type BookSessionScreenRouteProp = RouteProp<MainStackParamList, 'BookSession'>;
type BookSessionScreenNavigationProp = StackNavigationProp<MainStackParamList, 'BookSession'>;

interface TimeSlot {
  id: string;
  time: string;
  available: boolean;
  price: number;
}

interface BookingDetails {
  skillId: string;
  instructorId: string;
  date: Date;
  timeSlot: TimeSlot | null;
  duration: number; // in minutes
  totalPrice: number;
}

const BookSessionScreen: React.FC = () => {
  const navigation = useNavigation<BookSessionScreenNavigationProp>();
  const route = useRoute<BookSessionScreenRouteProp>();
  
  const { skillId, instructorId } = route.params;

  // TODO: Fetch these from API based on IDs
  const skillTitle = "React Native Development"; // Mock data
  const instructorName = "John Smith"; // Mock data

  // State management
  const [selectedDate, setSelectedDate] = useState<Date>(new Date());
  const [showDatePicker, setShowDatePicker] = useState(false);
  const [availableSlots, setAvailableSlots] = useState<TimeSlot[]>([]);
  const [selectedSlot, setSelectedSlot] = useState<TimeSlot | null>(null);
  const [duration, setDuration] = useState<number>(60); // Default 1 hour
  const [loading, setLoading] = useState(false);
  const [bookingDetails, setBookingDetails] = useState<BookingDetails>({
    skillId,
    instructorId: 'instructor_123', // TODO: Get from route params
    date: new Date(),
    timeSlot: null,
    duration: 60,
    totalPrice: 0,
  });

  // Duration options
  const durationOptions = [
    { value: 30, label: '30 min', price: 25 },
    { value: 60, label: '1 hour', price: 45 },
    { value: 90, label: '1.5 hours', price: 65 },
    { value: 120, label: '2 hours', price: 80 },
  ];

  // Load available time slots for selected date
  useEffect(() => {
    loadAvailableSlots(selectedDate);
  }, [selectedDate]);

  // Update total price when duration or slot changes
  useEffect(() => {
    if (selectedSlot) {
      const durationOption = durationOptions.find(opt => opt.value === duration);
      const totalPrice = durationOption?.price || 0;
      setBookingDetails(prev => ({
        ...prev,
        date: selectedDate,
        timeSlot: selectedSlot,
        duration,
        totalPrice,
      }));
    }
  }, [selectedSlot, duration, selectedDate]);

  const loadAvailableSlots = async (date: Date) => {
    setLoading(true);
    try {
      // TODO: Replace with actual API call
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 500));
      
      // Mock available slots
      const mockSlots: TimeSlot[] = [
        { id: '1', time: '09:00', available: true, price: 45 },
        { id: '2', time: '10:30', available: true, price: 45 },
        { id: '3', time: '12:00', available: false, price: 45 },
        { id: '4', time: '14:00', available: true, price: 45 },
        { id: '5', time: '15:30', available: true, price: 45 },
        { id: '6', time: '17:00', available: date.getDay() !== 0, price: 45 }, // Not available on Sundays
        { id: '7', time: '18:30', available: true, price: 45 },
        { id: '8', time: '20:00', available: date.getDay() < 6, price: 45 }, // Not available on weekends
      ];
      
      setAvailableSlots(mockSlots);
    } catch (error) {
      console.error('Error loading available slots:', error);
      Alert.alert('Error', 'Failed to load available time slots');
    } finally {
      setLoading(false);
    }
  };

  const handleDateChange = (event: any, selectedDate?: Date) => {
    setShowDatePicker(Platform.OS === 'ios');
    if (selectedDate) {
      setSelectedDate(selectedDate);
      setSelectedSlot(null); // Reset selected slot when date changes
    }
  };

  const handleSlotSelect = (slot: TimeSlot) => {
    if (slot.available) {
      setSelectedSlot(slot);
    }
  };

  const handleDurationSelect = (selectedDuration: number) => {
    setDuration(selectedDuration);
  };

  const handleBookSession = async () => {
    if (!selectedSlot) {
      Alert.alert('Error', 'Please select a time slot');
      return;
    }

    setLoading(true);
    try {
      const bookingRequest: BookingRequest = {
        skillId,
        instructorId,
        date: selectedDate.toISOString().split('T')[0], // YYYY-MM-DD format
        timeSlot: selectedSlot.time,
        duration: duration,
        notes: 'Looking forward to the session!'
      };

      const booking = await bookingService.createBooking(bookingRequest);
      
      if (booking) {
        Alert.alert(
          'Booking Confirmed!',
          `Your session with ${instructorName} for ${skillTitle} has been booked for ${selectedDate.toLocaleDateString()} at ${selectedSlot.time}. Booking ID: ${booking.id}`,
          [
            {
              text: 'OK',
              onPress: () => navigation.navigate('Home'),
            }
          ]
        );
      } else {
        throw new Error('Booking failed');
      }
    } catch (error) {
      console.error('Error booking session:', error);
      Alert.alert('Error', 'Failed to book session. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (date: Date) => {
    return date.toLocaleDateString('en-US', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

  const getMinDate = () => {
    const today = new Date();
    return today;
  };

  const getMaxDate = () => {
    const maxDate = new Date();
    maxDate.setMonth(maxDate.getMonth() + 3); // 3 months ahead
    return maxDate;
  };

  return (
    <View style={styles.container}>
      <ScrollView style={styles.scrollView} showsVerticalScrollIndicator={false}>
        {/* Header */}
        <Card style={styles.headerCard}>
          <Text style={styles.title}>Book a Session</Text>
          <Text style={styles.subtitle}>
            {skillTitle} with {instructorName}
          </Text>
        </Card>

        {/* Date Selection */}
        <Card style={styles.sectionCard}>
          <Text style={styles.sectionTitle}>Select Date</Text>
          <TouchableOpacity
            style={styles.dateSelector}
            onPress={() => setShowDatePicker(true)}
          >
            <Ionicons name="calendar-outline" size={24} color="#007AFF" />
            <Text style={styles.dateText}>{formatDate(selectedDate)}</Text>
            <Ionicons name="chevron-down" size={20} color="#666" />
          </TouchableOpacity>

          {showDatePicker && (
            <DateTimePicker
              value={selectedDate}
              mode="date"
              display={Platform.OS === 'ios' ? 'spinner' : 'default'}
              onChange={handleDateChange}
              minimumDate={getMinDate()}
              maximumDate={getMaxDate()}
              style={PlatformUtils.isIOS ? styles.iosDatePicker : undefined}
            />
          )}
        </Card>

        {/* Duration Selection */}
        <Card style={styles.sectionCard}>
          <Text style={styles.sectionTitle}>Session Duration</Text>
          <View style={styles.durationContainer}>
            {durationOptions.map((option) => (
              <TouchableOpacity
                key={option.value}
                style={[
                  styles.durationOption,
                  duration === option.value && styles.durationOptionSelected,
                ]}
                onPress={() => handleDurationSelect(option.value)}
              >
                <Text style={[
                  styles.durationText,
                  duration === option.value && styles.durationTextSelected,
                ]}>
                  {option.label}
                </Text>
                <Text style={[
                  styles.durationPrice,
                  duration === option.value && styles.durationPriceSelected,
                ]}>
                  ${option.price}
                </Text>
              </TouchableOpacity>
            ))}
          </View>
        </Card>

        {/* Time Slots */}
        <Card style={styles.sectionCard}>
          <Text style={styles.sectionTitle}>Available Time Slots</Text>
          {loading ? (
            <View style={styles.loadingContainer}>
              <Text style={styles.loadingText}>Loading available slots...</Text>
            </View>
          ) : (
            <View style={styles.slotsContainer}>
              {availableSlots.map((slot) => (
                <TouchableOpacity
                  key={slot.id}
                  style={[
                    styles.timeSlot,
                    !slot.available && styles.timeSlotUnavailable,
                    selectedSlot?.id === slot.id && styles.timeSlotSelected,
                  ]}
                  onPress={() => handleSlotSelect(slot)}
                  disabled={!slot.available}
                >
                  <Text style={[
                    styles.timeSlotText,
                    !slot.available && styles.timeSlotTextUnavailable,
                    selectedSlot?.id === slot.id && styles.timeSlotTextSelected,
                  ]}>
                    {slot.time}
                  </Text>
                </TouchableOpacity>
              ))}
            </View>
          )}
        </Card>

        {/* Booking Summary */}
        {selectedSlot && (
          <Card style={styles.summaryCard}>
            <Text style={styles.sectionTitle}>Booking Summary</Text>
            <View style={styles.summaryRow}>
              <Text style={styles.summaryLabel}>Date:</Text>
              <Text style={styles.summaryValue}>{formatDate(selectedDate)}</Text>
            </View>
            <View style={styles.summaryRow}>
              <Text style={styles.summaryLabel}>Time:</Text>
              <Text style={styles.summaryValue}>{selectedSlot.time}</Text>
            </View>
            <View style={styles.summaryRow}>
              <Text style={styles.summaryLabel}>Duration:</Text>
              <Text style={styles.summaryValue}>{duration} minutes</Text>
            </View>
            <View style={[styles.summaryRow, styles.totalRow]}>
              <Text style={styles.totalLabel}>Total:</Text>
              <Text style={styles.totalValue}>${bookingDetails.totalPrice}</Text>
            </View>
          </Card>
        )}
      </ScrollView>

      {/* Book Button */}
      <View style={styles.buttonContainer}>
        <Button
          title="Confirm Booking"
          onPress={handleBookSession}
          disabled={!selectedSlot}
          loading={loading}
          style={StyleSheet.flatten([
            styles.bookButton, 
            !selectedSlot && styles.bookButtonDisabled
          ])}
        />
      </View>
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
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#1A1A1A',
    marginBottom: 4,
  },
  subtitle: {
    fontSize: 16,
    color: '#666',
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
  dateSelector: {
    flexDirection: 'row',
    alignItems: 'center',
    padding: 12,
    backgroundColor: '#F8F8F8',
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#E0E0E0',
  },
  dateText: {
    flex: 1,
    fontSize: 16,
    color: '#1A1A1A',
    marginLeft: 12,
  },
  iosDatePicker: {
    marginTop: 12,
  },
  durationContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
  },
  durationOption: {
    flex: 1,
    minWidth: '45%',
    padding: 12,
    backgroundColor: '#F8F8F8',
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#E0E0E0',
    alignItems: 'center',
  },
  durationOptionSelected: {
    backgroundColor: '#007AFF',
    borderColor: '#007AFF',
  },
  durationText: {
    fontSize: 14,
    fontWeight: '600',
    color: '#1A1A1A',
    marginBottom: 2,
  },
  durationTextSelected: {
    color: '#FFFFFF',
  },
  durationPrice: {
    fontSize: 12,
    color: '#666',
  },
  durationPriceSelected: {
    color: '#FFFFFF',
  },
  loadingContainer: {
    padding: 20,
    alignItems: 'center',
  },
  loadingText: {
    fontSize: 16,
    color: '#666',
  },
  slotsContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
  },
  timeSlot: {
    paddingHorizontal: 16,
    paddingVertical: 10,
    backgroundColor: '#F8F8F8',
    borderRadius: 20,
    borderWidth: 1,
    borderColor: '#E0E0E0',
    minWidth: 80,
    alignItems: 'center',
  },
  timeSlotSelected: {
    backgroundColor: '#007AFF',
    borderColor: '#007AFF',
  },
  timeSlotUnavailable: {
    backgroundColor: '#F0F0F0',
    borderColor: '#D0D0D0',
    opacity: 0.5,
  },
  timeSlotText: {
    fontSize: 14,
    fontWeight: '500',
    color: '#1A1A1A',
  },
  timeSlotTextSelected: {
    color: '#FFFFFF',
  },
  timeSlotTextUnavailable: {
    color: '#999',
  },
  summaryCard: {
    backgroundColor: '#E3F2FD',
    borderWidth: 1,
    borderColor: '#007AFF',
  },
  summaryRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 4,
  },
  summaryLabel: {
    fontSize: 16,
    color: '#666',
  },
  summaryValue: {
    fontSize: 16,
    fontWeight: '500',
    color: '#1A1A1A',
  },
  totalRow: {
    borderTopWidth: 1,
    borderTopColor: '#007AFF',
    marginTop: 8,
    paddingTop: 8,
  },
  totalLabel: {
    fontSize: 18,
    fontWeight: '600',
    color: '#007AFF',
  },
  totalValue: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#007AFF',
  },
  buttonContainer: {
    padding: 16,
    backgroundColor: '#FFFFFF',
    borderTopWidth: 1,
    borderTopColor: '#E0E0E0',
    ...(PlatformUtils.isIOS ? {
      shadowColor: '#000',
      shadowOffset: { width: 0, height: -2 },
      shadowOpacity: 0.1,
      shadowRadius: 4,
    } : {
      elevation: 4,
    }),
  },
  bookButton: {
    width: '100%',
  },
  bookButtonDisabled: {
    opacity: 0.5,
  },
});

export default BookSessionScreen;
