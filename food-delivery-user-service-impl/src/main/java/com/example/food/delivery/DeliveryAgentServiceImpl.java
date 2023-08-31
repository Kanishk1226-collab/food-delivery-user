package com.example.food.delivery;

import com.example.food.delivery.Request.DeliveryAgentRequest;
import com.example.food.delivery.Request.LoginRequest;
import com.example.food.delivery.Request.RequestRestAgent;
import com.example.food.delivery.Request.RestaurantAgentRequest;
import com.example.food.delivery.Response.BaseResponse;
import com.example.food.delivery.Response.DeliveryAgentResponse;
import com.example.food.delivery.Response.ResponseStatus;
import com.example.food.delivery.ServiceInterface.DeliveryAgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DeliveryAgentServiceImpl implements DeliveryAgentService {
    @Autowired
    private DeliveryAgentRepository delAgentRepository;

    @Autowired
    public BaseResponse<?> response;

    @Autowired
    private RestTemplate restTemplate;

    public synchronized ResponseEntity<BaseResponse<?>> createDeliveryAgent(DeliveryAgentRequest delAgentRequest) {
        try{
            if (delAgentRepository.existsByDelAgentEmail(delAgentRequest.getDelAgentEmail())) {
                throw new UserManagementExceptions.UserAlreadyExistsException("User with this email already exists");
            }
            DeliveryAgent deliveryAgent = new DeliveryAgent();
            deliveryAgent.setDelAgentName(delAgentRequest.getDelAgentName());
            deliveryAgent.setDelAgentEmail(delAgentRequest.getDelAgentEmail());
            deliveryAgent.setDelAgentPassword(delAgentRequest.getDelAgentPassword());
            deliveryAgent.setDelAgentPhone(delAgentRequest.getDelAgentPhone());
            deliveryAgent.setRestAgentEmail(null);
            deliveryAgent.setIsVerified(false);
            deliveryAgent.setIsAvailable(false);
            deliveryAgent.setIsLoggedIn(false);
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null,  delAgentRepository.save(deliveryAgent));
        }
        catch (Exception e)
        {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public synchronized ResponseEntity<BaseResponse<?>> loginDelAgent(LoginRequest loginRequest) {
        try {
            String email = loginRequest.getEmail();
            String password = loginRequest.getPassword();
            DeliveryAgent delAgent = delAgentRepository.findByDelAgentEmail(email);

            if (delAgent == null) {
                throw new UserManagementExceptions.UserNotFoundException("No Delivery Agent found for ID " + email);
            }

            if (delAgent.getIsLoggedIn()) {
                throw new UserManagementExceptions.LoginException("Delivery Agent already logged in");
            }
            if (!delAgent.getDelAgentPassword().equals(password)) {
                throw new UserManagementExceptions.LoginException("Invalid Password");
            }
            delAgent.setIsLoggedIn(true);
            delAgentRepository.save(delAgent);
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, "Login Successful");
        } catch(Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public synchronized ResponseEntity<BaseResponse<?>> logoutDelAgent(String delAgentEmail) {
        try {
            if(!isValidEmail(delAgentEmail)) {
                throw new UserManagementExceptions.InvalidInputException("Enter Valid Email Id");
            }
            DeliveryAgent delAgent = delAgentRepository.findByDelAgentEmail(delAgentEmail);
            if (delAgent == null) {
                throw new UserManagementExceptions.UserNotFoundException("No Delivery Agent found for ID " + delAgentEmail);
            }
            if (!delAgent.getIsLoggedIn()) {
                throw new UserManagementExceptions.LoginException("Delivery Agent not logged in");
            }
            delAgent.setIsLoggedIn(false);
            delAgentRepository.save(delAgent);
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, "Logout Successful");
        } catch(Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public synchronized ResponseEntity<BaseResponse<?>> setDelAgentAvailability(String delAgentEmail) {
        try {
            isValidDelAgent(delAgentEmail);
            DeliveryAgent delAgent = delAgentRepository.findByDelAgentEmail(delAgentEmail);
            delAgent.setIsAvailable(true);
            delAgentRepository.save(delAgent);
            setRestAvailability(delAgent.getRestAgentEmail(), true);
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, "Availability status changed");
        } catch(Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public void isValidDelAgent(String delAgentEmail) {
        if(!isValidEmail(delAgentEmail)) {
            throw new UserManagementExceptions.InvalidInputException("Enter Valid Email Id");
        }
        DeliveryAgent delAgent = delAgentRepository.findByDelAgentEmail(delAgentEmail);
        if (delAgent == null) {
            throw new UserManagementExceptions.UserNotFoundException("No Delivery Agent found for ID " + delAgentEmail);
        }
        if (!delAgent.getIsLoggedIn()) {
            throw new UserManagementExceptions.LoginException("Delivery Agent not logged in");
        }
    }

    public synchronized ResponseEntity<BaseResponse<?>> isDelAgentLoggedIn(String delAgentEmail) {
        try {
            if(!isValidEmail(delAgentEmail)) {
                throw new UserManagementExceptions.InvalidInputException("Enter Valid Email Id");
            }
            DeliveryAgent delAgent = delAgentRepository.findByDelAgentEmail(delAgentEmail);
            if (delAgent == null) {
                throw new UserManagementExceptions.UserNotFoundException("No Delivery Agent found for ID " + delAgentEmail);
            }
            if (!delAgent.getIsLoggedIn()) {
                throw new UserManagementExceptions.LoginException("Delivery Agent not logged in");
            }
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, "Delivery Agent has Logged In");
        } catch(Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public synchronized ResponseEntity<?> getAllDeliveryAgents() {
        response = new BaseResponse<>(true,ResponseStatus.SUCCESS.getStatus(), null, delAgentRepository.findAll());
        return ResponseEntity.ok(response);
    }

    public synchronized ResponseEntity<BaseResponse<?>> deleteDeliveryAgent(int delAgentId) {
        try {
            DeliveryAgent deliveryAgent = delAgentRepository.findById(delAgentId).orElse(null);
            if (deliveryAgent == null) {
                throw new UserManagementExceptions.UserNotFoundException("User not found with ID " + delAgentId);
            }
            delAgentRepository.deleteById(delAgentId);
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, "User removed Successfully");
        } catch (Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public synchronized ResponseEntity<BaseResponse<?>> deliveryRequest(RequestRestAgent requestRestAgent) {
        try {
            String delAgentEmail = requestRestAgent.getDelAgentEmail();
            String restAgentEmail = requestRestAgent.getRestAgentEmail();
            if(!isValidEmail(delAgentEmail)) {
                throw new UserManagementExceptions.InvalidInputException("Enter Valid Delivery Agent Email");
            }
            if(!isValidEmail(restAgentEmail)) {
                throw new UserManagementExceptions.InvalidInputException("Enter Valid Restaurant Agent Email");
            }
            BaseResponse<?> isVerifiedRestaurant = restTemplate.getForObject("http://localhost:8082/restaurant-service/restaurant/isVerifiedRestaurant?restAgentEmail=" + restAgentEmail, BaseResponse.class);
            if(!isVerifiedRestaurant.isSuccess()) {
                throw new UserManagementExceptions.RestTemplateException(isVerifiedRestaurant.getError());
            }
            DeliveryAgent deliveryAgent = delAgentRepository.findByDelAgentEmail(delAgentEmail);
            if (deliveryAgent == null) {
                throw new UserManagementExceptions.UserNotFoundException("User not found with ID " + delAgentEmail);
            }
            deliveryAgent.setRestAgentEmail(restAgentEmail);
            delAgentRepository.save(deliveryAgent);
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, "Requested for Delivery Agent!");
        } catch (Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public synchronized ResponseEntity<BaseResponse<?>> assignDeliveryAgent(String restAgentEmail) {
        try {
            if(!isValidEmail(restAgentEmail)) {
                throw new UserManagementExceptions.InvalidInputException("Enter Valid Restaurant Agent Email");
            }
            BaseResponse<?> isVerifiedRestaurant = restTemplate.getForObject("http://localhost:8082/restaurant-service/restaurant/isVerifiedRestaurant?restAgentEmail=" + restAgentEmail, BaseResponse.class);
            if(!isVerifiedRestaurant.isSuccess()) {
                throw new UserManagementExceptions.RestTemplateException(isVerifiedRestaurant.getError());
            }
            List<DeliveryAgent> deliveryAgent = delAgentRepository.findByRestAgentEmailAndIsAvailable(restAgentEmail, true);
            if (deliveryAgent == null || deliveryAgent.isEmpty()) {
                throw new UserManagementExceptions.UserNotFoundException("Delivery Agent Not Available");
            }
            deliveryAgent.get(0).setIsAvailable(false);
            delAgentRepository.saveAll(deliveryAgent);
            DeliveryAgentResponse deliveryAgentResponse = new DeliveryAgentResponse();
            deliveryAgentResponse.setDelAgentName(deliveryAgent.get(0).getDelAgentName());
            deliveryAgentResponse.setDelAgentEmail(deliveryAgent.get(0).getDelAgentEmail());
            deliveryAgentResponse.setDelAgentPhone(deliveryAgent.get(0).getDelAgentPhone());
            if(deliveryAgent.size() == 1) {
                setRestAvailability(deliveryAgent.get(0).getRestAgentEmail(), false);
            }
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, deliveryAgentResponse);
        } catch (Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public void setRestAvailability(String restAgentEmail, boolean isAvail) {
        String url = "http://localhost:8082/restaurant-service/restaurant/setRestaurantAvailability?restAgentEmail=" + restAgentEmail + "&isAvail=" + isAvail;
        BaseResponse<?> getRestAvail;
        ResponseEntity<BaseResponse<?>> responseEntity3 =
                restTemplate.exchange(url, HttpMethod.PUT, null,
                        new ParameterizedTypeReference<BaseResponse<?>>() {});
        if (responseEntity3.getStatusCode().is2xxSuccessful()) {
            getRestAvail = responseEntity3.getBody();
            if (!getRestAvail.isSuccess()) {
                throw new UserManagementExceptions.RestTemplateException(getRestAvail.getError());
            }
        }
    }

    public synchronized ResponseEntity<BaseResponse<?>> checkDeliveryAgentAvailability(String restAgentEmail) {
        try {
            if(!isValidEmail(restAgentEmail)) {
                throw new UserManagementExceptions.InvalidInputException("Enter Valid Restaurant Agent Email");
            }
            List<DeliveryAgent> deliveryAgent = delAgentRepository.findByRestAgentEmailAndIsAvailable(restAgentEmail, true);
            if (deliveryAgent == null || deliveryAgent.isEmpty()) {
                throw new UserManagementExceptions.UserNotFoundException("Delivery Agent Not Available");
            }

            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, "Delivery Agent Available");
        } catch (Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public boolean isValidEmail(String email) {
        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
