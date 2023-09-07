package com.example.food.delivery.JwtAuth;

import java.io.IOException;
import java.util.function.Function;

import com.example.food.delivery.AdminServiceImpl;
import com.example.food.delivery.Response.UserCredentials;
import com.example.food.delivery.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AdminServiceImpl adminService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

//    @Autowired
//    private RedisTemplate<String, String> redisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String uuid = parseJwt(request);
//            System.out.println("uuid" + request);
            String jwt = jwtUtils.getTokenByUuid(uuid);
            System.out.println("uuid" + uuid);

            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String email = jwtUtils.getUserEmailFromJwtToken(jwt);

                Claims claim = jwtUtils.getUserRoleFromJwtToken(jwt);
                String role = jwtUtils.extractRoleFromToken(jwt);
System.out.println("Role" + role);
                UserCredentials userCredentials = new UserCredentials();
                userCredentials.setEmail(email);
                userCredentials.setRole(role);

                ObjectMapper objectMapper = new ObjectMapper();
                String serializedCredentials = objectMapper.writeValueAsString(userCredentials);

                UserDetails userDetails = userDetailsService.loadUserByUsername(serializedCredentials);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());
                System.out.println("Is user authenticated? " + authentication.isAuthenticated());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }
        filterChain.doFilter(request, response);
    }


    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}