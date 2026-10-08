package com.campusevent.management.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDashboardResponse {

    private long upcomingEventsCount;
    private long myRegistrationsCount;
    private long completedEventsCount;
    private long availableEventsCount;

    private List<RegistrationResponse> upcomingRegisteredEvents;
    private List<EventResponse> recommendedEvents;
}
