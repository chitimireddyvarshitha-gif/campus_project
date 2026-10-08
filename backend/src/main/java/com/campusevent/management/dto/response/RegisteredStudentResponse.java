package com.campusevent.management.dto.response;

import com.campusevent.management.entity.Registration;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisteredStudentResponse {

    private Long registrationId;
    private Long studentId;
    private String studentName;
    private String email;
    private String phone;
    private LocalDate registrationDate;
    private String status;

    public static RegisteredStudentResponse fromEntity(Registration registration) {
        if (registration == null) return null;

        String stat = "Registered";
        if (registration.getStatus() != null) {
            stat = registration.getStatus().name().substring(0, 1).toUpperCase() +
                   registration.getStatus().name().substring(1).toLowerCase();
        }

        return RegisteredStudentResponse.builder()
                .registrationId(registration.getId())
                .studentId(registration.getStudent() != null ? registration.getStudent().getId() : null)
                .studentName(registration.getStudent() != null ? registration.getStudent().getName() : "")
                .email(registration.getStudent() != null ? registration.getStudent().getEmail() : "")
                .phone(registration.getStudent() != null ? registration.getStudent().getPhone() : "")
                .registrationDate(registration.getRegistrationDate())
                .status(stat)
                .build();
    }
}
