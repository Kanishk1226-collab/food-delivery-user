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
import org.springframework.web.bind.annotation.*;

@RestController
public class CustomerController {
    @Autowired
    private CustomerService custService;

    @PostMapping(value = "/createCustomer")
    public ResponseEntity<BaseResponse<?>> createCustomer(@Valid @RequestBody CustomerRequest customerRequest){
        return custService.createCustomer(customerRequest);
    }

    @PutMapping("/customerLogin")
    public ResponseEntity<BaseResponse<?>> loginCustomer(@Valid @RequestBody LoginRequest loginRequest) {
        return custService.loginCustomer(loginRequest);
    }

    @PutMapping("/customerLogout")
    public ResponseEntity<BaseResponse<?>> logoutCustomer(@RequestParam String customerEmail) {
        return custService.logoutCustomer(customerEmail);
    }

    @GetMapping("/isCustomerLoggedIn")
    public ResponseEntity<BaseResponse<?>> isCustomerLoggedIn(@RequestParam String customerEmail) {
        return custService.isCustomerLoggedIn(customerEmail);
    }

    @GetMapping(value = "/api/auth/getCustomers")
    public ResponseEntity<?> getAllCustomers(@RequestParam String restAgentEmail, int page) {
        return custService.getAllCustomer(restAgentEmail, page);
    }

//    @PutMapping("/updateAdmin")
//    public ResponseEntity<BaseResponse<?>> updateRestAgent(@Valid @RequestBody UpdateAdminRequest adminRequest) {
//        return restAgentService.updateAdmin(adminRequest);
//    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<BaseResponse<?>> deleteCustomer(@PathVariable int customerId) {
        return custService.deleteCustomer(customerId);
    }

    @GetMapping(value = "/isValidCustomer")
    public ResponseEntity<?> getIsValidCustomer(@RequestParam String custEmail) {
        return custService.isValidCustomerEmail(custEmail);
    }
}
