package com.example.food.delivery.Controller;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {
    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CO_ADMIN') or hasRole('SUPER_ADMIN')")
    public String adminAccess() {
        return "Admin Content.";
    }

    @GetMapping("/superAdmin")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('CO_ADMIN')")
    public String SuperAdminAccess() {
        return "Super Admin and Co Admin Board.";
    }

    @GetMapping("/restAgent")
    @PreAuthorize("hasRole('REST_AGENT')")
    public String restAgentAccess() {
        return "Restaurant Agent Board.";
    }

    @GetMapping("/delAgent")
    @PreAuthorize("hasRole('DEL_AGENT')")
    public String delAgentAccess() {
        return "Delivery Agent Board.";
    }

    @GetMapping("/customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String customerAccess() {
        return "Customer Board.";
    }
}