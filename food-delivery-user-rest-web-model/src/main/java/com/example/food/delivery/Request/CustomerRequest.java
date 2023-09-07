package com.example.food.delivery.Request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {
    @NotBlank(message = "Customer name cannot be blank")
    private String custName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Customer email cannot be blank")
    private String custEmail;

    @NotBlank(message = "Customer password cannot be blank")
    private String custPassword;

    @NotBlank(message = "Customer phone cannot be blank")
    @Pattern(regexp = "\\d{10}", message = "Phone number should contain only number and it should be 10 digits")
    private String custPhone;
}
