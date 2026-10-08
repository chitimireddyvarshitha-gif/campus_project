package com.campusevent.management.controller;

import com.campusevent.management.dto.response.EventResponse;
import com.campusevent.management.repository.UserRepository;
import com.campusevent.management.security.JwtAuthenticationEntryPoint;
import com.campusevent.management.security.JwtTokenProvider;
import com.campusevent.management.service.EventService;
import com.campusevent.management.service.RegistrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
@AutoConfigureMockMvc(addFilters = false)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @MockBean
    private RegistrationService registrationService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Test
    void getAllEvents_ReturnsEventList() throws Exception {
        EventResponse event = EventResponse.builder()
                .id(1L)
                .title("Campus Hackathon 2026")
                .description("Coding marathon")
                .category("Technical")
                .eventDate(LocalDate.of(2026, 10, 14))
                .eventTime("09:00 AM")
                .venue("Alan Turing Hub")
                .organizer("Computer Science Dept")
                .capacity(200)
                .registeredCount(178)
                .availableSeats(22)
                .status("Registration Open")
                .build();

        when(eventService.getAllEvents(any(), any(), any(), any())).thenReturn(Arrays.asList(event));

        mockMvc.perform(get("/api/events")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Campus Hackathon 2026"))
                .andExpect(jsonPath("$[0].availableSeats").value(22));
    }
}
