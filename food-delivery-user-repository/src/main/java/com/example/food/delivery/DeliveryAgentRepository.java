package com.example.food.delivery;

import com.example.food.delivery.Request.DelAgentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryAgentRepository  extends JpaRepository<DeliveryAgent, Integer> {
    boolean existsByDelAgentEmail(String delAgentEmail);
    boolean existsByDelAgentPhone(String delAgentPhone);
    DeliveryAgent findByDelAgentEmail(String delAgentEmail);
    List<DeliveryAgent> findByRestAgentEmailAndStatus(String restAgentEmail, DelAgentStatus status);
    List<DeliveryAgent> findByRestAgentEmailAndStatusOrderByDeliveryCountAsc(String restAgentEmail, DelAgentStatus status);
}
