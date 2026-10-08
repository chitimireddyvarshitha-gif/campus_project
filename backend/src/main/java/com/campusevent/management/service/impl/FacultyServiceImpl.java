package com.campusevent.management.service.impl;

import com.campusevent.management.dto.request.CreateEventRequest;
import com.campusevent.management.dto.request.UpdateEventRequest;
import com.campusevent.management.dto.response.EventResponse;
import com.campusevent.management.dto.response.FacultyDashboardResponse;
import com.campusevent.management.dto.response.RegisteredStudentResponse;
import com.campusevent.management.entity.Event;
import com.campusevent.management.entity.EventStatus;
import com.campusevent.management.entity.RegistrationStatus;
import com.campusevent.management.repository.EventRepository;
import com.campusevent.management.repository.RegistrationRepository;
import com.campusevent.management.service.EventService;
import com.campusevent.management.service.FacultyService;
import com.campusevent.management.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacultyServiceImpl implements FacultyService {

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final EventService eventService;
    private final RegistrationService registrationService;

    @Override
    @Transactional(readOnly = true)
    public FacultyDashboardResponse getFacultyDashboard() {
        long totalEvents = eventRepository.count();
        long upcomingEvents = eventRepository.countUpcomingEvents(LocalDate.now());
        long totalRegistrations = registrationRepository.countByStatus(RegistrationStatus.REGISTERED);
        long completedEvents = eventRepository.countByStatus(EventStatus.COMPLETED);

        List<Event> allEvents = eventRepository.findAll();

        // Calculate category summary
        Map<String, int[]> categoryStats = new HashMap<>(); // [count, registrations, capacity]
        for (Event event : allEvents) {
            String catName = event.getCategory() != null ? event.getCategory().name() : "OTHER";
            String formattedCategory = catName.substring(0, 1).toUpperCase() + catName.substring(1).toLowerCase();

            categoryStats.putIfAbsent(formattedCategory, new int[]{0, 0, 0});
            int[] stats = categoryStats.get(formattedCategory);
            stats[0] += 1;
            stats[1] += (event.getRegisteredCount() != null ? event.getRegisteredCount() : 0);
            stats[2] += (event.getCapacity() != null ? event.getCapacity() : 0);
        }

        List<FacultyDashboardResponse.CategorySummaryDto> categorySummary = categoryStats.entrySet().stream()
                .map(entry -> {
                    int cap = entry.getValue()[2];
                    int reg = entry.getValue()[1];
                    int fillRate = cap > 0 ? (int) Math.round(((double) reg / cap) * 100) : 0;
                    return FacultyDashboardResponse.CategorySummaryDto.builder()
                            .category(entry.getKey())
                            .count(entry.getValue()[0])
                            .registrations(reg)
                            .capacity(cap)
                            .fillRate(fillRate)
                            .build();
                })
                .collect(Collectors.toList());

        List<EventResponse> recentEvents = allEvents.stream()
                .sorted(Comparator.comparing(Event::getEventDate))
                .limit(5)
                .map(EventResponse::fromEntity)
                .collect(Collectors.toList());

        return FacultyDashboardResponse.builder()
                .totalEvents(totalEvents)
                .upcomingEvents(upcomingEvents)
                .totalRegistrations(totalRegistrations)
                .completedEvents(completedEvents)
                .categorySummary(categorySummary)
                .recentEvents(recentEvents)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getFacultyEvents() {
        return eventService.getAllEvents(null, null, null, null);
    }

    @Override
    @Transactional
    public EventResponse createFacultyEvent(CreateEventRequest request) {
        return eventService.createEvent(request);
    }

    @Override
    @Transactional
    public EventResponse updateFacultyEvent(Long eventId, UpdateEventRequest request) {
        return eventService.updateEvent(eventId, request);
    }

    @Override
    @Transactional
    public void deleteFacultyEvent(Long eventId) {
        eventService.deleteEvent(eventId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegisteredStudentResponse> getRegisteredStudentsForEvent(Long eventId) {
        return registrationService.getRegisteredStudentsForEvent(eventId);
    }
}
