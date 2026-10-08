package com.campusevent.management.dto.request;

import com.campusevent.management.entity.EventCategory;
import com.campusevent.management.entity.EventStatus;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateEventRequest {

    private String title;

    private String description;

    private EventCategory category;

    @JsonAlias({"date", "eventDate"})
    private LocalDate eventDate;

    @JsonAlias({"time", "eventTime"})
    private String eventTime;

    private String venue;

    private String organizer;

    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    @JsonAlias({"deadline", "registrationDeadline"})
    private LocalDate registrationDeadline;

    private String imageUrl;

    private EventStatus status;
}
