import apiClient from './api';
import { CampusEvent } from '../types';

export interface CategorySummaryItem {
  category: string;
  count: number;
  registrations: number;
  capacity: number;
  fillRate: number;
}

export interface FacultyDashboardData {
  totalEvents: number;
  upcomingEvents: number;
  totalRegistrations: number;
  completedEvents: number;
  categorySummary: CategorySummaryItem[];
  recentEvents: CampusEvent[];
}

export interface RegisteredStudentItem {
  registrationId: number;
  studentId: number;
  studentName: string;
  email: string;
  phone: string;
  registrationDate: string;
  status: string;
}

export const facultyService = {
  // GET /api/faculty/dashboard
  async getFacultyDashboard(): Promise<FacultyDashboardData | null> {
    try {
      const response = await apiClient.get<FacultyDashboardData>('/faculty/dashboard');
      return response.data;
    } catch (error) {
      console.info('[FacultyService] Faculty dashboard endpoint fallback.');
      return null;
    }
  },

  // GET /api/faculty/events
  async getFacultyEvents(): Promise<CampusEvent[]> {
    const response = await apiClient.get<CampusEvent[]>('/faculty/events');
    return response.data;
  },

  // POST /api/faculty/events
  async createFacultyEvent(eventData: any): Promise<CampusEvent> {
    const response = await apiClient.post<CampusEvent>('/faculty/events', eventData);
    return response.data;
  },

  // PUT /api/faculty/events/{eventId}
  async updateFacultyEvent(eventId: string | number, updates: any): Promise<CampusEvent> {
    const response = await apiClient.put<CampusEvent>(`/faculty/events/${eventId}`, updates);
    return response.data;
  },

  // DELETE /api/faculty/events/{eventId}
  async deleteFacultyEvent(eventId: string | number): Promise<void> {
    await apiClient.delete(`/faculty/events/${eventId}`);
  },

  // GET /api/faculty/events/{eventId}/registrations
  async getRegisteredStudentsForEvent(eventId: string | number): Promise<RegisteredStudentItem[]> {
    const response = await apiClient.get<RegisteredStudentItem[]>(`/faculty/events/${eventId}/registrations`);
    return response.data;
  },
};

export default facultyService;
