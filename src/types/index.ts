export type UserRole = 'student' | 'faculty' | 'STUDENT' | 'FACULTY';

export interface User {
  id: string | number;
  name: string;
  email: string;
  role: UserRole;
  phone: string;
  department: string;
  year?: string; // for students (e.g. "3rd Year (Junior)")
  designation?: string; // for faculty (e.g. "Associate Dean of Student Activities")
  avatar?: string;
}

export type EventCategory = 'Technical' | 'Cultural' | 'Sports' | 'Academic' | 'Other' | 'TECHNICAL' | 'CULTURAL' | 'SPORTS' | 'ACADEMIC' | 'OTHER';

export type EventStatus = 
  | 'Upcoming' 
  | 'Registration Open' 
  | 'Registration Closed' 
  | 'Event Full' 
  | 'Completed' 
  | 'Cancelled'
  | 'UPCOMING'
  | 'ONGOING'
  | 'COMPLETED'
  | 'CANCELLED';

export interface CampusEvent {
  id: string | number;
  title: string;
  description: string;
  category: EventCategory;
  date: string; // YYYY-MM-DD
  eventDate?: string;
  time: string; // e.g. "09:30 AM"
  eventTime?: string;
  venue: string;
  organizer: string;
  capacity: number;
  registeredCount: number;
  availableSeats?: number;
  deadline: string; // YYYY-MM-DD
  registrationDeadline?: string;
  imageUrl: string;
  status: EventStatus;
}

export type RegistrationStatus = 'Registered' | 'Cancelled' | 'Completed' | 'REGISTERED' | 'CANCELLED';

export interface EventRegistration {
  id: string | number;
  eventId: string | number;
  eventTitle: string;
  eventDate: string;
  eventTime: string;
  eventVenue: string;
  studentId: string | number;
  studentName: string;
  studentEmail: string;
  studentPhone: string;
  registrationDate: string;
  status: RegistrationStatus;
}

export interface ToastMessage {
  id: string;
  type: 'success' | 'error' | 'info' | 'warning';
  message: string;
}
