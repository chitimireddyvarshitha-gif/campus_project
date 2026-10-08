package com.campusevent.management.service;

import com.campusevent.management.dto.request.CreateEventRequest;
import com.campusevent.management.dto.request.UpdateEventRequest;
import com.campusevent.management.dto.response.EventResponse;
import com.campusevent.management.entity.Event;
import com.campusevent.management.entity.EventCategory;
import com.campusevent.management.entity.EventStatus;
import com.campusevent.management.exception.BadRequestException;
import com.campusevent.management.exception.ResourceNotFoundException;
import com.campusevent.management.repository.EventRepository;
import com.campusevent.management.service.impl.EventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    private Event sampleEvent;

    @BeforeEach
    void setUp() {
        sampleEvent = Event.builder()
                .id(1L)
                .title("Campus Hackathon 2026")
                .description("24-hour coding marathon")
                .category(EventCategory.TECHNICAL)
                .eventDate(LocalDate.of(2026, 10, 14))
                .eventTime("09:00 AM")
                .venue("Alan Turing Hub")
                .organizer("Computer Science Dept")
                .capacity(200)
                .registeredCount(10)
                .registrationDeadline(LocalDate.of(2026, 10, 10))
                .status(EventStatus.UPCOMING)
                .build();
    }

    @Test
    void getAllEvents_ReturnsListOfEvents() {
        when(eventRepository.findAll()).thenReturn(Arrays.asList(sampleEvent));

        List<EventResponse> events = eventService.getAllEvents(null, null, null, null);

        assertNotNull(events);
        assertEquals(1, events.size());
        assertEquals("Campus Hackathon 2026", events.get(0).getTitle());
        assertEquals(190, events.get(0).getAvailableSeats());
    }

    @Test
    void getEventById_Success() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(sampleEvent));

        EventResponse response = eventService.getEventById(1L);

        assertNotNull(response);
        assertEquals("Campus Hackathon 2026", response.getTitle());
    }

    @Test
    void getEventById_ThrowsException_WhenNotFound() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventService.getEventById(99L));
    }

    @Test
    void createEvent_Success() {
        CreateEventRequest request = CreateEventRequest.builder()
                .title("Robotics Showcase")
                .description("Autonomous bots demonstration")
                .category(EventCategory.TECHNICAL)
                .eventDate(LocalDate.of(2026, 11, 20))
                .eventTime("10:00 AM")
                .venue("Innovation Lab")
                .organizer("Robotics Club")
                .capacity(100)
                .registrationDeadline(LocalDate.of(2026, 11, 15))
                .build();

        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> {
            Event saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });

        EventResponse response = eventService.createEvent(request);

        assertNotNull(response);
        assertEquals("Robotics Showcase", response.getTitle());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void createEvent_ThrowsException_WhenDeadlineAfterEventDate() {
        CreateEventRequest request = CreateEventRequest.builder()
                .title("Invalid Date Event")
                .description("Test")
                .category(EventCategory.TECHNICAL)
                .eventDate(LocalDate.of(2026, 10, 10))
                .eventTime("10:00 AM")
                .venue("Hall A")
                .organizer("Dept")
                .capacity(50)
                .registrationDeadline(LocalDate.of(2026, 10, 15)) // After event date!
                .build();

        assertThrows(BadRequestException.class, () -> eventService.createEvent(request));
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void updateEvent_Success() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(sampleEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(sampleEvent);

        UpdateEventRequest updateReq = UpdateEventRequest.builder()
                .title("Campus Hackathon 2026 - Extended")
                .capacity(250)
                .build();

        EventResponse response = eventService.updateEvent(1L, updateReq);

        assertNotNull(response);
        assertEquals("Campus Hackathon 2026 - Extended", sampleEvent.getTitle());
        assertEquals(250, sampleEvent.getCapacity());
        verify(eventRepository, times(1)).save(sampleEvent);
    }

    @Test
    void deleteEvent_Success() {
        when(eventRepository.existsById(1L)).thenReturn(true);

        eventService.deleteEvent(1L);

        verify(eventRepository, times(1)).deleteById(1L);
    }
}
