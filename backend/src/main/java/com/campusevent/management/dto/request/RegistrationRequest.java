package com.campusevent.management.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationRequest {

    @NotNull(message = "Event ID is required")
    private Long eventId;

    private Long studentId;
}
