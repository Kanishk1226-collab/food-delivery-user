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

    @Column(name = EntityConstants.ADMIN_PASSWORD)
    @NotBlank(message = "Admin Password cannot be blank")
    private String adminPassword;

    @Column(name = EntityConstants.IS_LOGGED_IN)
    private Boolean isLoggedIn;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(  name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

}
