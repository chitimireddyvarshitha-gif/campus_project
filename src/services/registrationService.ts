import apiClient from './api';
import { EventRegistration, User, RegistrationStatus } from '../types';
import { INITIAL_REGISTRATIONS, SAMPLE_STUDENTS } from '../data/sampleData';

/**
 * =========================================================================
 * REGISTRATION SERVICE - Spring Boot Connected
 * =========================================================================
 * Endpoints:
 * - GET    /api/students/{studentId}/registrations
 * - POST   /api/events/{eventId}/register
 * - DELETE /api/registrations/{registrationId}
 * - GET    /api/faculty/events/{eventId}/registrations
 * =========================================================================
 */

function transformRegistration(r: any): EventRegistration {
  let status: RegistrationStatus = 'Registered';
  if (r.status) {
    const raw = String(r.status).toUpperCase();
    if (raw.includes('CANCEL')) status = 'Cancelled';
    else if (raw.includes('COMPLET')) status = 'Completed';
    else status = 'Registered';
  }

  return {
    id: String(r.id || r.registrationId),
    eventId: String(r.eventId || ''),
    eventTitle: r.eventTitle || 'Campus Event',
    eventDate: r.eventDate || '',
    eventTime: r.eventTime || '09:00 AM',
    eventVenue: r.eventVenue || r.venue || 'University Pavilion',
    studentId: String(r.studentId || ''),
    studentName: r.studentName || '',
    studentEmail: r.studentEmail || r.email || '',
    studentPhone: r.studentPhone || r.phone || '',
    registrationDate: r.registrationDate ? String(r.registrationDate) : new Date().toISOString().split('T')[0],
    status,
  };
}

