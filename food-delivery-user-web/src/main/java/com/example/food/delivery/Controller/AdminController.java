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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@RequestMapping("/admins")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping(value = "/admin/signup")
    @PreAuthorize("#userRole == 'SUPER_ADMIN' or #userRole == 'CO_ADMIN'")
    public ResponseEntity<BaseResponse<?>> createAdmin(@Valid @RequestBody AdminRequest adminRequest,
                                                       @RequestHeader("userEmail") String userEmail,
                                                       @RequestHeader("userRole") String userRole){
            return adminService.createAdmin(adminRequest, userEmail);
    }

    @PostMapping("/auth/admin/login")
    public ResponseEntity<?> loginAdmin(@Valid @RequestBody LoginRequest loginRequest) {
        return adminService.loginAdmin(loginRequest);
    }

    @PutMapping("/admin/logout")
    @PreAuthorize("#userRole == 'SUPER_ADMIN' or #userRole == 'CO_ADMIN' or #userRole == 'ADMIN'")
    public ResponseEntity<BaseResponse<?>> logoutAdmin(@RequestHeader("userEmail") String userEmail,
                                                       @RequestHeader("userRole") String userRole) {
        return adminService.logoutAdmin(userEmail);
    }

    @GetMapping(value = "/admin/all")
    @PreAuthorize("#userRole == 'SUPER_ADMIN' or #userRole == 'CO_ADMIN'")
    public ResponseEntity<BaseResponse<?>> getAllAdmins(@RequestParam int page,
                                                        @RequestHeader("userEmail") String userEmail,
                                                        @RequestHeader("userRole") String userRole) {
        return adminService.getAllAdmins(page, userEmail);
    }

    @PutMapping("/admin/update")
    @PreAuthorize("#userRole == 'SUPER_ADMIN' or #userRole == 'CO_ADMIN' or #userRole == 'ADMIN'")
    public ResponseEntity<BaseResponse<?>> updateAdmin(@RequestBody UpdateAdminRequest adminRequest,
                                                       @RequestHeader("userEmail") String userEmail,
                                                       @RequestHeader("userRole") String userRole) {
        return adminService.updateAdmin(adminRequest, userEmail);
    }

    @DeleteMapping("/admin/delete")
    @PreAuthorize("#userRole == 'SUPER_ADMIN' or #userRole == 'CO_ADMIN'")
    public ResponseEntity<BaseResponse<?>> deleteAdmin(@RequestParam String adminEmail,
                                                       @RequestHeader("userEmail") String userEmail,
                                                       @RequestHeader("userRole") String userRole) {
        return adminService.deleteAdmin(adminEmail, userEmail);
    }

    @PutMapping("/transfer-super-admin")
    @PreAuthorize("#userRole == 'SUPER_ADMIN'")
    public ResponseEntity<BaseResponse<?>> transferSuperAdminRole(
            @RequestParam String newSuperAdminEmail,
            @RequestHeader("userEmail") String userEmail, @RequestHeader("userRole") String userRole) {
        return adminService.transferRoleAndDeleteSuperAdmin(userEmail, newSuperAdminEmail);
    }
}
