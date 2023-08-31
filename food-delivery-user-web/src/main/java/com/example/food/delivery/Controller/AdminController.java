package com.example.food.delivery.Controller;

import com.example.food.delivery.Admin;
import com.example.food.delivery.AdminServiceImpl;
import com.example.food.delivery.Request.AdminRequest;
import com.example.food.delivery.Request.LoginRequest;
import com.example.food.delivery.Request.UpdateAdminRequest;
import com.example.food.delivery.Response.BaseResponse;
import com.example.food.delivery.ServiceInterface.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admins")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping(value = "/createAdmin")
    public ResponseEntity<BaseResponse<?>> createAdmin(@Valid @RequestBody AdminRequest adminRequest, @RequestParam String requesterEmail){
            return adminService.createAdmin(adminRequest, requesterEmail);
    }

    @PutMapping("/adminLogin")
    public ResponseEntity<BaseResponse<?>> loginAdmin(@Valid @RequestBody LoginRequest loginRequest) {
        return adminService.loginAdmin(loginRequest);
    }

    @PutMapping("/adminLogout")
    public ResponseEntity<BaseResponse<?>> logoutAdmin(@RequestParam String adminEmail) {
        return adminService.logoutAdmin(adminEmail);
    }

    @GetMapping("/isAdminLoggedIn")
    public ResponseEntity<BaseResponse<?>> isAdminLoggedIn(@RequestParam String adminEmail) {
        return adminService.isAdminLoggedIn(adminEmail);
    }

    @GetMapping(value = "/getAdmins")
    public ResponseEntity<BaseResponse<?>> getAllAdmins(@RequestParam int page, String email) {
        return adminService.getAllAdmins(page, email);
    }

    @PutMapping("/updateAdmin")
    public ResponseEntity<BaseResponse<?>> updateAdmin(@Valid @RequestBody UpdateAdminRequest adminRequest) {
        return adminService.updateAdmin(adminRequest);
    }

    @DeleteMapping("/{adminId}")
    public ResponseEntity<BaseResponse<?>> deleteAdmin(@PathVariable int adminId, @RequestParam String requesterEmail) {
        return adminService.deleteAdmin(adminId, requesterEmail);
    }

    @PostMapping("/transfer-super-admin")
    public ResponseEntity<BaseResponse<?>> transferSuperAdminRole(
            @RequestParam String newSuperAdminEmail,
            @RequestParam String currentAdminEmail) {
        return adminService.transferRoleAndDeleteSuperAdmin(currentAdminEmail, newSuperAdminEmail);
    }

    @GetMapping(value = "/isValidAdmin")
    public ResponseEntity<?> isValidAdmin(@RequestParam String adminEmail) {
        return adminService.isValidAdmin(adminEmail);
    }

}
