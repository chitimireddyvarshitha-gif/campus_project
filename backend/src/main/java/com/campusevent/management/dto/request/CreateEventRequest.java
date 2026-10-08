package com.campusevent.management.dto.request;

import com.campusevent.management.entity.EventCategory;
import com.campusevent.management.entity.EventStatus;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateEventRequest {

    @NotBlank(message = "Event title is required")
    private String title;

    @NotBlank(message = "Event description is required")
    private String description;

    @NotNull(message = "Event category is required")
    private EventCategory category;

    @NotNull(message = "Event date is required")
    @JsonAlias({"date", "eventDate"})
    private LocalDate eventDate;

    @NotBlank(message = "Event time is required")
    @JsonAlias({"time", "eventTime"})
    private String eventTime;

    @NotBlank(message = "Event venue is required")
    private String venue;

    @NotBlank(message = "Organizing body is required")
    private String organizer;

    @NotNull(message = "Seat capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    @NotNull(message = "Registration deadline is required")
    @JsonAlias({"deadline", "registrationDeadline"})
    private LocalDate registrationDeadline;

    private String imageUrl;

    private EventStatus status; // defaults to UPCOMING / Registration Open
}
