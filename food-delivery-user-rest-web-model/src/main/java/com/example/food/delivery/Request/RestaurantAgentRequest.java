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
public class RestaurantAgentRequest {
//    @Positive(message = "Restaurant Id should be greater than zero")
//    @NotNull(message = "Restaurant Id is required")
//    private Integer restId;

    @NotBlank(message = "Restaurant Agent name cannot be blank")
    private String restAgentName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Restaurant Agent email cannot be blank")
    private String restAgentEmail;

    @NotBlank(message = "Restaurant Agent password cannot be blank")
    private String restAgentPassword;

    @NotBlank(message = "Restaurant Agent phone cannot be blank")
    @Pattern(regexp = "\\d{10,15}", message = "Invalid phone number")
    private String restAgentPhone;


}
