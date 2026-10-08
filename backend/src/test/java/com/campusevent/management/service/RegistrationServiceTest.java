package com.campusevent.management.service;

import com.campusevent.management.dto.response.RegistrationResponse;
import com.campusevent.management.entity.*;
import com.campusevent.management.exception.BadRequestException;
import com.campusevent.management.exception.DuplicateResourceException;
import com.campusevent.management.exception.EventFullException;
import com.campusevent.management.repository.EventRepository;
import com.campusevent.management.repository.RegistrationRepository;
import com.campusevent.management.repository.UserRepository;
import com.campusevent.management.service.impl.RegistrationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RegistrationServiceImpl registrationService;

    private User student;
    private Event openEvent;
    private Event fullEvent;
    private Event pastDeadlineEvent;
    private Registration existingRegistration;

    @BeforeEach
    void setUp() {
        student = User.builder()
                .id(1L)
                .name("Varshitha Chitimireddy")
                .email("varshitha.c@campus.edu")
                .role(Role.STUDENT)
                .build();

        openEvent = Event.builder()
                .id(100L)
                .title("Campus Hackathon 2026")
                .capacity(50)
                .registeredCount(10)
                .registrationDeadline(LocalDate.now().plusDays(5))
                .status(EventStatus.UPCOMING)
                .build();

        fullEvent = Event.builder()
                .id(101L)
                .title("Full Capacity Workshop")
                .capacity(20)
                .registeredCount(20) // Full!
                .registrationDeadline(LocalDate.now().plusDays(5))
                .status(EventStatus.UPCOMING)
                .build();

        pastDeadlineEvent = Event.builder()
                .id(102L)
                .title("Past Deadline Symposium")
                .capacity(100)
                .registeredCount(10)
                .registrationDeadline(LocalDate.now().minusDays(1)) // Deadline passed!
                .status(EventStatus.UPCOMING)
                .build();

        existingRegistration = Registration.builder()
                .id(500L)
                .student(student)
                .event(openEvent)
                .status(RegistrationStatus.REGISTERED)
                .registrationDate(LocalDate.now())
                .build();
    }

    @Test
    void registerStudentForEvent_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(eventRepository.findById(100L)).thenReturn(Optional.of(openEvent));
        when(registrationRepository.findByStudentIdAndEventId(1L, 100L)).thenReturn(Optional.empty());
        when(registrationRepository.save(any(Registration.class))).thenAnswer(invocation -> {
            Registration r = invocation.getArgument(0);
            r.setId(999L);
            return r;
        });

        RegistrationResponse response = registrationService.registerStudentForEvent(1L, 100L);

        assertNotNull(response);
        assertEquals("Campus Hackathon 2026", response.getEventTitle());
        assertEquals("Varshitha Chitimireddy", response.getStudentName());
        assertEquals(11, openEvent.getRegisteredCount()); // Count incremented
        verify(eventRepository, times(1)).save(openEvent);
    }

    @Test
    void registerStudentForEvent_ThrowsException_WhenDuplicateRegistration() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(eventRepository.findById(100L)).thenReturn(Optional.of(openEvent));
        when(registrationRepository.findByStudentIdAndEventId(1L, 100L)).thenReturn(Optional.of(existingRegistration));

        assertThrows(DuplicateResourceException.class,
                () -> registrationService.registerStudentForEvent(1L, 100L));
    }

    @Test
    void registerStudentForEvent_ThrowsException_WhenEventIsFull() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(eventRepository.findById(101L)).thenReturn(Optional.of(fullEvent));
        when(registrationRepository.findByStudentIdAndEventId(1L, 101L)).thenReturn(Optional.empty());

        assertThrows(EventFullException.class,
                () -> registrationService.registerStudentForEvent(1L, 101L));
    }

    @Test
    void registerStudentForEvent_ThrowsException_WhenDeadlineHasPassed() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(eventRepository.findById(102L)).thenReturn(Optional.of(pastDeadlineEvent));

        assertThrows(BadRequestException.class,
                () -> registrationService.registerStudentForEvent(1L, 102L));
    }

    @Test
    void cancelRegistration_Success_DecrementsRegisteredCount() {
        when(registrationRepository.findById(500L)).thenReturn(Optional.of(existingRegistration));

        int initialCount = openEvent.getRegisteredCount();

        registrationService.cancelRegistration(500L, 1L, false);

        assertEquals(RegistrationStatus.CANCELLED, existingRegistration.getStatus());
        assertEquals(initialCount - 1, openEvent.getRegisteredCount());
        verify(registrationRepository, times(1)).save(existingRegistration);
        verify(eventRepository, times(1)).save(openEvent);
    }
}