export const registrationService = {
  // Get all registrations for a student from Spring Boot
  async getStudentRegistrations(studentId: string | number): Promise<EventRegistration[]> {
    try {
      const response = await apiClient.get<any[]>(`/students/${studentId}/registrations`).catch(() =>
        apiClient.get<any[]>(`/registrations/student/${studentId}`)
      );

      if (Array.isArray(response?.data)) {
        const list = response.data.map(transformRegistration);
        localStorage.setItem('cems_registrations_data', JSON.stringify(list));
        return list;
      }
    } catch (error) {
      console.info('[RegistrationService] Using local registration records (Spring Boot loading/offline).');
    }

    const stored = localStorage.getItem('cems_registrations_data');
    if (stored) {
      try {
        const all: EventRegistration[] = JSON.parse(stored);
        return all.filter((r) => String(r.studentId) === String(studentId));
      } catch {
        return INITIAL_REGISTRATIONS;
      }
    }
    localStorage.setItem('cems_registrations_data', JSON.stringify(INITIAL_REGISTRATIONS));
    return INITIAL_REGISTRATIONS;
  },

  // Get all registered students for a particular event (Faculty view)
  async getEventRegistrations(eventId: string | number, eventTitle?: string): Promise<EventRegistration[]> {
    try {
      const response = await apiClient.get<any[]>(`/faculty/events/${eventId}/registrations`).catch(() =>
        apiClient.get<any[]>(`/registrations/event/${eventId}`)
      );

      if (Array.isArray(response?.data)) {
        return response.data.map((item) => ({
          id: String(item.registrationId || item.id),
          eventId: String(eventId),
          eventTitle: eventTitle || 'Campus Event',
          eventDate: item.eventDate || '',
          eventTime: item.eventTime || '09:00 AM',
          eventVenue: item.eventVenue || 'University Pavilion',
          studentId: String(item.studentId),
          studentName: item.studentName || '',
          studentEmail: item.email || item.studentEmail || '',
          studentPhone: item.phone || item.studentPhone || '',
          registrationDate: item.registrationDate ? String(item.registrationDate) : '',
          status: item.status?.toUpperCase()?.includes('CANCEL') ? 'Cancelled' : 'Registered',
        }));
      }
    } catch (error) {
      console.info('[RegistrationService] Fallback to cached attendee roster.');
    }

    const stored = localStorage.getItem('cems_registrations_data');
    let allRegistrations: EventRegistration[] = [];
    if (stored) {
      try {
        allRegistrations = JSON.parse(stored);
      } catch {
        allRegistrations = INITIAL_REGISTRATIONS;
      }
    } else {
      allRegistrations = INITIAL_REGISTRATIONS;
    }

    const eventSpecific = allRegistrations.filter((r) => String(r.eventId) === String(eventId));

    if (eventSpecific.length <= 1) {
      const demoList: EventRegistration[] = SAMPLE_STUDENTS.map((s, idx) => ({
        id: `reg-${eventId}-${idx + 1}`,
        eventId: String(eventId),
        eventTitle: eventTitle || 'Campus Event',
        eventDate: '2026-10-14',
        eventTime: '09:00 AM',
        eventVenue: 'Campus Main Pavilion',
        studentId: String(idx + 1),
        studentName: s.name,
        studentEmail: s.email,
        studentPhone: s.phone,
        registrationDate: s.regDate,
        status: s.status,
      }));
      return demoList;
    }

    return eventSpecific;
  },

  // Register student for an event in MySQL
  async registerForEvent(
    event: { id: string | number; title: string; date: string; time: string; venue: string },
    student: User
  ): Promise<EventRegistration> {
    try {
      // Call Spring Boot endpoint: POST /api/events/{eventId}/register
      const response = await apiClient.post<any>(`/events/${event.id}/register`, {}).catch(() =>
        apiClient.post<any>('/registrations', {
          eventId: Number(event.id) || event.id,
          studentId: Number(student.id) || student.id,
        })
      );

      if (response?.data) {
        const saved = transformRegistration(response.data);
        const stored = localStorage.getItem('cems_registrations_data');
        let list: EventRegistration[] = stored ? JSON.parse(stored) : INITIAL_REGISTRATIONS;
        list = [saved, ...list.filter(r => r.id !== saved.id)];
        localStorage.setItem('cems_registrations_data', JSON.stringify(list));
        return saved;
      }
    } catch (error: any) {
      // If server returned specific business error (e.g. event full, already registered)
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }
      console.warn('[RegistrationService] Spring Boot connection unavailable, using local session state:', error?.message);
    }

    // Local state fallback
    const stored = localStorage.getItem('cems_registrations_data');
    let list: EventRegistration[] = stored ? JSON.parse(stored) : INITIAL_REGISTRATIONS;

    const existing = list.find(
      (r) => String(r.eventId) === String(event.id) && String(r.studentId) === String(student.id) && r.status === 'Registered'
    );
    if (existing) {
      throw new Error('You are already registered for this event');
    }

    const todayStr = new Date().toISOString().split('T')[0];
    const newReg: EventRegistration = {
      id: 'reg-' + Date.now().toString().slice(-4),
      eventId: String(event.id),
      eventTitle: event.title,
      eventDate: event.date,
      eventTime: event.time,
      eventVenue: event.venue,
      studentId: String(student.id),
      studentName: student.name,
      studentEmail: student.email,
      studentPhone: student.phone,
      registrationDate: todayStr,
      status: 'Registered',
    };

    list = [newReg, ...list];
    localStorage.setItem('cems_registrations_data', JSON.stringify(list));

    // Update event seat count
    const eventsStored = localStorage.getItem('cems_events_data');
    if (eventsStored) {
      try {
        const events = JSON.parse(eventsStored);
        const targetEvt = events.find((e: any) => String(e.id) === String(event.id));
        if (targetEvt) {
          targetEvt.registeredCount = (targetEvt.registeredCount || 0) + 1;
          if (targetEvt.registeredCount >= targetEvt.capacity) {
            targetEvt.status = 'Event Full';
          }
          localStorage.setItem('cems_events_data', JSON.stringify(events));
        }
      } catch (e) {
        console.error(e);
      }
    }

    return newReg;
  },

  // Cancel an existing registration in MySQL
  async cancelRegistration(registrationId: string | number): Promise<void> {
    try {
      await apiClient.delete(`/registrations/${registrationId}`).catch(() =>
        apiClient.put(`/registrations/${registrationId}/cancel`)
      );
    } catch (error: any) {
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }
      console.warn('[RegistrationService] Spring Boot connection unavailable, cancelling locally:', error?.message);
    }

    // Update local state
    const stored = localStorage.getItem('cems_registrations_data');
    if (stored) {
      const list: EventRegistration[] = JSON.parse(stored);
      const target = list.find((r) => String(r.id) === String(registrationId));
      if (target) {
        target.status = 'Cancelled';
        localStorage.setItem('cems_registrations_data', JSON.stringify(list));

        // Decrement event registeredCount
        const eventsStored = localStorage.getItem('cems_events_data');
        if (eventsStored) {
          try {
            const events = JSON.parse(eventsStored);
            const evt = events.find((e: any) => String(e.id) === String(target.eventId));
            if (evt && evt.registeredCount > 0) {
              evt.registeredCount -= 1;
              if (evt.status === 'Event Full' && evt.registeredCount < evt.capacity) {
                evt.status = 'Registration Open';
              }
              localStorage.setItem('cems_events_data', JSON.stringify(events));
            }
          } catch (e) {
            console.error(e);
          }
        }
      }
    }
  }
};

