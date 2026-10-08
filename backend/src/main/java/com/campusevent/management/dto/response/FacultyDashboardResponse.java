package com.campusevent.management.dto.response;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacultyDashboardResponse {

    private long totalEvents;
    private long upcomingEvents;
    private long totalRegistrations;
    private long completedEvents;

    private List<CategorySummaryDto> categorySummary;
    private List<EventResponse> recentEvents;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategorySummaryDto {
        private String category;
        private int count;
        private int registrations;
        private int capacity;
        private int fillRate;
    }
}
