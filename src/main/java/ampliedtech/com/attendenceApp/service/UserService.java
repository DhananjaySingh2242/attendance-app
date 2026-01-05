package ampliedtech.com.attendenceApp.service;

import ampliedtech.com.attendenceApp.dto.AuthResponse;
import ampliedtech.com.attendenceApp.dto.LoginRequest;
import ampliedtech.com.attendenceApp.dto.RegisterRequest;
import ampliedtech.com.attendenceApp.dto.UpdateRequest;
import ampliedtech.com.attendenceApp.dto.UserResponse;
import ampliedtech.com.attendenceApp.entity.User;
import org.springframework.data.domain.Page;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    String registerUser(RegisterRequest req);

    AuthResponse loginUser(LoginRequest req);

    UserResponse getCurrentUser();

    String deleteUser(Long id);

    User updateUser(Long id, UpdateRequest updateData);

    Page<User> getAllUser(int page,int size);
}
