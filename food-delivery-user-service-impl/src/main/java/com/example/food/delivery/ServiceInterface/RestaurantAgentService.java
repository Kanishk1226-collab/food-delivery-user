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
    ResponseEntity<BaseResponse<?>> isRestAgentLoggedIn(String restAgentEmail);
    ResponseEntity<BaseResponse<?>> getAllRestAgents(String email, int page);
    ResponseEntity<?> isValidRestAgent(String restAgentEmail);
    ResponseEntity<BaseResponse<?>> deleteRestAgent(int restAgentId);
    ResponseEntity<?> approveDeliveryAgent(RequestRestAgent requestRestAgent);

}
