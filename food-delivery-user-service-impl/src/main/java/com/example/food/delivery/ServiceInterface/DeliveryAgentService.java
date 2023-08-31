package com.example.food.delivery.ServiceInterface;

import com.example.food.delivery.Request.DeliveryAgentRequest;
import com.example.food.delivery.Request.LoginRequest;
import com.example.food.delivery.Request.RequestRestAgent;
import com.example.food.delivery.Response.BaseResponse;
import org.springframework.http.ResponseEntity;

public interface DeliveryAgentService {
    ResponseEntity<BaseResponse<?>> createDeliveryAgent(DeliveryAgentRequest delAgentRequest);
    ResponseEntity<BaseResponse<?>> loginDelAgent(LoginRequest loginRequest);
    ResponseEntity<BaseResponse<?>> logoutDelAgent(String delAgentEmail);
    ResponseEntity<BaseResponse<?>> setDelAgentAvailability(String delAgentEmail);
    ResponseEntity<BaseResponse<?>> isDelAgentLoggedIn(String delAgentEmail);
    ResponseEntity<?> getAllDeliveryAgents();
    ResponseEntity<BaseResponse<?>> deleteDeliveryAgent(int delAgentId);
    ResponseEntity<BaseResponse<?>> deliveryRequest(RequestRestAgent requestRestAgent);
    ResponseEntity<BaseResponse<?>> assignDeliveryAgent(String restAgentEmail);
    ResponseEntity<BaseResponse<?>> checkDeliveryAgentAvailability(String restAgentEmail);
}
