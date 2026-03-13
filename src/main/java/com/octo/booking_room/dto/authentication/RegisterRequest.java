package com.octo.booking_room.dto.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterRequest {
    @NotBlank(message = "name is required")
    @Size(min = 3, max = 255, message = "name must be between 3 and 255 characters long")
    private String name;

    @NotBlank(message = "phone is required")
    @Pattern(
        regexp = "^[0-9]{10,15}$",
        message = "phone number must be between 10 and 15 digits long and contain only number"
    )
    private String phone;

    @Email(message = "invalid email format")
    @NotBlank(message = "email is required")
    private String email;

    @NotBlank(message = "password is required")
    @Size(min = 8, message = "password must be at least 8 characters long")
    private String password;
}
