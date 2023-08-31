package com.example.food.delivery.ServiceInterface;

import com.example.food.delivery.Request.AdminRequest;
import com.example.food.delivery.Request.LoginRequest;
import com.example.food.delivery.Request.UpdateAdminRequest;
import com.example.food.delivery.Response.BaseResponse;
import org.springframework.http.ResponseEntity;

public interface AdminService {
    ResponseEntity<BaseResponse<?>> createAdmin(AdminRequest adminRequest, String adminEmail);
    ResponseEntity<BaseResponse<?>> loginAdmin(LoginRequest loginRequest);
    ResponseEntity<BaseResponse<?>> logoutAdmin(String adminEmail);
    ResponseEntity<BaseResponse<?>> isAdminLoggedIn(String adminEmail);
    ResponseEntity<BaseResponse<?>> getAllAdmins(int page, String email);
    ResponseEntity<BaseResponse<?>> updateAdmin(UpdateAdminRequest updateAdminRequest);
    ResponseEntity<BaseResponse<?>> deleteAdmin(int adminId, String adminEmail);
    ResponseEntity<BaseResponse<?>> transferRoleAndDeleteSuperAdmin(String currentAdminEmail, String newSuperAdminEmail);
    ResponseEntity<?> isValidAdmin(String adminEmail);
}
