package com.campusevent.management.controller;

import com.campusevent.management.dto.request.CreateEventRequest;
import com.campusevent.management.dto.request.UpdateEventRequest;
import com.campusevent.management.dto.response.ApiResponse;
import com.campusevent.management.dto.response.EventResponse;
import com.campusevent.management.dto.response.RegistrationResponse;
import com.campusevent.management.entity.EventCategory;
import com.campusevent.management.entity.EventStatus;
import com.campusevent.management.entity.User;
import com.campusevent.management.repository.UserRepository;
import com.campusevent.management.service.EventService;
import com.campusevent.management.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final RegistrationService registrationService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) EventCategory category,
            @RequestParam(required = false) String venue,
            @RequestParam(required = false) EventStatus status) {

        List<EventResponse> events = eventService.getAllEvents(search, category, venue, status);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable Long eventId) {
        EventResponse event = eventService.getEventById(eventId);
        return ResponseEntity.ok(event);
    }

    @PostMapping("/{eventId}/register")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<RegistrationResponse> registerForEvent(
            @PathVariable Long eventId,
            Authentication authentication) {

        User student = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        RegistrationResponse response = registrationService.registerStudentForEvent(student.getId(), eventId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody CreateEventRequest request) {
        EventResponse response = eventService.createEvent(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{eventId}")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventRequest request) {

        EventResponse response = eventService.updateEvent(eventId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{eventId}")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.ok(ApiResponse.success("Event successfully deleted"));
    }
}
