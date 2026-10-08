package com.campusevent.management.dto.response;

import com.campusevent.management.entity.Registration;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationResponse {

    private Long id;
    private Long eventId;
    private String eventTitle;
    private String eventDate;
    private String eventTime;
    private String eventVenue;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private String studentPhone;
    private LocalDate registrationDate;
    private String status;

    public static RegistrationResponse fromEntity(Registration registration) {
        if (registration == null) return null;

        String stat = "Registered";
        if (registration.getStatus() != null) {
            stat = registration.getStatus().name().substring(0, 1).toUpperCase() +
                   registration.getStatus().name().substring(1).toLowerCase();
        }

        return RegistrationResponse.builder()
                .id(registration.getId())
                .eventId(registration.getEvent() != null ? registration.getEvent().getId() : null)
                .eventTitle(registration.getEvent() != null ? registration.getEvent().getTitle() : null)
                .eventDate(registration.getEvent() != null && registration.getEvent().getEventDate() != null
                        ? registration.getEvent().getEventDate().toString() : null)
                .eventTime(registration.getEvent() != null ? registration.getEvent().getEventTime() : null)
                .eventVenue(registration.getEvent() != null ? registration.getEvent().getVenue() : null)
                .studentId(registration.getStudent() != null ? registration.getStudent().getId() : null)
                .studentName(registration.getStudent() != null ? registration.getStudent().getName() : null)
                .studentEmail(registration.getStudent() != null ? registration.getStudent().getEmail() : null)
                .studentPhone(registration.getStudent() != null ? registration.getStudent().getPhone() : null)
                .registrationDate(registration.getRegistrationDate())
                .status(stat)
                .build();
    }
}
