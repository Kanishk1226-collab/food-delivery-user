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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/deliveryAgent")
public class DeliveryAgentController {
    @Autowired
    private DeliveryAgentService delAgentService;

    @PostMapping(value = "/createDelAgent")
    public ResponseEntity<BaseResponse<?>> createDelAgent(@Valid @RequestBody DeliveryAgentRequest delAgentRequest){
        return delAgentService.createDeliveryAgent(delAgentRequest);
    }

    @PutMapping("/delAgentLogin")
    public ResponseEntity<BaseResponse<?>> loginDelAgent(@Valid @RequestBody LoginRequest loginRequest) {
        return delAgentService.loginDelAgent(loginRequest);
    }

    @PutMapping("/delAgentLogout")
    public ResponseEntity<BaseResponse<?>> logoutDelAgent(@RequestParam String delAgentEmail) {
        return delAgentService.logoutDelAgent(delAgentEmail);
    }

    @PutMapping("/setDelAgentAvailability")
    public ResponseEntity<BaseResponse<?>> setAvailability(@RequestParam String delAgentEmail) {
        return delAgentService.setDelAgentAvailability(delAgentEmail);
    }

    @GetMapping("/isDeliveryAgentLoggedIn")
    public ResponseEntity<BaseResponse<?>> isDelAgentLoggedIn(@RequestParam String delAgentEmail) {
        return delAgentService.isDelAgentLoggedIn(delAgentEmail);
    }

    @PutMapping(value = "/requestRestaurantDelivery")
    public ResponseEntity<BaseResponse<?>> requestRestDelivery(@Valid @RequestBody RequestRestAgent requestRestAgent){
        return delAgentService.deliveryRequest(requestRestAgent);
    }

    @PutMapping(value = "/assignDelivery")
    public ResponseEntity<BaseResponse<?>> assignDeliveryAgent(@Valid @RequestParam String restaurantAgentEmail){
        return delAgentService.assignDeliveryAgent(restaurantAgentEmail);
    }

    @GetMapping(value = "/delAgentAvailability")
    public ResponseEntity<BaseResponse<?>> checkDeliveryAgentAvailability(@Valid @RequestParam String restaurantAgentEmail){
        return delAgentService.checkDeliveryAgentAvailability(restaurantAgentEmail);
    }

    @GetMapping(value = "/getDelAgents")
    public ResponseEntity<?> getAllDelAgents() {
        return ResponseEntity.ok(delAgentService.getAllDeliveryAgents());
    }

//    @PutMapping("/updateAdmin")
//    public ResponseEntity<BaseResponse<?>> updateRestAgent(@Valid @RequestBody UpdateAdminRequest adminRequest) {
//        return restAgentService.updateAdmin(adminRequest);
//    }

    @DeleteMapping("/{delAgentId}")
    public ResponseEntity<BaseResponse<?>> deleteDelAgent(@PathVariable int delAgentId) {
        return delAgentService.deleteDeliveryAgent(delAgentId);
    }
}
