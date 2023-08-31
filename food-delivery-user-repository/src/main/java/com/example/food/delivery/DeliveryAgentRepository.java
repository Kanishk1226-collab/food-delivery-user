package com.example.food.delivery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryAgentRepository  extends JpaRepository<DeliveryAgent, Integer> {
    boolean existsByDelAgentEmail(String delAgentEmail);
    DeliveryAgent findByDelAgentEmail(String delAgentEmail);
    List<DeliveryAgent> findByRestAgentEmailAndIsAvailable(String restAgentEmail, Boolean isAvailable);
}
