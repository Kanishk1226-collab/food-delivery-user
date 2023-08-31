package com.example.food.delivery;

import com.example.food.delivery.Request.CartRequest;
import com.example.food.delivery.Request.CustomerRequest;
import com.example.food.delivery.Request.DeliveryAgentRequest;
import com.example.food.delivery.Request.LoginRequest;
import com.example.food.delivery.Response.BaseResponse;
import com.example.food.delivery.Response.ResponseStatus;
import com.example.food.delivery.ServiceInterface.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    public BaseResponse<?> response;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private RestTemplate restTemplate;

    public synchronized ResponseEntity<BaseResponse<?>> createCustomer(CustomerRequest customerRequest) {
        try {
            if (customerRepository.existsByCustEmail(customerRequest.getCustEmail())) {
                throw new UserManagementExceptions.UserAlreadyExistsException("User with this email already exists");
            }
            Customer customer = new Customer();
            customer.setCustName(customerRequest.getCustName());
            customer.setCustEmail(customerRequest.getCustEmail());
            customer.setCustPassword(customerRequest.getCustPassword());
            customer.setCustPhone(customerRequest.getCustPhone());
            customer.setIsLoggedIn(false);
            CartRequest cartRequest = new CartRequest();
            cartRequest.setCartId(customerRequest.getCustEmail());
            BaseResponse<?> createCart = restTemplate.postForObject("http://localhost:8083/order-service/cart/createCart", cartRequest, BaseResponse.class);
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, customerRepository.save(customer));
        } catch (Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }


    public synchronized ResponseEntity<BaseResponse<?>> loginCustomer(LoginRequest loginRequest) {
        try {
            String email = loginRequest.getEmail();
            String password = loginRequest.getPassword();
            Customer customer = customerRepository.findByCustEmail(email);
            if (customer == null) {
                throw new UserManagementExceptions.UserNotFoundException("No Customer found for ID " + email);
            }
            if (customer.getIsLoggedIn()) {
                throw new UserManagementExceptions.LoginException("Customer already logged in");
            }
            if (!customer.getCustPassword().equals(password)) {
                throw new UserManagementExceptions.LoginException("Invalid Password");
            }
            customer.setIsLoggedIn(true);
            customerRepository.save(customer);
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, "Login Successful");
        } catch (Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public synchronized ResponseEntity<BaseResponse<?>> logoutCustomer(String customerEmail) {
        try {
            isValidEmail(customerEmail);
            Customer customer = customerRepository.findByCustEmail(customerEmail);
            if (customer == null) {
                throw new UserManagementExceptions.UserNotFoundException("No Customer found for ID " + customerEmail);
            }
            if (!customer.getIsLoggedIn()) {
                throw new UserManagementExceptions.LoginException("Customer not logged in");
            }
            customer.setIsLoggedIn(false);
            customerRepository.save(customer);
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, "Logout Successful");
        } catch (Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public synchronized ResponseEntity<BaseResponse<?>> isCustomerLoggedIn(String customerEmail) {
        try {
            isValidEmail(customerEmail);
            Customer customer = customerRepository.findByCustEmail(customerEmail);
            if (customer == null) {
                throw new UserManagementExceptions.UserNotFoundException("No Customer found for ID " + customerEmail);
            }
            if (!customer.getIsLoggedIn()) {
                throw new UserManagementExceptions.LoginException("Customer not logged in");
            }
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, "Customer has Logged In");
        } catch (Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }


    public synchronized ResponseEntity<BaseResponse<?>> getAllCustomer(String email, int page) {
        try {
            isAdminLoggedIn(email);
            int pageSize = 10;
            Sort sortById = Sort.by(Sort.Direction.DESC, "custId");
            PageRequest pageRequest = PageRequest.of(page, pageSize, sortById);
            Page<Customer> customer = customerRepository.findAll(pageRequest);
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, customer.getContent());
        } catch (Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public synchronized ResponseEntity<BaseResponse<?>> deleteCustomer(int customerId) {
        try {
            Customer customer = customerRepository.findById(customerId).orElse(null);
            if (customer == null) {
                throw new UserManagementExceptions.UserNotFoundException("User not found with ID " + customerId);
            }
            customerRepository.deleteById(customerId);
            restTemplate.delete("http://localhost:8083/order-service/cart/deleteCart?cartId=" + customer.getCustEmail());
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, "User removed Successfully");
        } catch (Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public synchronized void isAdminLoggedIn(String adminEmail) {
        isValidEmail(adminEmail);
        Admin admin = adminRepository.findByAdminEmail(adminEmail);
        if (admin == null) {
            throw new UserManagementExceptions.UserNotFoundException("No Admin found for ID " + adminEmail);
        }
        if (!admin.getIsLoggedIn()) {
            throw new UserManagementExceptions.LoginException("Admin not logged in");
        }
    }

    public synchronized ResponseEntity<BaseResponse<?>> isValidCustomerEmail(String custEmail) {
        try {
            isValidEmail(custEmail);
            Customer customer = customerRepository.findByCustEmail(custEmail);
            if (customer == null) {
                throw new UserManagementExceptions.UserNotFoundException("User not found with Email Id " + custEmail);
            }
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, "User Found Successfully");
        } catch (Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public void isValidEmail(String email) {
        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            throw new UserManagementExceptions.InvalidInputException("Oser not found on Id " + email);
        }
    }

}
