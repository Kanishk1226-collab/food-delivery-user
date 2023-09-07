package com.example.food.delivery.ServiceInterface;

import com.example.food.delivery.Request.LoginRequest;
import com.example.food.delivery.Request.RequestRestAgent;
import com.example.food.delivery.Request.RestaurantAgentRequest;
import com.example.food.delivery.Response.BaseResponse;
import org.springframework.http.ResponseEntity;

public interface RestaurantAgentService {
    ResponseEntity<BaseResponse<?>> createRestAgent(RestaurantAgentRequest restAgentRequest);
    ResponseEntity<BaseResponse<?>> loginRestAgent(LoginRequest loginRequest);
    ResponseEntity<BaseResponse<?>> logoutRestAgent(String restAgentEmail);
    ResponseEntity<BaseResponse<?>> getAllRestAgents(int page);
    ResponseEntity<BaseResponse<?>> deleteRestAgent(String restAgentEmail);
    ResponseEntity<?> approveDeliveryAgent(String delAgentEmail, String restAgentEmail);

}
