package com.example.food.delivery.Controller;

import com.example.food.delivery.DeliveryAgentServiceImpl;
import com.example.food.delivery.Request.DeliveryAgentRequest;
import com.example.food.delivery.Request.LoginRequest;
import com.example.food.delivery.Request.RequestRestAgent;
import com.example.food.delivery.Request.RestaurantAgentRequest;
import com.example.food.delivery.Response.BaseResponse;
import com.example.food.delivery.RestaurantAgentServiceImpl;
import com.example.food.delivery.ServiceInterface.DeliveryAgentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("/deliveryAgent")
public class DeliveryAgentController {
    @Autowired
    private DeliveryAgentService delAgentService;

    @PostMapping(value = "/auth/delAgent/signup")
    public ResponseEntity<BaseResponse<?>> createDelAgent(@Valid @RequestBody DeliveryAgentRequest delAgentRequest){
        return delAgentService.createDeliveryAgent(delAgentRequest);
    }

    @PostMapping("/auth/delAgent/login")
    public ResponseEntity<BaseResponse<?>> loginDelAgent(@Valid @RequestBody LoginRequest loginRequest) {
        return delAgentService.loginDelAgent(loginRequest);
    }

    @PreAuthorize("#userRole == 'DELIVERY_AGENT'")
    @PutMapping("/delAgent/logout")
    public ResponseEntity<BaseResponse<?>> logoutDelAgent(@RequestHeader("userEmail") String userEmail,
                                                          @RequestHeader("userRole") String userRole) {
        return delAgentService.logoutDelAgent(userEmail);
    }

    @PreAuthorize("#userRole == 'DELIVERY_AGENT'")
    @PutMapping("/setDelAgentAvailability")
    public ResponseEntity<BaseResponse<?>> setAvailability(@RequestParam String status,
                                                           @RequestHeader("userEmail") String userEmail,
                                                           @RequestHeader("userRole") String userRole) {
        return delAgentService.setDelAgentAvailability(userEmail, status);
    }

//    @GetMapping("/isDeliveryAgentLoggedIn")
//    public ResponseEntity<BaseResponse<?>> isDelAgentLoggedIn(@RequestParam String delAgentEmail) {
//        return delAgentService.isDelAgentLoggedIn(delAgentEmail);
//    }

    @PutMapping(value = "/request/delivery")
    @PreAuthorize("#userRole == 'DELIVERY_AGENT'")
    public ResponseEntity<BaseResponse<?>> requestRestDelivery(@Valid @RequestParam String restAgentEmail,
                                                               @RequestHeader("userEmail") String userEmail,
                                                               @RequestHeader("userRole") String userRole){
        return delAgentService.deliveryRequest(userEmail, restAgentEmail);
    }

//    @PutMapping(value = "/assignDelivery")
    @PutMapping(value = "assign/delAgent")
    public ResponseEntity<BaseResponse<?>> assignDeliveryAgent(@Valid @RequestParam String restaurantAgentEmail){
        return delAgentService.assignDeliveryAgent(restaurantAgentEmail);
    }

    @GetMapping(value = "/delAgentAvailability")
    public ResponseEntity<BaseResponse<?>> checkDeliveryAgentAvailability(@Valid @RequestParam String restaurantAgentEmail){
        return delAgentService.checkDeliveryAgentAvailability(restaurantAgentEmail);
    }

    @GetMapping(value = "delAgent/all")
    @PreAuthorize("#userRole == 'SUPER_ADMIN' or #userRole == 'CO_ADMIN' or #userRole == 'ADMIN'")
    public ResponseEntity<?> getAllDelAgents(@RequestHeader("userEmail") String userEmail,
                                             @RequestHeader("userRole") String userRole) {
        return ResponseEntity.ok(delAgentService.getAllDeliveryAgents());
    }

//    @PutMapping("/updateAdmin")
//    public ResponseEntity<BaseResponse<?>> updateRestAgent(@Valid @RequestBody UpdateAdminRequest adminRequest) {
//        return restAgentService.updateAdmin(adminRequest);
//    }

    @DeleteMapping("delAgent/delete")
    @PreAuthorize("#userRole == 'DELIVERY_AGENT'")
    public ResponseEntity<BaseResponse<?>> deleteDelAgent(@RequestHeader("userEmail") String userEmail,
                                                          @RequestHeader("userRole") String userRole) {
        return delAgentService.deleteDeliveryAgent(userEmail);
    }
}
