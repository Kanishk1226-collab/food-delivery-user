package com.example.food.delivery.Controller;

import com.example.food.delivery.CustomerAddressServiceImpl;
import com.example.food.delivery.CustomerServiceImpl;
import com.example.food.delivery.Request.CustomerAddressRequest;
import com.example.food.delivery.Request.CustomerRequest;
import com.example.food.delivery.Response.BaseResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/address")
public class CustomerAddressController {
    @Autowired
    private CustomerAddressServiceImpl custAddressService;

    @PostMapping(value = "/create")
    @PreAuthorize("#userRole == 'CUSTOMER'")
    public ResponseEntity<BaseResponse<?>> createCustAddress(@Valid @RequestBody CustomerAddressRequest custAddressRequest,
                                                             @RequestHeader("userEmail") String userEmail,
                                                             @RequestHeader("userRole") String userRole){
        return custAddressService.addAddress(custAddressRequest, userEmail);
    }

    @GetMapping(value = "/all")
    @PreAuthorize("#userRole == 'SUPER_ADMIN' or #userRole == 'CO_ADMIN' or #userRole == 'ADMIN'")
    public ResponseEntity<?> getAllAddress(@RequestHeader("userRole") String userRole) {
        return ResponseEntity.ok(custAddressService.getAllCustomerAddress());
    }

    @GetMapping(value = "/get")
    @PreAuthorize("#userRole == 'CUSTOMER'")
    public ResponseEntity<?> getAllCustAddress(@RequestHeader("userEmail") String userEmail,
                                               @RequestHeader("userRole") String userRole) {
        return ResponseEntity.ok(custAddressService.getCustomerAddress(userEmail));
    }

    @GetMapping(value = "/detail")
    public ResponseEntity<?> getAddressDetail(int addressId,
                                              @RequestHeader("userEmail") String userEmail,
                                              @RequestHeader("userRole") String userRole) {
        return custAddressService.getAddressDetail(userEmail, addressId);
    }

    @DeleteMapping("/delete/{custAddressId}")
    @PreAuthorize("#userRole == 'CUSTOMER'")
    public ResponseEntity<BaseResponse<?>> deleteCustAddress(@PathVariable int custAddressId,
                                                             @RequestHeader("userEmail") String userEmail,
                                                             @RequestHeader("userRole") String userRole) {
        return custAddressService.deleteCustomerAddress(custAddressId, userRole);
    }
}
