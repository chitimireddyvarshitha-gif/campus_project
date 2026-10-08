package com.campusevent.management.service.impl;

import com.campusevent.management.dto.response.RegisteredStudentResponse;
import com.campusevent.management.dto.response.RegistrationResponse;
import com.campusevent.management.entity.*;
import com.campusevent.management.exception.BadRequestException;
import com.campusevent.management.exception.DuplicateResourceException;
import com.campusevent.management.exception.EventFullException;
import com.campusevent.management.exception.ResourceNotFoundException;
import com.campusevent.management.repository.EventRepository;
import com.campusevent.management.repository.RegistrationRepository;
import com.campusevent.management.repository.UserRepository;
import com.campusevent.management.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public RegistrationResponse registerStudentForEvent(Long studentId, Long eventId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

        if (student.getRole() != Role.STUDENT) {
            throw new BadRequestException("Only students with verified enrollments are authorized to register for events.");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));

        if (event.getStatus() == EventStatus.CANCELLED) {
            throw new BadRequestException("Cannot register for an event that has been cancelled by administration.");
        }

        if (LocalDate.now().isAfter(event.getRegistrationDeadline())) {
            throw new BadRequestException("The registration deadline for this event (" + event.getRegistrationDeadline() + ") has already passed.");
        }

        // Check if student already has an active registration
        Optional<Registration> existingOpt = registrationRepository.findByStudentIdAndEventId(studentId, eventId);
        if (existingOpt.isPresent()) {
            Registration existing = existingOpt.get();
            if (existing.getStatus() == RegistrationStatus.REGISTERED) {
                throw new DuplicateResourceException("You are already actively enrolled for event: " + event.getTitle());
            }
            // Re-activate if was previously cancelled
            if (event.getRegisteredCount() >= event.getCapacity()) {
                throw new EventFullException("This event has reached its maximum seat capacity (" + event.getCapacity() + " seats).");
            }
            existing.setStatus(RegistrationStatus.REGISTERED);
            existing.setRegistrationDate(LocalDate.now());
            event.setRegisteredCount(event.getRegisteredCount() + 1);
            eventRepository.save(event);
            Registration reactivated = registrationRepository.save(existing);
            return RegistrationResponse.fromEntity(reactivated);
        }

        if (event.getRegisteredCount() >= event.getCapacity()) {
            throw new EventFullException("This event has reached its maximum seat capacity (" + event.getCapacity() + " seats).");
        }

        Registration registration = Registration.builder()
                .student(student)
                .event(event)
                .registrationDate(LocalDate.now())
                .status(RegistrationStatus.REGISTERED)
                .build();

        event.setRegisteredCount(event.getRegisteredCount() + 1);
        eventRepository.save(event);

        Registration saved = registrationRepository.save(registration);
        return RegistrationResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistrationResponse> getStudentRegistrations(Long studentId) {
        if (!userRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student with ID " + studentId + " does not exist.");
        }

        return registrationRepository.findByStudentIdWithDetails(studentId).stream()
                .map(RegistrationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelRegistration(Long registrationId, Long studentId, boolean isFaculty) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration record with ID " + registrationId + " not found."));

        if (!isFaculty && !registration.getStudent().getId().equals(studentId)) {
            throw new BadRequestException("You are not authorized to cancel another student's registration record.");
        }

        if (registration.getStatus() == RegistrationStatus.CANCELLED) {
            throw new BadRequestException("Registration record is already cancelled.");
        }

        registration.setStatus(RegistrationStatus.CANCELLED);
        registrationRepository.save(registration);

        // Decrement event registeredCount
        Event event = registration.getEvent();
        if (event != null && event.getRegisteredCount() > 0) {
            event.setRegisteredCount(event.getRegisteredCount() - 1);
            eventRepository.save(event);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegisteredStudentResponse> getRegisteredStudentsForEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event with ID " + eventId + " does not exist.");
        }

        return registrationRepository.findByEventIdWithStudent(eventId).stream()
                .map(RegisteredStudentResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
