package com.example.food.delivery.Request;

import com.example.food.delivery.Validator.EnumNamePattern;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminRequest {
    @NotBlank(message = "Admin name cannot be blank")
    private String adminName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Admin email cannot be blank")
    private String adminEmail;

    @EnumNamePattern(
            regexp = "ADMIN|CO_ADMIN",
            message = "Role Should be either ADMIN or CO_ADMIN"
    )
    @NotNull(message = "Admin role cannot be null")
    private AdminRole adminRole;

    @NotBlank(message = "Admin phone number cannot be blank")
    @Pattern(regexp = "\\d{10}", message = "Phone number should contain only number and it should be 10 digits")
    private String phoneNo;
}
