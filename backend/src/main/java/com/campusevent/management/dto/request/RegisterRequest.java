package com.campusevent.management.dto.request;

import com.campusevent.management.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    private String name;

    @NotBlank(message = "College email is required")
    @Email(message = "Must be a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Phone number is required")
    private String phone;

    private String department;

    private String year; // e.g. "3rd Year (Junior)"

    private String designation; // for faculty

    private Role role; // Defaults to STUDENT if null
}
