package com.campusevent.management.service.impl;

import com.campusevent.management.dto.request.CreateEventRequest;
import com.campusevent.management.dto.request.UpdateEventRequest;
import com.campusevent.management.dto.response.EventResponse;
import com.campusevent.management.entity.Event;
import com.campusevent.management.entity.EventCategory;
import com.campusevent.management.entity.EventStatus;
import com.campusevent.management.exception.BadRequestException;
import com.campusevent.management.exception.ResourceNotFoundException;
import com.campusevent.management.repository.EventRepository;
import com.campusevent.management.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getAllEvents(String search, EventCategory category, String venue, EventStatus status) {
        String cleanSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        String cleanVenue = (venue != null && !venue.trim().isEmpty() && !venue.equalsIgnoreCase("All")) ? venue.trim() : null;

        List<Event> events;
        if (cleanSearch == null && category == null && cleanVenue == null && status == null) {
            events = eventRepository.findAll();
        } else {
            events = eventRepository.searchAndFilterEvents(cleanSearch, category, cleanVenue, status);
        }

        return events.stream()
                .map(EventResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponse getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event with ID " + eventId + " not found."));
        return EventResponse.fromEntity(event);
    }

    @Override
    @Transactional
    public EventResponse createEvent(CreateEventRequest request) {
        if (request.getRegistrationDeadline().isAfter(request.getEventDate())) {
            throw new BadRequestException("Registration deadline cannot be after the scheduled event date.");
        }

        EventStatus eventStatus = request.getStatus() != null ? request.getStatus() : EventStatus.UPCOMING;

        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .eventDate(request.getEventDate())
                .eventTime(request.getEventTime())
                .venue(request.getVenue())
                .organizer(request.getOrganizer())
                .capacity(request.getCapacity())
                .registeredCount(0)
                .registrationDeadline(request.getRegistrationDeadline())
                .imageUrl(request.getImageUrl() != null && !request.getImageUrl().isEmpty()
                        ? request.getImageUrl()
                        : "/src/assets/images/hero_campus_portal_1790559781450.jpg")
                .status(eventStatus)
                .build();

        Event savedEvent = eventRepository.save(event);
        return EventResponse.fromEntity(savedEvent);
    }

    @Override
    @Transactional
    public EventResponse updateEvent(Long eventId, UpdateEventRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event with ID " + eventId + " not found."));

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            event.setTitle(request.getTitle());
        }
        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            event.setDescription(request.getDescription());
        }
        if (request.getCategory() != null) {
            event.setCategory(request.getCategory());
        }
        if (request.getEventDate() != null) {
            event.setEventDate(request.getEventDate());
        }
        if (request.getEventTime() != null && !request.getEventTime().isBlank()) {
            event.setEventTime(request.getEventTime());
        }
        if (request.getVenue() != null && !request.getVenue().isBlank()) {
            event.setVenue(request.getVenue());
        }
        if (request.getOrganizer() != null && !request.getOrganizer().isBlank()) {
            event.setOrganizer(request.getOrganizer());
        }
        if (request.getCapacity() != null) {
            if (request.getCapacity() < event.getRegisteredCount()) {
                throw new BadRequestException("Capacity cannot be reduced below current active registered student count (" + event.getRegisteredCount() + ")");
            }
            event.setCapacity(request.getCapacity());
        }
        if (request.getRegistrationDeadline() != null) {
            if (event.getEventDate() != null && request.getRegistrationDeadline().isAfter(event.getEventDate())) {
                throw new BadRequestException("Registration deadline cannot be after the scheduled event date.");
            }
            event.setRegistrationDeadline(request.getRegistrationDeadline());
        }
        if (request.getImageUrl() != null && !request.getImageUrl().isBlank()) {
            event.setImageUrl(request.getImageUrl());
        }
        if (request.getStatus() != null) {
            event.setStatus(request.getStatus());
        }

        Event updatedEvent = eventRepository.save(event);
        return EventResponse.fromEntity(updatedEvent);
    }

    @Override
    @Transactional
    public void deleteEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event with ID " + eventId + " does not exist.");
        }
        eventRepository.deleteById(eventId);
    }
}
