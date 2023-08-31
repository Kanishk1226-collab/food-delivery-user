package com.example.food.delivery;

import com.example.food.delivery.Request.*;
import com.example.food.delivery.Response.BaseResponse;
import com.example.food.delivery.Response.ResponseStatus;
import com.example.food.delivery.ServiceInterface.RestaurantAgentService;
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

    public synchronized ResponseEntity<BaseResponse<?>> createRestAgent(RestaurantAgentRequest restAgentRequest) {
        try {
            if (restAgentRepository.existsByRestAgentEmail(restAgentRequest.getRestAgentEmail())) {
                throw new UserManagementExceptions.UserAlreadyExistsException("User with this email already exists");
            }
            RestaurantAgent restaurantAgent = new RestaurantAgent();
            restaurantAgent.setRestId(0);
            restaurantAgent.setRestAgentName(restAgentRequest.getRestAgentName());
            restaurantAgent.setRestAgentEmail(restAgentRequest.getRestAgentEmail());
            restaurantAgent.setRestAgentPassword(restAgentRequest.getRestAgentPassword());
            restaurantAgent.setRestAgentPhone(restAgentRequest.getRestAgentPhone());
            restaurantAgent.setIsLoggedIn(false);
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, restAgentRepository.save(restaurantAgent));
        } catch (Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public synchronized ResponseEntity<BaseResponse<?>> loginRestAgent(LoginRequest loginRequest) {
        try {
            String email = loginRequest.getEmail();
            String password = loginRequest.getPassword();
            RestaurantAgent restAgent = restAgentRepository.findByRestAgentEmail(email);
            if (restAgent == null) {
                throw new UserManagementExceptions.UserNotFoundException("No Restaurant Agent found for ID " + email);
            }
            if (restAgent.getIsLoggedIn()) {
                throw new UserManagementExceptions.LoginException("Restaurant Agent already logged in");
            }
            if (!restAgent.getRestAgentPassword().equals(password)) {
                throw new UserManagementExceptions.LoginException("Invalid Password");
            }
            restAgent.setIsLoggedIn(true);
            restAgentRepository.save(restAgent);
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, "Login Successful");
        } catch(Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public synchronized ResponseEntity<BaseResponse<?>> logoutRestAgent(String restAgentEmail) {
        try {
            isValidEmail(restAgentEmail);
            RestaurantAgent restAgent = restAgentRepository.findByRestAgentEmail(restAgentEmail);
            if (restAgent == null) {
                throw new UserManagementExceptions.UserNotFoundException("No Restaurant Agent found for ID " + restAgentEmail);
            }
            if (!restAgent.getIsLoggedIn()) {
                throw new UserManagementExceptions.LoginException("Restaurant Agent not logged in");
            }
            restAgent.setIsLoggedIn(false);
            restAgentRepository.save(restAgent);
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, "Logout Successful");
        } catch(Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public synchronized ResponseEntity<BaseResponse<?>> isRestAgentLoggedIn(String restAgentEmail) {
        try {
            isValidEmail(restAgentEmail);
            RestaurantAgent restAgent = restAgentRepository.findByRestAgentEmail(restAgentEmail);
            if (restAgent == null) {
                throw new UserManagementExceptions.UserNotFoundException("No Restaurant Agent found for ID " + restAgentEmail);
            }
            if (!restAgent.getIsLoggedIn()) {
                throw new UserManagementExceptions.LoginException("Restaurant Agent not logged in");
            }
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, "Restaurant Agent has Logged In");
        } catch(Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }


    public synchronized ResponseEntity<?> isValidRestAgent(String restAgentEmail) {
        try {
            isValidEmail(restAgentEmail);
            RestaurantAgent restAgent = restAgentRepository.findByRestAgentEmail(restAgentEmail);
            if(restAgent == null) {
                throw new UserManagementExceptions.UserNotFoundException("No user found");
            }
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, "Valid Rest Agent");
        } catch(Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public synchronized ResponseEntity<BaseResponse<?>> getAllRestAgents(String email, int page) {
        try {
            isAdminLoggedIn(email);
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

    public synchronized ResponseEntity<BaseResponse<?>> deleteRestAgent(int restAgentId) {
        try {
            RestaurantAgent restaurantAgent = restAgentRepository.findById(restAgentId).orElse(null);
            if (restaurantAgent == null) {
                throw new UserManagementExceptions.UserNotFoundException("User not found with ID " + restAgentId);
            }
            restAgentRepository.deleteById(restAgentId);
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

    public synchronized ResponseEntity<?> approveDeliveryAgent(RequestRestAgent requestRestAgent) {
        try {
            String delAgentEmail = requestRestAgent.getDelAgentEmail();
            String restAgentEmail = requestRestAgent.getRestAgentEmail();
            isValidEmail(delAgentEmail);
            isValidEmail(restAgentEmail);
            RestaurantAgent restAgent = restAgentRepository.findByRestAgentEmail(restAgentEmail);
            if(restAgent == null) {
                throw new UserManagementExceptions.UserNotFoundException("No Restaurant Agent found with ID " + restAgentEmail);
            }
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
            deliveryAgent.setIsAvailable(true);
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
