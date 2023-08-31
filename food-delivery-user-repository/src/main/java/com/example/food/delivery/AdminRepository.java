package com.example.food.delivery;

import com.example.food.delivery.Request.AdminRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {
    boolean existsByAdminEmail(String adminEmail);
    long countByAdminRole(AdminRole adminRole);

    Admin findByAdminName(String adminName);

    Admin findByAdminEmail(String adminEmail);

    void deleteByAdminEmail(String currentAdminEmail);
}
