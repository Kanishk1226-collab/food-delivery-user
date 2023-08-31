package com.example.food.delivery.Request;

import com.example.food.delivery.Validator.EnumNamePattern;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAdminRequest {
    @Positive(message = "Admin ID should be greater than zero")
    @NotNull(message = "Admin Field should not be null")
    private Integer adminId;

    private String adminName;

    private String adminEmail;

}
