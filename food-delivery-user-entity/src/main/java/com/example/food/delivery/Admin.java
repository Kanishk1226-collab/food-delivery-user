package com.example.food.delivery;

import com.example.food.delivery.Request.AdminRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = EntityConstants.ADMIN_TABLE_NAME)
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = EntityConstants.ADMIN_ID)
    private int adminId;

    @NotBlank(message = "Admin name cannot be blank")
    @Column(name = EntityConstants.ADMIN_NAME)
    private String adminName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Admin email cannot be blank")
    @Column(name = EntityConstants.ADMIN_EMAIL)
    private String adminEmail;

    @NotNull(message = "Admin role cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = EntityConstants.ADMIN_ROLE)
    private AdminRole adminRole;

    @NotBlank(message = "Admin phone number cannot be blank")
    @Pattern(regexp = "\\d{10}", message = "Phone number should contain only number and it should be 10 digits")
    @Column(name = EntityConstants.ADMIN_PHONE_NO)
    private String phoneNo;

    @Column(name = EntityConstants.ADMIN_PASSWORD)
    @NotBlank(message = "Admin Password cannot be blank")
    private String adminPassword;
}
