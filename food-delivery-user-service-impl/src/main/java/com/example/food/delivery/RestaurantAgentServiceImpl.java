package com.example.food.delivery;

import com.example.food.delivery.JwtAuth.JwtUtils;
import com.example.food.delivery.Request.*;
import com.example.food.delivery.Response.BaseResponse;
import com.example.food.delivery.Response.JwtResponse;
import com.example.food.delivery.Response.ResponseStatus;
import com.example.food.delivery.Response.UserCredentials;
import com.example.food.delivery.ServiceInterface.RestaurantAgentService;
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
public class RestaurantAgentServiceImpl implements RestaurantAgentService {
    @Autowired
    private RestaurantAgentRepository restAgentRepository;

    @Autowired
    private DeliveryAgentRepository delAgentRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    public BaseResponse<?> response;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JwtUtils jwtUtil;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailServiceImpl;

    public synchronized ResponseEntity<BaseResponse<?>> createRestAgent(RestaurantAgentRequest restAgentRequest) {
        try {
            if (restAgentRepository.existsByRestAgentEmail(restAgentRequest.getRestAgentEmail())) {
                throw new UserManagementExceptions.UserAlreadyExistsException("User with this email already exists");
            }
            RestaurantAgent restaurantAgent = new RestaurantAgent();
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            restaurantAgent.setRestAgentName(restAgentRequest.getRestAgentName());
            restaurantAgent.setRestAgentEmail(restAgentRequest.getRestAgentEmail());
            restaurantAgent.setRestAgentPassword(bCryptPasswordEncoder.encode(restAgentRequest.getRestAgentPassword()));
            restaurantAgent.setRestAgentPhone(restAgentRequest.getRestAgentPhone());
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, restAgentRepository.save(restaurantAgent));
        } catch (Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public synchronized ResponseEntity<BaseResponse<?>> loginRestAgent(LoginRequest loginRequest) {
        try {
            String email = loginRequest.getEmail();
            RestaurantAgent restAgent = restAgentRepository.findByRestAgentEmail(email);
            if (restAgent == null) {
                throw new UserManagementExceptions.UserNotFoundException("No Restaurant Agent found for ID " + email);
            }
            UserCredentials userCredentials = new UserCredentials();
            userCredentials.setEmail(email);
            userCredentials.setRole("RESTAURANT_AGENT");
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(mapper.writeValueAsString(userCredentials), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            ObjectMapper objectMapper = new ObjectMapper();
            String serializedCredentials = objectMapper.writeValueAsString(userCredentials);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = userDetailServiceImpl.loadUserByUsername(serializedCredentials);
            String jwt = jwtUtil.generateJwtToken(userDetails);
            JwtResponse jwtResponse = JwtResponse.builder()
                    .token(jwt)
                    .username(  restAgent.getRestAgentName())
                    .email(restAgent.getRestAgentEmail())
                    .role("RESTAURANT_AGENT").build();
            restAgentRepository.save(restAgent);
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, jwtResponse);
        } catch(Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public synchronized ResponseEntity<BaseResponse<?>> logoutRestAgent(String restAgentEmail) {
        try {
            isValidEmail(restAgentEmail);
            RestaurantAgent restAgent = restAgentRepository.findByRestAgentEmail(restAgentEmail);

            restAgentRepository.save(restAgent);
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, "Logout Successful");
        } catch(Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }




//    public synchronized ResponseEntity<?> isValidRestAgent(String restAgentEmail) {
//        try {
//            isValidEmail(restAgentEmail);
//            RestaurantAgent restAgent = restAgentRepository.findByRestAgentEmail(restAgentEmail);
//            if(restAgent == null) {
//                throw new UserManagementExceptions.UserNotFoundException("No user found");
//            }
//            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, "Valid Rest Agent");
//        } catch(Exception e) {
//            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
//        }
//        return ResponseEntity.ok(response);
//    }

    public synchronized ResponseEntity<BaseResponse<?>> getAllRestAgents(int page) {
        try {
//            isAdminLoggedIn(email);
            int pageSize = 10;
            Sort sortById = Sort.by(Sort.Direction.DESC, "restAgentId");
            PageRequest pageRequest = PageRequest.of(page, pageSize, sortById);
            Page<RestaurantAgent> restAgents = restAgentRepository.findAll(pageRequest);
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, restAgents.getContent());
        } catch (Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public synchronized ResponseEntity<BaseResponse<?>> deleteRestAgent(String restAgentEmail) {
        try {
            RestaurantAgent restaurantAgent = restAgentRepository.findByRestAgentEmail(restAgentEmail);
            restAgentRepository.deleteById(restaurantAgent.getRestAgentId());
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, "User removed Successfully");
        } catch (Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

//    public synchronized void isAdminLoggedIn(String adminEmail) {
//            isValidEmail(adminEmail);
//            Admin admin = adminRepository.findByAdminEmail(adminEmail);
//            if (admin == null) {
//                throw new UserManagementExceptions.UserNotFoundException("No Admin found for ID " + adminEmail);
//            }
//            if (!admin.getIsLoggedIn()) {
//                throw new UserManagementExceptions.LoginException("Admin not logged in");
//            }
//    }

    public synchronized ResponseEntity<?> approveDeliveryAgent(String delAgentEmail, String restAgentEmail) {
        try {
            isValidEmail(delAgentEmail);
            isValidEmail(restAgentEmail);
            DeliveryAgent deliveryAgent = delAgentRepository.findByDelAgentEmail(delAgentEmail);
            if (deliveryAgent == null) {
                throw new UserManagementExceptions.UserNotFoundException("No Delivery Agent found with ID " + delAgentEmail);
            }
            if(deliveryAgent.getRestAgentEmail() == null || !deliveryAgent.getRestAgentEmail().equals(restAgentEmail)) {
                throw new UserManagementExceptions.VerificationFailureException("No request for Delivery from " + deliveryAgent);
            }
            if(deliveryAgent.getIsVerified()) {
                throw new UserManagementExceptions.VerificationFailureException("Delivery Agent already verified");
            }
            deliveryAgent.setIsVerified(true);
            deliveryAgent.setStatus(DelAgentStatus.AVAILABLE);
            delAgentRepository.save(deliveryAgent);
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, "Approved Delivery Agent");
        } catch(Exception e) {
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
