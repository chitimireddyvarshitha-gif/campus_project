package com.campusevent.management.service.impl;

import com.campusevent.management.dto.request.UpdateProfileRequest;
import com.campusevent.management.dto.response.EventResponse;
import com.campusevent.management.dto.response.RegistrationResponse;
import com.campusevent.management.dto.response.StudentDashboardResponse;
import com.campusevent.management.dto.response.StudentProfileResponse;
import com.campusevent.management.dto.response.UserResponse;
import com.campusevent.management.entity.Event;
import com.campusevent.management.entity.EventStatus;
import com.campusevent.management.entity.Registration;
import com.campusevent.management.entity.RegistrationStatus;
import com.campusevent.management.entity.User;
import com.campusevent.management.exception.ResourceNotFoundException;
import com.campusevent.management.repository.EventRepository;
import com.campusevent.management.repository.RegistrationRepository;
import com.campusevent.management.repository.UserRepository;
import com.campusevent.management.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;

    @Override
    @Transactional(readOnly = true)
    public StudentDashboardResponse getStudentDashboard(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student with ID " + studentId + " not found."));

        LocalDate today = LocalDate.now();

        // Active student registrations
        List<Registration> activeRegistrations = registrationRepository.findByStudentIdAndStatus(studentId, RegistrationStatus.REGISTERED);
        long myRegistrationsCount = activeRegistrations.size();

        // Completed student registrations (events with date in past or status COMPLETED)
        long completedCount = activeRegistrations.stream()
                .filter(r -> r.getEvent().getStatus() == EventStatus.COMPLETED || r.getEvent().getEventDate().isBefore(today))
                .count();

        // Overall upcoming events
        List<Event> upcomingEvents = eventRepository.findUpcomingEvents(today);
        long upcomingEventsCount = upcomingEvents.size();

        // Available events count (capacity > registeredCount and deadline not passed)
        long availableEventsCount = upcomingEvents.stream()
                .filter(e -> e.getRegisteredCount() < e.getCapacity() && !today.isAfter(e.getRegistrationDeadline()))
                .count();

        // Upcoming registered events list
        List<RegistrationResponse> upcomingRegistered = activeRegistrations.stream()
                .filter(r -> !r.getEvent().getEventDate().isBefore(today))
                .map(RegistrationResponse::fromEntity)
                .collect(Collectors.toList());

        // Recommended events (events the student has NOT yet registered for)
        Set<Long> registeredEventIds = activeRegistrations.stream()
                .map(r -> r.getEvent().getId())
                .collect(Collectors.toSet());

        List<EventResponse> recommended = upcomingEvents.stream()
                .filter(e -> !registeredEventIds.contains(e.getId()) && e.getRegisteredCount() < e.getCapacity())
                .limit(4)
                .map(EventResponse::fromEntity)
                .collect(Collectors.toList());

        return StudentDashboardResponse.builder()
                .upcomingEventsCount(upcomingEventsCount)
                .myRegistrationsCount(myRegistrationsCount)
                .completedEventsCount(completedCount)
                .availableEventsCount(availableEventsCount)
                .upcomingRegisteredEvents(upcomingRegistered)
                .recommendedEvents(recommended)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public StudentProfileResponse getStudentProfile(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student with ID " + studentId + " not found."));

        long activeCount = registrationRepository.countByStudentIdAndStatus(studentId, RegistrationStatus.REGISTERED);
        long cancelledCount = registrationRepository.countByStudentIdAndStatus(studentId, RegistrationStatus.CANCELLED);
        long totalCount = activeCount + cancelledCount;

        return StudentProfileResponse.builder()
                .user(UserResponse.fromEntity(student))
                .activeRegistrationsCount(activeCount)
                .completedRegistrationsCount(0)
                .totalRegistrationsCount(totalCount)
                .build();
    }

    @Override
    @Transactional
    public UserResponse updateStudentProfile(Long studentId, UpdateProfileRequest request) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student with ID " + studentId + " not found."));

        if (request.getName() != null && !request.getName().isBlank()) {
            student.setName(request.getName());
        }
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            student.setPhone(request.getPhone());
        }
        if (request.getDepartment() != null && !request.getDepartment().isBlank()) {
            student.setDepartment(request.getDepartment());
        }
        if (request.getYear() != null && !request.getYear().isBlank()) {
            student.setYear(request.getYear());
        }
        if (request.getAvatar() != null && !request.getAvatar().isBlank()) {
            student.setAvatar(request.getAvatar());
        }

        User updated = userRepository.save(student);
        return UserResponse.fromEntity(updated);
    }
}
