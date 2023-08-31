package com.example.food.delivery.Request;

import com.example.food.delivery.Validator.EnumNamePattern;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
}
