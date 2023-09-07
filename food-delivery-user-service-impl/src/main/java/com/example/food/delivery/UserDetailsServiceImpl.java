package com.example.food.delivery;

import com.example.food.delivery.JwtAuth.JwtUtils;
import com.example.food.delivery.Response.UserCredentials;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private RestaurantAgentRepository restAgentRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DeliveryAgentRepository delAgentRepository;

    @Override
    public UserDetails loadUserByUsername(String user) throws UsernameNotFoundException {
        ObjectMapper objectMapper = new ObjectMapper();
        UserCredentials deserializedCredentials = null;
        try {
            deserializedCredentials = objectMapper.readValue(user, UserCredentials.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String email = deserializedCredentials.getEmail();
        String role = deserializedCredentials.getRole();
        if(role.equalsIgnoreCase("SUPER_ADMIN") || role.equalsIgnoreCase("CO_ADMIN") || role.equalsIgnoreCase("ADMIN")) {
            Admin admin = adminRepository.findByAdminEmail(email);
            if (admin == null) {
                throw new UsernameNotFoundException("User Not Found with email: " + email);
            }
            return new User(admin.getAdminEmail(), admin.getAdminPassword(), Arrays.stream(admin.getAdminRole().toString().split(",")).map(SimpleGrantedAuthority::new).toList());
        }

        if(role.equalsIgnoreCase("RESTAURANT_AGENT")) {
            RestaurantAgent restaurantAgent = restAgentRepository.findByRestAgentEmail(email);
            if (restaurantAgent == null) {
                throw new UsernameNotFoundException("User Not Found with email: " + email);
            }
            return new User(restaurantAgent.getRestAgentEmail(), restaurantAgent.getRestAgentPassword(), Arrays.stream(role.toString().split(",")).map(SimpleGrantedAuthority::new).toList());
        }

        if(role.equalsIgnoreCase("DELIVERY_AGENT")) {
            DeliveryAgent deliveryAgent = delAgentRepository.findByDelAgentEmail(email);
            if (deliveryAgent == null) {
                throw new UsernameNotFoundException("User Not Found with email: " + email);
            }
            return new User(deliveryAgent.getDelAgentEmail(), deliveryAgent.getDelAgentPassword(), Arrays.stream(role.toString().split(",")).map(SimpleGrantedAuthority::new).toList());
        }

        if(role.equalsIgnoreCase("CUSTOMER")) {
            Customer customer = customerRepository.findByCustEmail(email);
            if (customer == null) {
                throw new UsernameNotFoundException("User Not Found with email: " + email);
            }
            return new User(customer.getCustEmail(), customer.getCustPassword(), Arrays.stream(role.toString().split(",")).map(SimpleGrantedAuthority::new).toList());
        }
        return null;
    }

}
