package com.example.food.delivery;

import com.example.food.delivery.JwtAuth.JwtUtils;
import com.example.food.delivery.Request.*;
import com.example.food.delivery.Response.*;
import com.example.food.delivery.ServiceInterface.DeliveryAgentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtil;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserDetailsServiceImpl userDetailServiceImpl;

    public synchronized ResponseEntity<BaseResponse<?>> createDeliveryAgent(DeliveryAgentRequest delAgentRequest) {
        try{
            if (delAgentRepository.existsByDelAgentEmail(delAgentRequest.getDelAgentEmail())) {
                throw new UserManagementExceptions.UserAlreadyExistsException("User with this email already exists");
            }
            if(delAgentRepository.existsByDelAgentPhone(delAgentRequest.getDelAgentPhone())) {
                throw new UserManagementExceptions.UserAlreadyExistsException("User with this phone no. already exists");
            }
            DeliveryAgent deliveryAgent = new DeliveryAgent();
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            deliveryAgent.setDelAgentName(delAgentRequest.getDelAgentName());
            deliveryAgent.setDelAgentEmail(delAgentRequest.getDelAgentEmail());
            deliveryAgent.setDelAgentPassword(bCryptPasswordEncoder.encode(delAgentRequest.getDelAgentPassword()));
            deliveryAgent.setDelAgentPhone(delAgentRequest.getDelAgentPhone());
            deliveryAgent.setRestAgentEmail(null);
            deliveryAgent.setIsVerified(false);
            deliveryAgent.setStatus(null);
            deliveryAgent.setDeliveryCount(0);
            delAgentRepository.save(deliveryAgent);
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null,  "Account created Successfully");
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
            DeliveryAgent delAgent = delAgentRepository.findByDelAgentEmail(email);
            if (delAgent == null) {
                throw new UserManagementExceptions.UserNotFoundException("No Delivery Agent found with ID " + email);
            }
            UserCredentials userCredentials = new UserCredentials();
            userCredentials.setEmail(email);
            userCredentials.setRole("DELIVERY_AGENT");
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
                    .username(delAgent.getDelAgentName())
                    .email(delAgent.getDelAgentEmail())
                    .role("DELIVERY_AGENT").build();
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, jwtResponse);
        } catch(Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public synchronized ResponseEntity<BaseResponse<?>> logoutDelAgent(String delAgentEmail) {
        try {
            DeliveryAgent delAgent = delAgentRepository.findByDelAgentEmail(delAgentEmail);
            delAgentRepository.save(delAgent);
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, "Logout Successful");
        } catch(Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public synchronized ResponseEntity<BaseResponse<?>> setDelAgentAvailability(String delAgentEmail, String status) {
        try {
            String delAgentstatus = status.toUpperCase();
            String responseMessage = "Availability status changed";
            if(!EnumUtils.isValidEnum(DelAgentStatus.class, delAgentstatus)) {
                throw new UserManagementExceptions.InvalidInputException("Status should be either AVAILABLE or NOT_AVAILABLE");
            }
//            isValidDelAgent(delAgentEmail);
            DeliveryAgent delAgent = delAgentRepository.findByDelAgentEmail(delAgentEmail);
            if(!delAgent.getIsVerified()) {
                throw new UserManagementExceptions.UnauthorizedAccessException("You need to get verified by Restaurant Agent in order change status.");
            }
            if(delAgent.getStatus() == DelAgentStatus.ON_DELIVERY && status.equals("NOT_AVAILABLE")) {
                throw new UserManagementExceptions.UnauthorizedAccessException("You can't change your status while you're in ON_DELIVERY");
            }

            if(delAgentstatus.equals("NOT_AVAILABLE")) {
                responseMessage = responseMessage + "Note: Changing the status to NOT AVAILABLE, it is Delivery Agent's responsibility " +
                        "to change the status back to AVAILABLE.";
                List<DeliveryAgent> deliveryAgent = delAgentRepository.findByRestAgentEmailAndStatusOrderByDeliveryCountAsc(delAgent.getRestAgentEmail(), DelAgentStatus.AVAILABLE);
                if (deliveryAgent == null || deliveryAgent.isEmpty() || deliveryAgent.size() == 1) {
                    throw new UserManagementExceptions.UserNotFoundException("Delivery Agent Not Available");
                }
                setRestAvailability(delAgent.getRestAgentEmail(), "NOT_DELIVERABLE");
            } else {
                setRestAvailability(delAgent.getRestAgentEmail(), "AVAILABLE");
            }
            delAgent.setStatus(DelAgentStatus.valueOf(delAgentstatus));
            delAgentRepository.save(delAgent);
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, responseMessage);
        } catch(Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public synchronized ResponseEntity<?> getAllDeliveryAgents() {
        response = new BaseResponse<>(true,ResponseStatus.SUCCESS.getStatus(), null, delAgentRepository.findAll());
        return ResponseEntity.ok(response);
    }

    public synchronized ResponseEntity<BaseResponse<?>> deleteDeliveryAgent(String delAgentEmail) {
        try {
            DeliveryAgent deliveryAgent = delAgentRepository.findByDelAgentEmail(delAgentEmail);
            delAgentRepository.deleteById(deliveryAgent.getDelAgentId());
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, "User removed Successfully");
        } catch (Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public synchronized ResponseEntity<BaseResponse<?>> deliveryRequest(String delAgentEmail, String restAgentEmail) {
        try {
            if(!isValidEmail(restAgentEmail)) {
                throw new UserManagementExceptions.InvalidInputException("Enter Valid Restaurant Agent Email");
            }
            BaseResponse<?> isVerifiedRestaurant = restTemplate.getForObject("http://localhost:8082/restaurant-service/restaurant/isVerified?restAgentEmail=" + restAgentEmail, BaseResponse.class);
            if(!isVerifiedRestaurant.isSuccess()) {
                throw new UserManagementExceptions.RestTemplateException(isVerifiedRestaurant.getError());
            }
            DeliveryAgent deliveryAgent = delAgentRepository.findByDelAgentEmail(delAgentEmail);
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

            List<DeliveryAgent> deliveryAgent = delAgentRepository.findByRestAgentEmailAndStatusOrderByDeliveryCountAsc(restAgentEmail, DelAgentStatus.AVAILABLE);
            if (deliveryAgent == null || deliveryAgent.isEmpty() || deliveryAgent.size() == 0) {
                throw new UserManagementExceptions.UserNotFoundException("Delivery Agent Not Available");
            }
            deliveryAgent.get(0).setStatus(DelAgentStatus.ON_DELIVERY);
            DeliveryAgentResponse deliveryAgentResponse = new DeliveryAgentResponse();
            deliveryAgentResponse.setDelAgentName(deliveryAgent.get(0).getDelAgentName());
            deliveryAgentResponse.setDelAgentEmail(deliveryAgent.get(0).getDelAgentEmail());
            deliveryAgentResponse.setDelAgentPhone(deliveryAgent.get(0).getDelAgentPhone());
            if(deliveryAgent.size() == 1) {
                setRestAvailability(deliveryAgent.get(0).getRestAgentEmail(), "NOT_DELIVERABLE");
            }
            delAgentRepository.saveAll(deliveryAgent);
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, deliveryAgentResponse);
        } catch (Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public void setRestAvailability(String restAgentEmail, String status) {
        String url = "http://localhost:8082/restaurant-service/restaurant/status?restAgentEmail=" + restAgentEmail + "&status=" + status;
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
            List<DeliveryAgent> deliveryAgent = delAgentRepository.findByRestAgentEmailAndStatus(restAgentEmail, DelAgentStatus.AVAILABLE);
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
