package com.example.food.delivery.Controller;

import com.example.food.delivery.AdminServiceImpl;
import com.example.food.delivery.Request.*;
import com.example.food.delivery.Response.BaseResponse;
import com.example.food.delivery.RestaurantAgentServiceImpl;
import com.example.food.delivery.ServiceInterface.RestaurantAgentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/restaurantAgent")
public class RestaurantAgentController {
    @Autowired
    private RestaurantAgentService restAgentService;

    @PostMapping(value = "/createRestAgent")
    public ResponseEntity<BaseResponse<?>> createRestAgent(@Valid @RequestBody RestaurantAgentRequest restAgentRequest){
        return restAgentService.createRestAgent(restAgentRequest);
    }

    @PutMapping("/restAgentLogin")
    public ResponseEntity<BaseResponse<?>> loginRestAgent(@Valid @RequestBody LoginRequest loginRequest) {
        return restAgentService.loginRestAgent(loginRequest);
    }

    @PutMapping("/restAgentLogout")
    public ResponseEntity<BaseResponse<?>> logoutRestAgent(@RequestParam String restAgentEmail) {
        return restAgentService.logoutRestAgent(restAgentEmail);
    }

    @GetMapping("/isRestAgentLoggedIn")
    public ResponseEntity<BaseResponse<?>> isRestAgentLoggedIn(@RequestParam String restAgentEmail) {
        return restAgentService.isRestAgentLoggedIn(restAgentEmail);
    }

    @GetMapping(value = "/getRestAgents")
    public ResponseEntity<?> getAllRestAgents(@RequestParam String restAgentEmail, int page) {
        return restAgentService.getAllRestAgents(restAgentEmail, page);
    }

    @GetMapping(value = "/isValidRestAgent")
    public ResponseEntity<?> isValidRestAgent(@RequestParam String restAgentEmail) {
        return restAgentService.isValidRestAgent(restAgentEmail);
    }

    @PutMapping(value = "/approveDelAgent")
    public ResponseEntity<?> approveDeliveryAgent(@RequestBody RequestRestAgent requestRestAgent) {
        return restAgentService.approveDeliveryAgent(requestRestAgent);
    }

//    @PutMapping("/updateAdmin")
//    public ResponseEntity<BaseResponse<?>> updateRestAgent(@Valid @RequestBody UpdateAdminRequest adminRequest) {
//        return restAgentService.updateAdmin(adminRequest);
//    }

    @DeleteMapping("/{restAgentId}")
    public ResponseEntity<BaseResponse<?>> deleteRestAgent(@PathVariable int restAgentId) {
        return restAgentService.deleteRestAgent(restAgentId);
    }
}
