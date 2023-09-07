package com.example.food.delivery.ServiceInterface;

import com.example.food.delivery.Request.CustomerAddressRequest;
import com.example.food.delivery.Response.BaseResponse;
import org.springframework.http.ResponseEntity;

public interface CustomerAddressService {
    ResponseEntity<BaseResponse<?>> addAddress(CustomerAddressRequest custAddressRequest, String custEmail);
    ResponseEntity<?> getAllCustomerAddress();
    ResponseEntity<BaseResponse<?>> deleteCustomerAddress(int addressId, String custEmail);
    ResponseEntity<BaseResponse<?>> getCustomerAddress(String customerEmail);
    ResponseEntity<BaseResponse<?>> getAddressDetail(String customerEmail, int addressId);
}
