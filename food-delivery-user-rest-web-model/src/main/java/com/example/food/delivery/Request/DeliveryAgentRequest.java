package com.example.food.delivery.Request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAgentRequest {
    @NotBlank(message = "Delivery Agent name cannot be blank")
    private String delAgentName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Delivery Agent email cannot be blank")
    private String delAgentEmail;

    @NotBlank(message = "Delivery Agent password cannot be blank")
    private String delAgentPassword;

    @NotBlank(message = "Delivery Agent phone cannot be blank")
    @Pattern(regexp = "\\d{10,15}", message = "Invalid phone number")
    private String delAgentPhone;
}
