package com.example.food.delivery.Controller;

import com.example.food.delivery.CustomerServiceImpl;
import com.example.food.delivery.DeliveryAgentServiceImpl;
import com.example.food.delivery.Request.CustomerRequest;
import com.example.food.delivery.Request.DeliveryAgentRequest;
import com.example.food.delivery.Request.LoginRequest;
import com.example.food.delivery.Response.BaseResponse;
import com.example.food.delivery.ServiceInterface.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("/customer")
public class CustomerController {
    @Autowired
    private CustomerService custService;

    @PostMapping(value = "/auth/customer/signup")
    public ResponseEntity<BaseResponse<?>> createCustomer(@Valid @RequestBody CustomerRequest customerRequest){
        return custService.createCustomer(customerRequest);
    }

    @PostMapping("/auth/customer/login")
    public ResponseEntity<BaseResponse<?>> loginCustomer(@Valid @RequestBody LoginRequest loginRequest) {
        return custService.loginCustomer(loginRequest);
    }

    @PutMapping("/customer/logout")
    @PreAuthorize("#userRole == 'CUSTOMER'")
    public ResponseEntity<BaseResponse<?>> logoutCustomer(@RequestHeader("userEmail") String userEmail,
                                                          @RequestHeader("userRole") String userRole) {
        return custService.logoutCustomer(userEmail);
    }

    @PreAuthorize("#userRole == 'SUPER_ADMIN' or #userRole == 'CO_ADMIN' or #userRole == 'ADMIN'")
    @GetMapping(value = "/api/auth/getCustomers")
    public ResponseEntity<?> getAllCustomers(int page,
                                             @RequestHeader("userEmail") String userEmail,
                                             @RequestHeader("userRole") String userRole) {
        return custService.getAllCustomer(userEmail, page);
    }


    @DeleteMapping("/delete/customer")
    @PreAuthorize("#userRole == 'CUSTOMER'")
    public ResponseEntity<BaseResponse<?>> deleteCustomer(@RequestHeader("userEmail") String userEmail,
                                                          @RequestHeader("userRole") String userRole) {
        return custService.deleteCustomer(userEmail);
    }

}
