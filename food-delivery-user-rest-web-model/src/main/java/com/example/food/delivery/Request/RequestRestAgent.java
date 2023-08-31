package com.example.food.delivery.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestRestAgent {
    @Email(message = "Invalid Delivery agent email format")
    @NotBlank(message = "Delivery Agent email cannot be blank")
    private String delAgentEmail;

    @Email(message = "Invalid Restaurant agent email format")
    @NotBlank(message = "Restaurant Agent email cannot be blank")
    private String restAgentEmail;
}