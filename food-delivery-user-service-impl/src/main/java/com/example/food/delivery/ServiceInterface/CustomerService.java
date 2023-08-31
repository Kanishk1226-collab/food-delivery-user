package com.example.food.delivery.ServiceInterface;

import com.example.food.delivery.Request.CustomerRequest;
import com.example.food.delivery.Request.LoginRequest;
import com.example.food.delivery.Response.BaseResponse;
import org.springframework.http.ResponseEntity;

public interface CustomerService {
    ResponseEntity<BaseResponse<?>> createCustomer(CustomerRequest customerRequest);
    ResponseEntity<BaseResponse<?>> loginCustomer(LoginRequest loginRequest);
    ResponseEntity<BaseResponse<?>> logoutCustomer(String customerEmail);
    ResponseEntity<BaseResponse<?>> isCustomerLoggedIn(String customerEmail);
    ResponseEntity<BaseResponse<?>> getAllCustomer(String email, int page);
    ResponseEntity<BaseResponse<?>> deleteCustomer(int customerId);
    ResponseEntity<BaseResponse<?>> isValidCustomerEmail(String custEmail);

}
