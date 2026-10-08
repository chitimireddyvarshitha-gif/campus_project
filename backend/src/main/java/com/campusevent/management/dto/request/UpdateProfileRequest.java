package com.campusevent.management.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileRequest {

    private String name;
    private String phone;
    private String department;
    private String year;
    private String designation;
    private String avatar;
}
