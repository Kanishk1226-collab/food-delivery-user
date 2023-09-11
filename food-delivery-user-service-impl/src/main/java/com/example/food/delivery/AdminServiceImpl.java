package com.example.food.delivery;

import com.example.food.delivery.JwtAuth.JwtUtils;
import com.example.food.delivery.Request.AdminRequest;
import com.example.food.delivery.Request.AdminRole;
import com.example.food.delivery.Request.LoginRequest;
import com.example.food.delivery.Request.UpdateAdminRequest;
import com.example.food.delivery.Response.BaseResponse;
import com.example.food.delivery.Response.JwtResponse;
import com.example.food.delivery.Response.ResponseStatus;
import com.example.food.delivery.Response.UserCredentials;
import com.example.food.delivery.ServiceInterface.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    public BaseResponse<?> response;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private JwtUtils jwtUtil;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailServiceImpl;

    public synchronized ResponseEntity<BaseResponse<?>> createAdmin(AdminRequest adminRequest, String adminEmail) {
        try {
            Admin requesterAdmin = adminRepository.findByAdminEmail(adminEmail);
            if(requesterAdmin == null) {
                throw new UserManagementExceptions.InvalidInputException("Enter Valid Email ID");
            }
            if (adminRequest.getAdminRole() == AdminRole.CO_ADMIN && requesterAdmin.getAdminRole() != AdminRole.SUPER_ADMIN) {
                throw new UserManagementExceptions.UnauthorizedAccessException("CO_ADMIN can be created only by SUPER_ADMIN!");
            }
            if (adminRequest.getAdminRole() == AdminRole.ADMIN && requesterAdmin.getAdminRole() == AdminRole.ADMIN) {
                throw new UserManagementExceptions.UnauthorizedAccessException("ADMIN can be created only by SUPER_ADMIN or CO_ADMIN!");
            }
            if (adminRepository.existsByAdminEmail(adminRequest.getAdminEmail())) {
                throw new UserManagementExceptions.UserAlreadyExistsException("User with this email already exists");
            }
            if(adminRepository.existsByPhoneNo(adminRequest.getPhoneNo())) {
                throw new UserManagementExceptions.UserAlreadyExistsException("User with this phone number already exists");
            }
            addNewAdmin(adminRequest);
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, adminRequest.getAdminRole() + " added successfully");
        } catch (Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public void addNewAdmin(AdminRequest adminRequest) {
        Admin admin = new Admin();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        admin.setAdminName(adminRequest.getAdminName());
        admin.setAdminEmail(adminRequest.getAdminEmail());
        admin.setAdminRole(adminRequest.getAdminRole());
        admin.setAdminPassword(bCryptPasswordEncoder.encode(adminRequest.getAdminName().replaceAll("\\s", "") + "@fda.com"));
        admin.setPhoneNo(adminRequest.getPhoneNo());
        adminRepository.save(admin);
    }

    public synchronized ResponseEntity<?> loginAdmin(LoginRequest loginRequest) {
        try {
            String email = loginRequest.getEmail();
            Admin admin = adminRepository.findByAdminEmail(email);
            if (admin == null) {
                throw new UserManagementExceptions.UserNotFoundException("No Admin found for ID " + email);
            }
            UserCredentials userCredentials = new UserCredentials();
            userCredentials.setEmail(email);
            userCredentials.setRole(admin.getAdminRole().toString());
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(mapper.writeValueAsString(userCredentials), loginRequest.getPassword()));

            ObjectMapper objectMapper = new ObjectMapper();
            String serializedCredentials = objectMapper.writeValueAsString(userCredentials);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = userDetailServiceImpl.loadUserByUsername(serializedCredentials);
            String jwt = jwtUtil.generateJwtToken(userDetails);

            JwtResponse jwtResponse = JwtResponse.builder()
                    .token(jwt)
                    .username(admin.getAdminName())
                    .email(admin.getAdminEmail())
                    .role(admin.getAdminRole().toString()).build();
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, jwtResponse);
        } catch(Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public synchronized ResponseEntity<BaseResponse<?>> logoutAdmin(String adminEmail) {
        try {
            Admin admin = adminRepository.findByAdminEmail(adminEmail);
            adminRepository.save(admin);
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, "Logout Successful");
        } catch(Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public synchronized ResponseEntity<BaseResponse<?>> getAllAdmins(int page, String email) {
        try {
            int pageSize = 10;
            Sort sortById = Sort.by(Sort.Direction.DESC, "adminId");
            PageRequest pageRequest = PageRequest.of(page, pageSize, sortById);
            Page<Admin> admin = adminRepository.findAll(pageRequest);
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, admin.getContent());
        } catch (Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public synchronized ResponseEntity<BaseResponse<?>> updateAdmin(UpdateAdminRequest updateAdminRequest, String adminEmail) {
        try {
            Admin admin = adminRepository.findByAdminEmail(adminEmail);
            if (updateAdminRequest.getAdminName() != null) {
                if (updateAdminRequest.getAdminName().trim().isEmpty()) {
                    throw new UserManagementExceptions.InvalidInputException("User Name should not be empty");
                }
                admin.setAdminName(updateAdminRequest.getAdminName());
            }
            if (updateAdminRequest.getPhoneNo() != null) {
                if (!Pattern.matches("\\d{10}", updateAdminRequest.getPhoneNo())) {
                    throw new UserManagementExceptions.InvalidInputException("Phone number should contain only number and it should be 10 digits");
                }
                if (adminRepository.existsByPhoneNo(updateAdminRequest.getPhoneNo())) {
                    throw new UserManagementExceptions.UserAlreadyExistsException("User with this email already exists");
                }
                admin.setAdminEmail(updateAdminRequest.getPhoneNo());
            }
            adminRepository.save(admin);
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, "Admin detail saved successfully!");
        } catch (Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public synchronized ResponseEntity<BaseResponse<?>> deleteAdmin(String adminEmail, String userEmail) {
        try {
            Admin requesterAdmin = adminRepository.findByAdminEmail(userEmail);
            Admin admin = adminRepository.findByAdminEmail(adminEmail);
            if (admin == null) {
                throw new UserManagementExceptions.UserNotFoundException("User not found with ID " + adminEmail);
            }
            if(admin.getAdminRole() == AdminRole.SUPER_ADMIN) {
                throw new UserManagementExceptions.UnauthorizedAccessException("SUPER_ADMIN cannot be deleted");
            }
            if(admin.getAdminRole() == AdminRole.CO_ADMIN && requesterAdmin.getAdminRole() != AdminRole.SUPER_ADMIN) {
                throw new UserManagementExceptions.UnauthorizedAccessException("Only SUPER_ADMIN can be delete CO-ADMIN");
            }
            adminRepository.deleteById(admin.getAdminId());
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, "User removed Successfully");
        } catch (Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<BaseResponse<?>> transferRoleAndDeleteSuperAdmin(String currentAdminEmail, String newSuperAdminEmail) {
        try {
            Admin requesterAdmin = adminRepository.findByAdminEmail(currentAdminEmail);
            if (requesterAdmin == null) {
                throw new UserManagementExceptions.InvalidInputException("SUPER_ADMIN User not found");
            }
            Admin newAdmin = adminRepository.findByAdminEmail(newSuperAdminEmail);
            if(newAdmin == null) {
                throw new UserManagementExceptions.UserNotFoundException("CO_ADMIN User not found");
            }
            if (requesterAdmin.getAdminRole() == AdminRole.SUPER_ADMIN) {
                Admin newSuperAdmin = adminRepository.findByAdminEmail(newSuperAdminEmail);
                newSuperAdmin.setAdminRole(AdminRole.SUPER_ADMIN);
                adminRepository.save(newSuperAdmin);
                adminRepository.deleteByAdminEmail(currentAdminEmail);
                response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, "Role Transferred Successfully");
            } else {
                throw new UserManagementExceptions.UnauthorizedAccessException("Only SUPER_ADMIN can transfer it's role");
            }
        } catch (Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

}
