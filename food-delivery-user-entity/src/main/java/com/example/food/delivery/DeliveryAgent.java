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
@Table(name = EntityConstants.DELIVERY_AGENT_TABLE_NAME)
public class DeliveryAgent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = EntityConstants.DELIVERY_AGENT_ID)
    private int delAgentId;

    @NotBlank(message = "Delivery Agent name cannot be blank")
    @Column(name = EntityConstants.DELIVERY_AGENT_NAME)
    private String delAgentName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Delivery Agent email cannot be blank")
    @Column(name = EntityConstants.DELIVERY_AGENT_EMAIL)
    private String delAgentEmail;

    @NotBlank(message = "Delivery Agent password cannot be blank")
    @Column(name = EntityConstants.DELIVERY_AGENT_PASSWORD)
    private String delAgentPassword;

    private String restAgentEmail;

    @NotBlank(message = "Delivery Agent phone cannot be blank")
    @Pattern(regexp = "\\d{10,15}", message = "Invalid phone number")
    @Column(name = EntityConstants.DELIVERY_AGENT_PHONE)
    private String delAgentPhone;

    @Column(name = EntityConstants.DELIVERY_AGENT_IS_VERIFIED)
    private Boolean isVerified;

    @Column(name = EntityConstants.DELIVERY_AGENT_IS_AVAILABLE)
    private Boolean isAvailable;

    @Column(name = EntityConstants.IS_LOGGED_IN)
    private Boolean isLoggedIn;
}
