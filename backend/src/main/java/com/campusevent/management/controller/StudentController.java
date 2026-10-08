package com.campusevent.management.controller;

import com.campusevent.management.dto.request.UpdateProfileRequest;
import com.campusevent.management.dto.response.RegistrationResponse;
import com.campusevent.management.dto.response.StudentDashboardResponse;
import com.campusevent.management.dto.response.StudentProfileResponse;
import com.campusevent.management.dto.response.UserResponse;
import com.campusevent.management.service.RegistrationService;
import com.campusevent.management.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final RegistrationService registrationService;

    @GetMapping("/{studentId}/dashboard")
    public ResponseEntity<StudentDashboardResponse> getStudentDashboard(@PathVariable Long studentId) {
        StudentDashboardResponse dashboard = studentService.getStudentDashboard(studentId);
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/{studentId}/profile")
    public ResponseEntity<StudentProfileResponse> getStudentProfile(@PathVariable Long studentId) {
        StudentProfileResponse profile = studentService.getStudentProfile(studentId);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{studentId}/profile")
    public ResponseEntity<UserResponse> updateStudentProfile(
            @PathVariable Long studentId,
            @RequestBody UpdateProfileRequest request) {

        UserResponse updated = studentService.updateStudentProfile(studentId, request);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{studentId}/registrations")
    public ResponseEntity<List<RegistrationResponse>> getStudentRegistrations(@PathVariable Long studentId) {
        List<RegistrationResponse> registrations = registrationService.getStudentRegistrations(studentId);
        return ResponseEntity.ok(registrations);
    }
}
