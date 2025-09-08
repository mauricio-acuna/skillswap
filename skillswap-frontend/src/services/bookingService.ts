import { apiClient, ApiResponse } from './apiClient';

// Types for bookings
export interface BookingRequest {
  skillId: string;
  instructorId: string;
  date: string; // ISO date string
  timeSlot: string; // e.g., "14:00"
  duration: number; // in minutes
  notes?: string;
}

export interface Booking {
  id: string;
  skillId: string;
  skillTitle: string;
  instructorId: string;
  instructorName: string;
  studentId: string;
  studentName: string;
  date: string;
  timeSlot: string;
  duration: number;
  status: 'pending' | 'confirmed' | 'in_progress' | 'completed' | 'cancelled';
  price: number;
  notes?: string;
  createdAt: string;
  updatedAt: string;
}

export interface TimeSlot {
  id: string;
  time: string;
  available: boolean;
  price: number;
}

export interface AvailabilityRequest {
  instructorId: string;
  date: string; // ISO date string
  skillId?: string;
}

class BookingService {
  async createBooking(bookingData: BookingRequest): Promise<Booking | null> {
    try {
      const response = await apiClient.post<Booking>('/bookings', bookingData);
      
      if (response.success) {
        return response.data;
      }

      // Mock successful booking for development
      return this.createMockBooking(bookingData);
    } catch (error) {
      console.error('Error creating booking:', error);
      // Return mock booking as fallback
      return this.createMockBooking(bookingData);
    }
  }

  async getBookingById(bookingId: string): Promise<Booking | null> {
    try {
      const response = await apiClient.get<Booking>(`/bookings/${bookingId}`);
      
      if (response.success) {
        return response.data;
      }

      return this.getMockBookingById(bookingId);
    } catch (error) {
      console.error('Error fetching booking:', error);
      return this.getMockBookingById(bookingId);
    }
  }

  async getUserBookings(userId: string): Promise<Booking[]> {
    try {
      const response = await apiClient.get<Booking[]>(`/users/${userId}/bookings`);
      
      if (response.success) {
        return response.data;
      }

      return this.getMockUserBookings(userId);
    } catch (error) {
      console.error('Error fetching user bookings:', error);
      return this.getMockUserBookings(userId);
    }
  }

  async getInstructorBookings(instructorId: string): Promise<Booking[]> {
    try {
      const response = await apiClient.get<Booking[]>(`/instructors/${instructorId}/bookings`);
      
      if (response.success) {
        return response.data;
      }

      return this.getMockInstructorBookings(instructorId);
    } catch (error) {
      console.error('Error fetching instructor bookings:', error);
      return this.getMockInstructorBookings(instructorId);
    }
  }

  async getAvailableSlots(availabilityData: AvailabilityRequest): Promise<TimeSlot[]> {
    try {
      const params = new URLSearchParams({
        instructorId: availabilityData.instructorId,
        date: availabilityData.date,
        ...(availabilityData.skillId && { skillId: availabilityData.skillId }),
      });

      const response = await apiClient.get<TimeSlot[]>(
        `/availability/slots?${params.toString()}`
      );
      
      if (response.success) {
        return response.data;
      }

      return this.getMockAvailableSlots(availabilityData);
    } catch (error) {
      console.error('Error fetching available slots:', error);
      return this.getMockAvailableSlots(availabilityData);
    }
  }

  async updateBookingStatus(
    bookingId: string, 
    status: Booking['status']
  ): Promise<Booking | null> {
    try {
      const response = await apiClient.patch<Booking>(
        `/bookings/${bookingId}`, 
        { status }
      );
      
      if (response.success) {
        return response.data;
      }

      throw new Error(response.error || 'Failed to update booking status');
    } catch (error) {
      console.error('Error updating booking status:', error);
      throw error;
    }
  }

  async cancelBooking(bookingId: string, reason?: string): Promise<boolean> {
    try {
      const response = await apiClient.patch(
        `/bookings/${bookingId}/cancel`, 
        { reason }
      );
      
      return response.success;
    } catch (error) {
      console.error('Error cancelling booking:', error);
      return false;
    }
  }

