package com.campusevent.management.controller;

import com.campusevent.management.dto.request.RegistrationRequest;
import com.campusevent.management.dto.response.ApiResponse;
import com.campusevent.management.dto.response.RegisteredStudentResponse;
import com.campusevent.management.dto.response.RegistrationResponse;
import com.campusevent.management.entity.Role;
import com.campusevent.management.entity.User;
import com.campusevent.management.repository.UserRepository;
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
@RequestMapping("/api/registrations")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<RegistrationResponse> createRegistration(
            @Valid @RequestBody RegistrationRequest request,
            Authentication authentication) {

        User student = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        RegistrationResponse response = registrationService.registerStudentForEvent(student.getId(), request.getEventId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<RegistrationResponse>> getStudentRegistrations(@PathVariable Long studentId) {
        List<RegistrationResponse> registrations = registrationService.getStudentRegistrations(studentId);
        return ResponseEntity.ok(registrations);
    }

    @GetMapping("/event/{eventId}")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<List<RegisteredStudentResponse>> getEventRegistrations(@PathVariable Long eventId) {
        List<RegisteredStudentResponse> students = registrationService.getRegisteredStudentsForEvent(eventId);
        return ResponseEntity.ok(students);
    }

    @DeleteMapping("/{registrationId}")
    public ResponseEntity<ApiResponse<Void>> cancelRegistration(
            @PathVariable Long registrationId,
            Authentication authentication) {

        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        boolean isFaculty = currentUser.getRole() == Role.FACULTY;
        registrationService.cancelRegistration(registrationId, currentUser.getId(), isFaculty);

        return ResponseEntity.ok(ApiResponse.success("Registration successfully cancelled"));
    }

    @PutMapping("/{registrationId}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelRegistrationPut(
            @PathVariable Long registrationId,
            Authentication authentication) {

        return cancelRegistration(registrationId, authentication);
    }
}
