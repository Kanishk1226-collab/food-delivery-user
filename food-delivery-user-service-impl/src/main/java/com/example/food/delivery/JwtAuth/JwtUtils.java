package com.example.food.delivery.JwtAuth;
import java.io.Serial;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.example.food.delivery.Response.JwtResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    @Serial
    private static final long serialVersionUID = -2550185165626007488L;


    @Value("${foodservice.app.jwtSecret}")
    private String jwtSecret;

    @Value("${foodservice.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public String generateJwtToken(UserDetails userDetails) {

//        UserDetails userPrincipal = userDetails.getAuthorities();
        Map<String, Object> claims = new HashMap<>();
        String email = userDetails.getUsername();
        String uuid = generateUuid();
        userDetails.getAuthorities().forEach(user -> claims.put("role", user.getAuthority()));
        String jwtToken = Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
        redisTemplate.opsForValue().set(uuid, jwtToken);
        return uuid;
    }

    public String generateUuid() {
        return UUID.randomUUID().toString();
    }

    public String getTokenByUuid(String uuid) {
        return redisTemplate.opsForValue().get(uuid);
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }


    public String getUserEmailFromJwtToken(String token) {
      return  Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String extractRoleFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(key()).parseClaimsJws(token).getBody();
        return (String) claims.get("role");
    }

    public Claims getUserRoleFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(key())
                .parseClaimsJws(token).getBody();
    }

    public String getSubjectFromJwtToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            e.printStackTrace();
            return null;
        }
    }



    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}