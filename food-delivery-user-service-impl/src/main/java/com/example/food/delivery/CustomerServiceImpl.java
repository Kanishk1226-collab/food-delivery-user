package com.example.food.delivery;

import com.example.food.delivery.JwtAuth.JwtUtils;
import com.example.food.delivery.Request.CartRequest;
import com.example.food.delivery.Request.CustomerRequest;
import com.example.food.delivery.Request.DeliveryAgentRequest;
import com.example.food.delivery.Request.LoginRequest;
import com.example.food.delivery.Response.BaseResponse;
import com.example.food.delivery.Response.JwtResponse;
import com.example.food.delivery.Response.ResponseStatus;
import com.example.food.delivery.Response.UserCredentials;
import com.example.food.delivery.ServiceInterface.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private ObjectMapper mapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JwtUtils jwtUtil;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailServiceImpl;

    public synchronized ResponseEntity<BaseResponse<?>> createCustomer(CustomerRequest customerRequest) {
        try {
            if (customerRepository.existsByCustEmail(customerRequest.getCustEmail())) {
                throw new UserManagementExceptions.UserAlreadyExistsException("User with this email already exists");
            }
            if (customerRepository.existsByCustPhone(customerRequest.getCustPhone())) {
                throw new UserManagementExceptions.UserAlreadyExistsException("User with this Phone number already exists");
            }
            Customer customer = new Customer();
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            customer.setCustName(customerRequest.getCustName());
            customer.setCustEmail(customerRequest.getCustEmail());
            customer.setCustPassword(bCryptPasswordEncoder.encode(customerRequest.getCustPassword()));
            customer.setCustPhone(customerRequest.getCustPhone());
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
            Customer customer = customerRepository.findByCustEmail(email);
            if (customer == null) {
                throw new UserManagementExceptions.UserNotFoundException("No Customer found for ID " + email);
            }
            UserCredentials userCredentials = new UserCredentials();
            userCredentials.setEmail(email);
            userCredentials.setRole("CUSTOMER");

            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(mapper.writeValueAsString(userCredentials), loginRequest.getPassword()));

            ObjectMapper objectMapper = new ObjectMapper();
            String serializedCredentials = objectMapper.writeValueAsString(userCredentials);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = userDetailServiceImpl.loadUserByUsername(serializedCredentials);
            String jwt = jwtUtil.generateJwtToken(userDetails);
            JwtResponse jwtResponse = JwtResponse.builder()
                    .token(jwt)
                    .username(customer.getCustName())
                    .email(customer.getCustEmail())
                    .role("CUSTOMER").build();
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, jwtResponse);
        } catch (Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public synchronized ResponseEntity<BaseResponse<?>> logoutCustomer(String customerEmail) {
        try {
            Customer customer = customerRepository.findByCustEmail(customerEmail);
            customerRepository.save(customer);
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, "Logout Successful");
        } catch (Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

//    public synchronized ResponseEntity<BaseResponse<?>> isCustomerLoggedIn(String customerEmail) {
//        try {
//            isValidEmail(customerEmail);
//            Customer customer = customerRepository.findByCustEmail(customerEmail);
//            if (customer == null) {
//                throw new UserManagementExceptions.UserNotFoundException("No Customer found for ID " + customerEmail);
//            }
////            if (!customer.getIsLoggedIn()) {
////                throw new UserManagementExceptions.LoginException("Customer not logged in");
////            }
//            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, "Customer has Logged In");
//        } catch (Exception e) {
//            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
//        }
//        return ResponseEntity.ok(response);
//    }


    public synchronized ResponseEntity<BaseResponse<?>> getAllCustomer(String email, int page) {
        try {
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

    public synchronized ResponseEntity<BaseResponse<?>> deleteCustomer(String custEmail) {
        try {
            Customer customer = customerRepository.findByCustEmail(custEmail);
            customerRepository.deleteById(customer.getCustId());
            restTemplate.delete("http://localhost:8083/order-service/cart/deleteCart?cartId=" + customer.getCustEmail());
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, "User removed Successfully");
        } catch (Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

//    public synchronized void isAdminLoggedIn(String adminEmail) {
//        isValidEmail(adminEmail);
//        Admin admin = adminRepository.findByAdminEmail(adminEmail);
//        if (admin == null) {
//            throw new UserManagementExceptions.UserNotFoundException("No Admin found for ID " + adminEmail);
//        }
//        if (!admin.getIsLoggedIn()) {
//            throw new UserManagementExceptions.LoginException("Admin not logged in");
//        }
//    }

//    public synchronized ResponseEntity<BaseResponse<?>> isValidCustomerEmail(String custEmail) {
//        try {
//            isValidEmail(custEmail);
//            Customer customer = customerRepository.findByCustEmail(custEmail);
//            if (customer == null) {
//                throw new UserManagementExceptions.UserNotFoundException("User not found with Email Id " + custEmail);
//            }
//            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, "User Found Successfully");
//        } catch (Exception e) {
//            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
//        }
//        return ResponseEntity.ok(response);
//    }

//    public void isValidEmail(String email) {
//        String regex = "^(.+)@(.+)$";
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(email);
//        if (!matcher.matches()) {
//            throw new UserManagementExceptions.InvalidInputException("Oser not found on Id " + email);
//        }
//    }

}
