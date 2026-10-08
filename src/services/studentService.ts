import apiClient from './api';
import { User, EventRegistration, CampusEvent } from '../types';

export interface StudentDashboardData {
  upcomingEventsCount: number;
  myRegistrationsCount: number;
  completedEventsCount: number;
  availableEventsCount: number;
  upcomingRegisteredEvents: EventRegistration[];
  recommendedEvents: CampusEvent[];
}

export interface StudentProfileData {
  user: User;
  activeRegistrationsCount: number;
  completedRegistrationsCount: number;
  totalRegistrationsCount: number;
}

export const studentService = {
  // GET /api/students/{studentId}/dashboard
  async getStudentDashboard(studentId: string | number): Promise<StudentDashboardData | null> {
    try {
      const response = await apiClient.get<StudentDashboardData>(`/students/${studentId}/dashboard`);
      return response.data;
    } catch (error) {
      console.info(`[StudentService] Dashboard API fallback for student #${studentId}`);
      return null;
    }
  },

  // GET /api/students/{studentId}/profile
  async getStudentProfile(studentId: string | number): Promise<StudentProfileData | null> {
    try {
      const response = await apiClient.get<StudentProfileData>(`/students/${studentId}/profile`);
      return response.data;
    } catch (error) {
      console.info(`[StudentService] Profile API fallback for student #${studentId}`);
      return null;
    }
  },

  // PUT /api/students/{studentId}/profile
  async updateStudentProfile(studentId: string | number, updates: Partial<User>): Promise<User | null> {
    try {
      const response = await apiClient.put(`/students/${studentId}/profile`, {
        name: updates.name,
        phone: updates.phone,
        department: updates.department,
        year: updates.year,
        avatar: updates.avatar,
      });
      return response.data;
    } catch (error) {
      console.info(`[StudentService] Profile update fallback for student #${studentId}`);
      return null;
    }
  },

  // GET /api/students/{studentId}/registrations
  async getStudentRegistrations(studentId: string | number): Promise<EventRegistration[]> {
    const response = await apiClient.get<EventRegistration[]>(`/students/${studentId}/registrations`);
    return response.data;
  },
};

export default studentService;
