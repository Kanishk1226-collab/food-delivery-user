package com.example.food.delivery.ServiceInterface;

import com.example.food.delivery.Request.AdminRequest;
import com.example.food.delivery.Request.LoginRequest;
import com.example.food.delivery.Request.UpdateAdminRequest;
import com.example.food.delivery.Response.BaseResponse;
import org.springframework.http.ResponseEntity;

public interface AdminService {
    ResponseEntity<BaseResponse<?>> createAdmin(AdminRequest adminRequest, String adminEmail);
    ResponseEntity<?> loginAdmin(LoginRequest loginRequest);
    ResponseEntity<BaseResponse<?>> logoutAdmin(String adminEmail);
    ResponseEntity<BaseResponse<?>> updateAdmin(UpdateAdminRequest updateAdminRequest, String adminEmail);
    ResponseEntity<BaseResponse<?>> getAllAdmins(int page, String email);
    ResponseEntity<BaseResponse<?>> deleteAdmin(String adminEmail, String userEmail);
    ResponseEntity<BaseResponse<?>> transferRoleAndDeleteSuperAdmin(String currentAdminEmail, String newSuperAdminEmail);
}
