import apiClient from './api';
import { CampusEvent, EventCategory, EventStatus } from '../types';
import { INITIAL_EVENTS } from '../data/sampleData';

/**
 * =========================================================================
 * EVENT SERVICE - Spring Boot Connected
 * =========================================================================
 * Endpoints:
 * - GET    /api/events
 * - GET    /api/events/{id}
 * - POST   /api/faculty/events  (or /api/events)
 * - PUT    /api/faculty/events/{id}
 * - DELETE /api/faculty/events/{id}
 * =========================================================================
 */

function transformBackendEvent(e: any): CampusEvent {
  // Normalize category
  let cat: EventCategory = 'Technical';
  if (e.category) {
    const rawCat = String(e.category).toUpperCase();
    if (rawCat.includes('TECH')) cat = 'Technical';
    else if (rawCat.includes('CULT')) cat = 'Cultural';
    else if (rawCat.includes('SPORT')) cat = 'Sports';
    else if (rawCat.includes('ACAD')) cat = 'Academic';
    else cat = 'Other';
  }

  // Normalize status
  let status: EventStatus = 'Registration Open';
  if (e.status) {
    const rawStat = String(e.status).toUpperCase();
    if (rawStat.includes('CANCEL')) status = 'Cancelled';
    else if (rawStat.includes('COMPLET')) status = 'Completed';
    else if (rawStat.includes('FULL')) status = 'Event Full';
    else if (rawStat.includes('CLOSE')) status = 'Registration Closed';
    else if (e.availableSeats <= 0) status = 'Event Full';
    else status = 'Registration Open';
  }

  return {
    id: String(e.id),
    title: e.title || '',
    description: e.description || '',
    category: cat,
    date: e.date || e.eventDate || '',
    eventDate: e.eventDate || e.date,
    time: e.time || e.eventTime || '09:00 AM',
    eventTime: e.eventTime || e.time,
    venue: e.venue || '',
    organizer: e.organizer || '',
    capacity: Number(e.capacity || 0),
    registeredCount: Number(e.registeredCount || 0),
    availableSeats: e.availableSeats !== undefined ? Number(e.availableSeats) : Math.max(0, Number(e.capacity || 0) - Number(e.registeredCount || 0)),
    deadline: e.deadline || e.registrationDeadline || '',
    registrationDeadline: e.registrationDeadline || e.deadline,
    imageUrl: e.imageUrl || '/src/assets/images/hero_campus_portal_1790559781450.jpg',
    status,
  };
}

export const eventService = {
  // Fetch all events from Spring Boot
  async getAllEvents(): Promise<CampusEvent[]> {
    try {
      const response = await apiClient.get<any[]>('/events');
      if (Array.isArray(response.data)) {
        const events = response.data.map(transformBackendEvent);
        localStorage.setItem('cems_events_data', JSON.stringify(events));
        return events;
      }
    } catch (error) {
      console.info('[EventService] Using cached / fallback events (Spring Boot offline or loading).');
    }

    const stored = localStorage.getItem('cems_events_data');
    if (stored) {
      try {
        return JSON.parse(stored);
      } catch {
        return INITIAL_EVENTS;
      }
    }
    localStorage.setItem('cems_events_data', JSON.stringify(INITIAL_EVENTS));
    return INITIAL_EVENTS;
  },

  // Fetch single event by ID
  async getEventById(id: string): Promise<CampusEvent | null> {
    try {
      const response = await apiClient.get<any>(`/events/${id}`);
      if (response.data) {
        return transformBackendEvent(response.data);
      }
    } catch (error) {
      console.info(`[EventService] Event #${id} fallback lookup from cache.`);
    }

    const events = await this.getAllEvents();
    return events.find((e) => String(e.id) === String(id)) || null;
  },

  // Create new event (Faculty action)
  async createEvent(eventData: Omit<CampusEvent, 'id' | 'registeredCount'>): Promise<CampusEvent> {
    const payload = {
      title: eventData.title,
      description: eventData.description,
      category: String(eventData.category).toUpperCase(),
      eventDate: eventData.date,
      eventTime: eventData.time,
      venue: eventData.venue,
      organizer: eventData.organizer,
      capacity: Number(eventData.capacity),
      registrationDeadline: eventData.deadline,
      imageUrl: eventData.imageUrl,
      status: 'UPCOMING',
    };

    try {
      // First try faculty endpoint, then standard events endpoint
      const response = await apiClient.post<any>('/faculty/events', payload).catch(() =>
        apiClient.post<any>('/events', payload)
      );

      if (response?.data) {
        const newEvent = transformBackendEvent(response.data);
        const events = await this.getAllEvents();
        localStorage.setItem('cems_events_data', JSON.stringify([newEvent, ...events.filter(e => e.id !== newEvent.id)]));
        return newEvent;
      }
    } catch (error: any) {
      console.warn('[EventService] Spring Boot createEvent fallback to local storage:', error?.message);
    }

    // Local fallback
    const events = await this.getAllEvents();
    const newEvent: CampusEvent = {
      ...eventData,
      id: 'evt-' + String(Date.now()).slice(-4),
      registeredCount: 0,
    };
    const updated = [newEvent, ...events];
    localStorage.setItem('cems_events_data', JSON.stringify(updated));
    return newEvent;
  },

  // Update existing event (Faculty action)
  async updateEvent(id: string, updates: Partial<CampusEvent>): Promise<CampusEvent> {
    const payload: Record<string, any> = {};
    if (updates.title) payload.title = updates.title;
    if (updates.description) payload.description = updates.description;
    if (updates.category) payload.category = String(updates.category).toUpperCase();
    if (updates.date) payload.eventDate = updates.date;
    if (updates.time) payload.eventTime = updates.time;
    if (updates.venue) payload.venue = updates.venue;
    if (updates.organizer) payload.organizer = updates.organizer;
    if (updates.capacity) payload.capacity = Number(updates.capacity);
    if (updates.deadline) payload.registrationDeadline = updates.deadline;
    if (updates.imageUrl) payload.imageUrl = updates.imageUrl;
    if (updates.status) payload.status = String(updates.status).toUpperCase();

    try {
      const response = await apiClient.put<any>(`/faculty/events/${id}`, payload).catch(() =>
        apiClient.put<any>(`/events/${id}`, payload)
      );

      if (response?.data) {
        const updated = transformBackendEvent(response.data);
        const events = await this.getAllEvents();
        const replaced = events.map(e => String(e.id) === String(id) ? updated : e);
        localStorage.setItem('cems_events_data', JSON.stringify(replaced));
        return updated;
      }
    } catch (error: any) {
      console.warn('[EventService] Spring Boot updateEvent fallback to local storage:', error?.message);
    }

    const events = await this.getAllEvents();
    const index = events.findIndex((e) => String(e.id) === String(id));
    if (index === -1) {
      throw new Error(`Event with id ${id} not found`);
    }
    const updatedEvent = { ...events[index], ...updates };
    events[index] = updatedEvent;
    localStorage.setItem('cems_events_data', JSON.stringify(events));
    return updatedEvent;
  },

  // Delete event (Faculty action)
  async deleteEvent(id: string): Promise<void> {
    try {
      await apiClient.delete(`/faculty/events/${id}`).catch(() =>
        apiClient.delete(`/events/${id}`)
      );
    } catch (error: any) {
      console.warn('[EventService] Spring Boot deleteEvent fallback to local storage:', error?.message);
    }

    const events = await this.getAllEvents();
    const filtered = events.filter((e) => String(e.id) !== String(id));
    localStorage.setItem('cems_events_data', JSON.stringify(filtered));
  }
};

