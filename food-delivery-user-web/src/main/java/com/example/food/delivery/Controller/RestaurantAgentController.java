package com.example.food.delivery.Controller;

import com.example.food.delivery.AdminServiceImpl;
import com.example.food.delivery.Request.*;
import com.example.food.delivery.Response.BaseResponse;
import com.example.food.delivery.RestaurantAgentServiceImpl;
import com.example.food.delivery.ServiceInterface.RestaurantAgentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("/restaurantAgent")
public class RestaurantAgentController {
    @Autowired
    private RestaurantAgentService restAgentService;

    @PostMapping(value = "/auth/restAgent/signup")
    public ResponseEntity<BaseResponse<?>> createRestAgent(@Valid @RequestBody RestaurantAgentRequest restAgentRequest){
        return restAgentService.createRestAgent(restAgentRequest);
    }

    @PostMapping("/auth/restAgent/login")
    public ResponseEntity<BaseResponse<?>> loginRestAgent(@Valid @RequestBody LoginRequest loginRequest) {
        return restAgentService.loginRestAgent(loginRequest);
    }

    @PutMapping("/restAgent/logout")
    @PreAuthorize("#userRole == 'RESTAURANT_AGENT'")
    public ResponseEntity<BaseResponse<?>> logoutRestAgent(@RequestParam String restAgentEmail,
                                                           @RequestHeader("userEmail") String userEmail,
                                                           @RequestHeader("userRole") String userRole) {
        return restAgentService.logoutRestAgent(restAgentEmail);
    }

    @GetMapping(value = "/getRestAgents")
    @PreAuthorize("#userRole == 'SUPER_ADMIN' or #userRole == 'CO_ADMIN' or #userRole == 'ADMIN'")
    public ResponseEntity<?> getAllRestAgents(@RequestParam int page,
                                              @RequestHeader("userEmail") String userEmail,
                                              @RequestHeader("userRole") String userRole) {
        return restAgentService.getAllRestAgents(page);
    }

    @PutMapping(value = "/delAgent/approve")
    @PreAuthorize("#userRole == 'RESTAURANT_AGENT'")
    public ResponseEntity<?> approveDeliveryAgent(@RequestParam String delAgentEmail,
                                                  @RequestHeader("userEmail") String userEmail,
                                                  @RequestHeader("userRole") String userRole) {
        return restAgentService.approveDeliveryAgent(delAgentEmail, userEmail);
    }

    @DeleteMapping("/delete/restAgent")
    @PreAuthorize("#userRole == 'RESTAURANT_AGENT'")
    public ResponseEntity<BaseResponse<?>> deleteRestAgent(@RequestHeader("userEmail") String userEmail,
                                                           @RequestHeader("userRole") String userRole) {
        return restAgentService.deleteRestAgent(userEmail);
    }
}
