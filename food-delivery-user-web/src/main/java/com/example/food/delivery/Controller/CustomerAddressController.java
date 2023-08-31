package com.example.food.delivery.Controller;

import com.example.food.delivery.CustomerAddressServiceImpl;
import com.example.food.delivery.CustomerServiceImpl;
import com.example.food.delivery.Request.CustomerAddressRequest;
import com.example.food.delivery.Request.CustomerRequest;
import com.example.food.delivery.Response.BaseResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customerAddress")
public class CustomerAddressController {
    @Autowired
    private CustomerAddressServiceImpl custAddressService;

    @PostMapping(value = "/createCustomerAddress")
    public ResponseEntity<BaseResponse<?>> createCustAddress(@Valid @RequestBody CustomerAddressRequest custAddressRequest, @RequestParam String custEmail){
        return custAddressService.addAddress(custAddressRequest, custEmail);
    }

//    @GetMapping(value = "/getAllCustomerAddress")
//    public ResponseEntity<?> getAllCustAddress() {
//        return ResponseEntity.ok(custAddressService.getAllCustomerAddress());
//    }

    @GetMapping(value = "/getCustomerAddress")
    public ResponseEntity<?> getAllCustAddress(@RequestParam String customerEmail) {
        return ResponseEntity.ok(custAddressService.getCustomerAddress(customerEmail));
    }

    @GetMapping(value = "/getAddressDetail")
    public ResponseEntity<?> getAddressDetail(@RequestParam String customerEmail, int addressId) {
        return custAddressService.getAddressDetail(customerEmail, addressId);
    }

//    @PutMapping("/updateAdmin")
//    public ResponseEntity<BaseResponse<?>> updateRestAgent(@Valid @RequestBody UpdateAdminRequest adminRequest) {
//        return restAgentService.updateAdmin(adminRequest);
//    }

    @DeleteMapping("/{custAddressId}")
    public ResponseEntity<BaseResponse<?>> deleteCustAddress(@PathVariable int custAddressId) {
        return custAddressService.deleteCustomerAddress(custAddressId);
    }
}
