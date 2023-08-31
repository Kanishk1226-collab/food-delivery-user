package com.example.food.delivery.Request;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Email Id cannot be blank")
    @Email(message = "Enter Valid Email Id")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    private String password;
}
