package com.campusevent.management.dto.response;

import com.campusevent.management.entity.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private String role; // "STUDENT" or "FACULTY"
    private String phone;
    private String department;
    private String year;
    private String designation;
    private String avatar;

    public static UserResponse fromEntity(User user) {
        if (user == null) return null;
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .phone(user.getPhone())
                .department(user.getDepartment())
                .year(user.getYear())
                .designation(user.getDesignation())
                .avatar(user.getAvatar())
                .build();
    }
}
