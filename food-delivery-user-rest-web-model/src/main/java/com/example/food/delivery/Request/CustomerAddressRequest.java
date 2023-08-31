package com.example.food.delivery.Request;

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
public class CustomerAddressRequest {

    @NotNull(message = "Door No. cannot be null")
    private Integer doorNo;

    @NotBlank(message = "Locality cannot be blank")
    private String locality;

    @NotNull(message = "City cannot be null")
    private String city;

    @NotNull(message = "Pincode cannot be null")
    @Pattern(regexp = "\\d{6}", message = "Invalid Pin Code")
    private String pincode;
}
