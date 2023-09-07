package com.example.food.delivery;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = EntityConstants.CUSTOMER_TABLE_NAME)
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = EntityConstants.CUSTOMER_ID)
    private int custId;

    @NotBlank(message = "Customer name cannot be blank")
    @Column(name = EntityConstants.CUSTOMER_NAME)
    private String custName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Customer email cannot be blank")
    @Column(name = EntityConstants.CUSTOMER_EMAIL)
    private String custEmail;

    @NotBlank(message = "Customer password cannot be blank")
    @Column(name = EntityConstants.CUSTOMER_PASSWORD)
    private String custPassword;

    @NotBlank(message = "Customer phone cannot be blank")
    @Pattern(regexp = "\\d{10}", message = "Phone number should contain only number and it should be 10 digits")
    @Column(name = EntityConstants.CUSTOMER_PHONE)
    private String custPhone;

}
