package com.campusevent.management.controller;

import com.campusevent.management.dto.request.CreateEventRequest;
import com.campusevent.management.dto.request.UpdateEventRequest;
import com.campusevent.management.dto.response.ApiResponse;
import com.campusevent.management.dto.response.EventResponse;
import com.campusevent.management.dto.response.FacultyDashboardResponse;
import com.campusevent.management.dto.response.RegisteredStudentResponse;
import com.campusevent.management.service.FacultyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/faculty")
@PreAuthorize("hasRole('FACULTY')")
@RequiredArgsConstructor
public class FacultyController {

    private final FacultyService facultyService;

    @GetMapping("/dashboard")
    public ResponseEntity<FacultyDashboardResponse> getFacultyDashboard() {
        FacultyDashboardResponse response = facultyService.getFacultyDashboard();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/events")
    public ResponseEntity<List<EventResponse>> getFacultyEvents() {
        List<EventResponse> events = facultyService.getFacultyEvents();
        return ResponseEntity.ok(events);
    }

    @PostMapping("/events")
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody CreateEventRequest request) {
        EventResponse response = facultyService.createFacultyEvent(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/events/{eventId}")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventRequest request) {

        EventResponse response = facultyService.updateFacultyEvent(eventId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable Long eventId) {
        facultyService.deleteFacultyEvent(eventId);
        return ResponseEntity.ok(ApiResponse.success("Event successfully deleted from campus catalog"));
    }

    @GetMapping("/events/{eventId}/registrations")
    public ResponseEntity<List<RegisteredStudentResponse>> getRegisteredStudents(@PathVariable Long eventId) {
        List<RegisteredStudentResponse> students = facultyService.getRegisteredStudentsForEvent(eventId);
        return ResponseEntity.ok(students);
    }
}
