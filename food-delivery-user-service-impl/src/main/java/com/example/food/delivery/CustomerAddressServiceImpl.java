package com.example.food.delivery;

import com.example.food.delivery.Request.CustomerAddressRequest;
import com.example.food.delivery.Request.CustomerRequest;
import com.example.food.delivery.Response.BaseResponse;
import com.example.food.delivery.Response.CustomerAddressResponse;
import com.example.food.delivery.Response.ResponseStatus;
import com.example.food.delivery.ServiceInterface.CustomerAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CustomerAddressServiceImpl implements CustomerAddressService {
    @Autowired
    private CustomerAddressRepository custAddressRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    public BaseResponse<?> response;

    public synchronized ResponseEntity<BaseResponse<?>> addAddress(CustomerAddressRequest custAddressRequest, String custEmail) {
        try {
            isValidEmail(custEmail);
            if (!customerRepository.existsByCustEmail(custEmail)) {
                throw new UserManagementExceptions.UserNotFoundException("User with this email not found");
            }
            CustomerAddress custAddress = new CustomerAddress();
            custAddress.setCustEmail(custEmail);
            custAddress.setDoorNo(custAddressRequest.getDoorNo());
            custAddress.setLocality(custAddressRequest.getLocality());
            custAddress.setCity(custAddressRequest.getCity());
            custAddress.setPincode(custAddressRequest.getPincode());
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, custAddressRepository.save(custAddress));
        } catch (Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public synchronized ResponseEntity<?> getAllCustomerAddress() {
        response = new BaseResponse<>(true,ResponseStatus.SUCCESS.getStatus(), null, custAddressRepository.findAll());
        return ResponseEntity.ok(response);
    }

    public synchronized ResponseEntity<BaseResponse<?>> deleteCustomerAddress(int addressId) {
        try {
            CustomerAddress custAddress = custAddressRepository.findById(addressId).orElse(null);
            if (custAddress == null) {
                throw new UserManagementExceptions.UserNotFoundException("Address Not Found");
            }
            custAddressRepository.deleteById(addressId);
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, "Address removed Successfully");
        } catch (Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public synchronized ResponseEntity<BaseResponse<?>> getCustomerAddress(String customerEmail) {
        try {
            isValidEmail(customerEmail);
            if (!customerRepository.existsByCustEmail(customerEmail)) {
                throw new UserManagementExceptions.UserNotFoundException("User with this email not found");
            }
            List<CustomerAddress> custAddress = custAddressRepository.findByCustEmail(customerEmail);
            if (custAddress == null || custAddress.isEmpty()) {
                throw new UserManagementExceptions.UserNotFoundException("No Address Registered");
            }
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, custAddress);
        } catch (Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public synchronized ResponseEntity<BaseResponse<?>> getAddressDetail(String customerEmail, int addressId) {
        try {
            isValidEmail(customerEmail);
            if (!customerRepository.existsByCustEmail(customerEmail)) {
                throw new UserManagementExceptions.AddressNotFoundException("User with this email not found");
            }
            Optional<CustomerAddress> optCustAddress = custAddressRepository.findById(addressId);
            if (!optCustAddress.isPresent()) {
                throw new UserManagementExceptions.AddressNotFoundException("No Address Found");
            }
            CustomerAddress custAddress = optCustAddress.get();
            if(!customerEmail.equalsIgnoreCase(custAddress.getCustEmail())) {
                throw new UserManagementExceptions.AddressNotFoundException("Customer email with addressId doesn't match");
            }
            Customer customer = customerRepository.findByCustEmail(customerEmail);
            CustomerAddressResponse custResponse = new CustomerAddressResponse();
            custResponse.setCustName(customer.getCustName());
            custResponse.setCustEmail(customer.getCustEmail());
            custResponse.setCustPhone(customer.getCustPhone());
            custResponse.setCity(custAddress.getCity());
            custResponse.setDoorNo(custAddress.getDoorNo());
            custResponse.setLocality(custAddress.getLocality());
            custResponse.setPincode(custAddress.getPincode());
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, custResponse);
        } catch (Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public void isValidEmail(String email) {
        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        if(!matcher.matches()) {
            throw new UserManagementExceptions.InvalidInputException("Oser not found on Id " + email);
        }
    }
}
