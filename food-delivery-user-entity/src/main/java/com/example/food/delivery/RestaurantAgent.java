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
@Table(name = EntityConstants.REST_AGENT_TABLE_NAME)
public class RestaurantAgent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = EntityConstants.REST_AGENT_ID)
    private int restAgentId;

    @NotBlank(message = "Restaurant Agent name cannot be blank")
    @Column(name = EntityConstants.REST_AGENT_NAME)
    private String restAgentName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Restaurant Agent email cannot be blank")
    @Column(name = EntityConstants.REST_AGENT_EMAIL)
    private String restAgentEmail;

    @NotBlank(message = "Restaurant Agent password cannot be blank")
    @Column(name = EntityConstants.REST_AGENT_PASSWORD)
    private String restAgentPassword;

    @NotBlank(message = "Restaurant Agent phone cannot be blank")
    @Pattern(regexp = "\\d{10}", message = "Phone number should contain only number and it should be 10 digits")
    @Column(name = EntityConstants.REST_AGENT_PHONE)
    private String restAgentPhone;
}
