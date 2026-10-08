package com.campusevent.management.dto.response;

import com.campusevent.management.entity.Event;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponse {

    private Long id;
    private String title;
    private String description;
    private String category;

    // Both eventDate and date for complete frontend compatibility
    private LocalDate eventDate;
    
    @JsonProperty("date")
    public String getDate() {
        return eventDate != null ? eventDate.toString() : null;
    }

    private String eventTime;

    @JsonProperty("time")
    public String getTime() {
        return eventTime;
    }

    private String venue;
    private String organizer;
    private Integer capacity;
    private Integer registeredCount;
    private Integer availableSeats;

    // Both registrationDeadline and deadline for complete frontend compatibility
    private LocalDate registrationDeadline;

    @JsonProperty("deadline")
    public String getDeadline() {
        return registrationDeadline != null ? registrationDeadline.toString() : null;
    }

    private String imageUrl;
    private String status;

    public static EventResponse fromEntity(Event event) {
        if (event == null) return null;

        int regCount = event.getRegisteredCount() != null ? event.getRegisteredCount() : 0;
        int cap = event.getCapacity() != null ? event.getCapacity() : 0;
        int avail = Math.max(0, cap - regCount);

        // Format category and status nicely (e.g. "Technical" or "TECHNICAL")
        String catName = event.getCategory() != null ? event.getCategory().name() : "OTHER";
        // Convert to title case for category to match frontend "Technical", "Cultural", etc.
        String formattedCategory = catName.substring(0, 1).toUpperCase() + catName.substring(1).toLowerCase();

        // Convert status to readable or matching frontend status
        String statusStr = "Registration Open";
        if (event.getStatus() != null) {
            switch (event.getStatus()) {
                case UPCOMING -> statusStr = (avail <= 0) ? "Event Full" : "Registration Open";
                case ONGOING -> statusStr = "Upcoming";
                case COMPLETED -> statusStr = "Completed";
                case CANCELLED -> statusStr = "Cancelled";
            }
        }

        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .category(formattedCategory)
                .eventDate(event.getEventDate())
                .eventTime(event.getEventTime())
                .venue(event.getVenue())
                .organizer(event.getOrganizer())
                .capacity(cap)
                .registeredCount(regCount)
                .availableSeats(avail)
                .registrationDeadline(event.getRegistrationDeadline())
                .imageUrl(event.getImageUrl())
                .status(statusStr)
                .build();
    }
}
