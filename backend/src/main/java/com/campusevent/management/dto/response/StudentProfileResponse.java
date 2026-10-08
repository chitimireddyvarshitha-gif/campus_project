package com.campusevent.management.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfileResponse {

    private UserResponse user;
    private long activeRegistrationsCount;
    private long completedRegistrationsCount;
    private long totalRegistrationsCount;
}
