package com.example.food.delivery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    boolean existsByCustEmail(String custEmail);
    boolean existsByCustPhone(String custPhone);
    Customer findByCustEmail(String custEmail);
}