  // Mock data methods for development/fallback
  private createMockBooking(bookingData: BookingRequest): Booking {
    return {
      id: `booking_${Date.now()}`,
      skillId: bookingData.skillId,
      skillTitle: 'React Native Development', // Mock skill title
      instructorId: bookingData.instructorId,
      instructorName: 'John Smith', // Mock instructor name
      studentId: 'current_user_123',
      studentName: 'Current User',
      date: bookingData.date,
      timeSlot: bookingData.timeSlot,
      duration: bookingData.duration,
      status: 'pending',
      price: 45, // Mock price based on duration
      notes: bookingData.notes,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    };
  }

  private getMockBookingById(bookingId: string): Booking | null {
    // Mock booking data
    return {
      id: bookingId,
      skillId: 'skill_1',
      skillTitle: 'React Native Development',
      instructorId: 'instructor_1',
      instructorName: 'John Smith',
      studentId: 'current_user_123',
      studentName: 'Current User',
      date: '2024-02-15',
      timeSlot: '14:00',
      duration: 60,
      status: 'confirmed',
      price: 45,
      notes: 'Looking forward to learning React Native!',
      createdAt: '2024-02-10T10:00:00Z',
      updatedAt: '2024-02-10T10:30:00Z',
    };
  }

  private getMockUserBookings(userId: string): Booking[] {
    return [
      {
        id: 'booking_1',
        skillId: 'skill_1',
        skillTitle: 'React Native Development',
        instructorId: 'instructor_1',
        instructorName: 'Sarah Johnson',
        studentId: userId,
        studentName: 'Current User',
        date: '2024-02-15',
        timeSlot: '14:00',
        duration: 60,
        status: 'confirmed',
        price: 45,
        createdAt: '2024-02-10T10:00:00Z',
        updatedAt: '2024-02-10T10:30:00Z',
      },
      {
        id: 'booking_2',
        skillId: 'skill_2',
        skillTitle: 'Spanish Conversation',
        instructorId: 'instructor_2',
        instructorName: 'Carlos Rodriguez',
        studentId: userId,
        studentName: 'Current User',
        date: '2024-02-20',
        timeSlot: '16:00',
        duration: 45,
        status: 'pending',
        price: 30,
        createdAt: '2024-02-12T14:00:00Z',
        updatedAt: '2024-02-12T14:00:00Z',
      },
    ];
  }

  private getMockInstructorBookings(instructorId: string): Booking[] {
    return [
      {
        id: 'booking_3',
        skillId: 'skill_1',
        skillTitle: 'React Native Development',
        instructorId: instructorId,
        instructorName: 'Current User',
        studentId: 'student_1',
        studentName: 'Jane Doe',
        date: '2024-02-16',
        timeSlot: '10:00',
        duration: 60,
        status: 'confirmed',
        price: 45,
        createdAt: '2024-02-11T09:00:00Z',
        updatedAt: '2024-02-11T09:30:00Z',
      },
    ];
  }

  private getMockAvailableSlots(availabilityData: AvailabilityRequest): TimeSlot[] {
    const selectedDate = new Date(availabilityData.date);
    const dayOfWeek = selectedDate.getDay();
    
    // Mock availability based on day of week
    const baseSlots: TimeSlot[] = [
      { id: '1', time: '09:00', available: true, price: 45 },
      { id: '2', time: '10:30', available: true, price: 45 },
      { id: '3', time: '12:00', available: false, price: 45 },
      { id: '4', time: '14:00', available: true, price: 45 },
      { id: '5', time: '15:30', available: true, price: 45 },
      { id: '6', time: '17:00', available: dayOfWeek !== 0, price: 45 }, // Not available on Sundays
      { id: '7', time: '18:30', available: true, price: 45 },
      { id: '8', time: '20:00', available: dayOfWeek < 6, price: 45 }, // Not available on weekends
    ];

    return baseSlots;
  }
}

export const bookingService = new BookingService();
