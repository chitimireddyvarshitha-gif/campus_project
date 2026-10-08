package com.campusevent.management.service;

import com.campusevent.management.dto.request.CreateEventRequest;
import com.campusevent.management.dto.request.UpdateEventRequest;
import com.campusevent.management.dto.response.EventResponse;
import com.campusevent.management.entity.EventCategory;
import com.campusevent.management.entity.EventStatus;

import java.util.List;

public interface EventService {

    List<EventResponse> getAllEvents(String search, EventCategory category, String venue, EventStatus status);

    EventResponse getEventById(Long eventId);

    EventResponse createEvent(CreateEventRequest request);

    EventResponse updateEvent(Long eventId, UpdateEventRequest request);

    void deleteEvent(Long eventId);
}
