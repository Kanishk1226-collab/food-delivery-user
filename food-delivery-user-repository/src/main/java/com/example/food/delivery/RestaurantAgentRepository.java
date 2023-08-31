package com.example.food.delivery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantAgentRepository extends JpaRepository<RestaurantAgent, Integer> {
    boolean existsByRestAgentEmail(String restAgentEmail);
    RestaurantAgent findByRestAgentEmail(String restAgentEmail);
}
