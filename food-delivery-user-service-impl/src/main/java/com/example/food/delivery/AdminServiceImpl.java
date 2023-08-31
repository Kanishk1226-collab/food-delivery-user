package com.example.food.delivery;

import com.example.food.delivery.Request.AdminRequest;
import com.example.food.delivery.Request.AdminRole;
import com.example.food.delivery.Request.LoginRequest;
import com.example.food.delivery.Request.UpdateAdminRequest;
import com.example.food.delivery.Response.BaseResponse;
import com.example.food.delivery.Response.ResponseStatus;
import com.example.food.delivery.ServiceInterface.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AdminServiceImpl implements UserDetailsService, AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    public BaseResponse<?> response;

    public synchronized ResponseEntity<BaseResponse<?>> createAdmin(AdminRequest adminRequest, String adminEmail) {
        try {
            Admin requesterAdmin = adminRepository.findByAdminEmail(adminEmail);
            if(requesterAdmin == null) {
                throw new UserManagementExceptions.InvalidInputException("Enter Valid Email ID");
            }
            if (adminRequest.getAdminRole() == AdminRole.SUPER_ADMIN) {
                    throw new UserManagementExceptions.UnauthorizedAccessException("SUPER_ADMIN cannot be created!");
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
            addNewAdmin(adminRequest);
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, adminRequest.getAdminRole() + " added successfully");
        } catch (Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public void addNewAdmin(AdminRequest adminRequest) {
        Admin admin = new Admin();
        admin.setAdminName(adminRequest.getAdminName());
        admin.setAdminEmail(adminRequest.getAdminEmail());
        admin.setAdminRole(adminRequest.getAdminRole());
        admin.setAdminPassword(adminRequest.getAdminName().replaceAll("\\s", "") + "@fda.com");
        admin.setIsLoggedIn(false);
        adminRepository.save(admin);
    }

    public synchronized ResponseEntity<BaseResponse<?>> loginAdmin(LoginRequest loginRequest) {
        try {
            String email = loginRequest.getEmail();
            String password = loginRequest.getPassword();
            Admin admin = adminRepository.findByAdminEmail(email);
            if (admin == null) {
                throw new UserManagementExceptions.UserNotFoundException("No Admin found for ID " + email);
            }
            if (admin.getIsLoggedIn()) {
                throw new UserManagementExceptions.LoginException("User already logged in");
            }
            if (!admin.getAdminPassword().equals(password)) {
                throw new UserManagementExceptions.LoginException("Invalid Password");
            }
            admin.setIsLoggedIn(true);
            adminRepository.save(admin);
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, "Login Successful");
        } catch(Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public synchronized ResponseEntity<BaseResponse<?>> logoutAdmin(String adminEmail) {
        try {
            isValidEmail(adminEmail);
            Admin admin = adminRepository.findByAdminEmail(adminEmail);
            if (admin == null) {
                throw new UserManagementExceptions.UserNotFoundException("No Admin found for ID " + adminEmail);
            }
            if (!admin.getIsLoggedIn()) {
                throw new UserManagementExceptions.LoginException("Admin not logged in");
            }
            admin.setIsLoggedIn(false);
            adminRepository.save(admin);
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, "Logout Successful");
        } catch(Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public synchronized ResponseEntity<BaseResponse<?>> isAdminLoggedIn(String adminEmail) {
        try {
            isValidEmail(adminEmail);
            Admin admin = adminRepository.findByAdminEmail(adminEmail);
            if (admin == null) {
                throw new UserManagementExceptions.UserNotFoundException("No Admin found for ID " + adminEmail);
            }
            if (!admin.getIsLoggedIn()) {
                throw new UserManagementExceptions.LoginException("Admin not logged in");
            }
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, "Admin has Logged In");
        } catch(Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public synchronized ResponseEntity<BaseResponse<?>> getAllAdmins(int page, String email) {
        try {
            isSuperOrCoAdmin(email);
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

    public synchronized ResponseEntity<BaseResponse<?>> updateAdmin(UpdateAdminRequest updateAdminRequest) {
        try {
            Admin admin = adminRepository.findById(updateAdminRequest.getAdminId()).orElse(null);
            if (admin == null) {
                throw new UserManagementExceptions.UserNotFoundException("User not found with ID " + updateAdminRequest.getAdminId());
            }
            if (updateAdminRequest.getAdminName() != null) {
                if (updateAdminRequest.getAdminName().isEmpty()) {
                    throw new UserManagementExceptions.InvalidInputException("User Name should not be empty");
                }
                admin.setAdminName(updateAdminRequest.getAdminName());
            }
            if (updateAdminRequest.getAdminEmail() != null) {
                if (updateAdminRequest.getAdminEmail().isEmpty()) {
                    throw new UserManagementExceptions.InvalidInputException("User Email should not be empty");
                }
                isValidEmail(updateAdminRequest.getAdminEmail());
                if (adminRepository.existsByAdminEmail(updateAdminRequest.getAdminEmail())) {
                    throw new UserManagementExceptions.UserAlreadyExistsException("User with this email already exists");
                }
                admin.setAdminEmail(updateAdminRequest.getAdminEmail());
            }
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, adminRepository.save(admin));
        } catch (Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public synchronized ResponseEntity<BaseResponse<?>> deleteAdmin(int adminId, String adminEmail) {
        try {
            Admin requesterAdmin = adminRepository.findByAdminEmail(adminEmail);
            if(requesterAdmin == null) {
                throw new UserManagementExceptions.InvalidInputException("User not found");
            }
            if(requesterAdmin.getAdminRole() == AdminRole.ADMIN) {
                throw new UserManagementExceptions.UnauthorizedAccessException("Only SUPER_ADMIN and CO_ADMIN have access to delete.");
            }
            Admin admin = adminRepository.findById(adminId).orElse(null);
            if (admin == null) {
                throw new UserManagementExceptions.UserNotFoundException("User not found with ID " + adminId);
            }
            if(admin.getAdminRole() == AdminRole.SUPER_ADMIN) {
                throw new UserManagementExceptions.UnauthorizedAccessException("SUPER_ADMIN cannot be deleted");
            }
            if(admin.getAdminRole() == AdminRole.CO_ADMIN && requesterAdmin.getAdminRole() != AdminRole.SUPER_ADMIN) {
                throw new UserManagementExceptions.UnauthorizedAccessException("Only SUPER_ADMIN can be delete CO-ADMIN");
            }
            adminRepository.deleteById(adminId);
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

    public synchronized ResponseEntity<?> isValidAdmin(String adminEmail) {
        try {
            isValidEmail(adminEmail);
            Admin admin = adminRepository.findByAdminEmail(adminEmail);
            if(admin == null) {
                throw new UserManagementExceptions.UserNotFoundException("No user found");
            }
            response = new BaseResponse<>(true, ResponseStatus.SUCCESS.getStatus(), null, "Valid Admin");
        } catch(Exception e) {
            response = new BaseResponse<>(false, ResponseStatus.ERROR.getStatus(), e.getMessage(), null);
        }
        return ResponseEntity.ok(response);
    }

    public synchronized void isSuperOrCoAdmin(String adminEmail) {
            isValidEmail(adminEmail);
            Admin admin = adminRepository.findByAdminEmail(adminEmail);
            if(admin == null) {
                throw new UserManagementExceptions.UserNotFoundException("No user found");
            }
            if(admin.getAdminRole().equals(AdminRole.ADMIN)) {
                throw new UserManagementExceptions.UnauthorizedAccessException("Only SUPER ADMIN and CO ADMIN can view Admins");
            }
            if(!admin.getIsLoggedIn()) {
                throw new UserManagementExceptions.UnauthorizedAccessException("Admin Not Logged In");
            }
        }

    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByAdminName(username);
                if(admin == null) {
                    throw new UsernameNotFoundException("User Not Found with username: " + username);
                }
        return UserDetailsImpl.build(admin);
    }
    public boolean isValidInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void isValidEmail(String email) {
        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        if(!matcher.matches()) {
            throw new UserManagementExceptions.InvalidInputException("Enter valid Email");
        }

    }


}
