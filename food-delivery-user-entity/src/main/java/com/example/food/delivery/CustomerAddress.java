package com.example.food.delivery;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = EntityConstants.ADDRESS_TABLE_NAME)
public class CustomerAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = EntityConstants.ADDRESS_ID)
    private int addressId;

    @NotNull(message = "Customer Email cannot be null")
    @Email(message = "Enter valid Email")
    @Column(name = EntityConstants.CUSTOMER_EMAIL)
    private String custEmail;

    @NotNull(message = "Door No. cannot be null")
    @Column(name = EntityConstants.DOOR_NO)
    private Integer doorNo;

    @NotBlank(message = "Locality cannot be blank")
    @Column(name = EntityConstants.LOCALITY)
    private String locality;

    @NotNull(message = "City cannot be null")
    @Column(name = EntityConstants.CITY)
    private String city;

    @NotNull(message = "Pincode cannot be null")
    @Pattern(regexp = "\\d{6}", message = "Invalid Pin Code")
    @Column(name = EntityConstants.PINCODE)
    private String pincode;
}
