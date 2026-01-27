package ampliedtech.com.attendenceApp.service;

import ampliedtech.com.attendenceApp.entity.User;
import ampliedtech.com.attendenceApp.requestDto.LoginRequest;
import ampliedtech.com.attendenceApp.requestDto.RegisterRequest;
import ampliedtech.com.attendenceApp.requestDto.UpdateRequest;
import ampliedtech.com.attendenceApp.responseDto.AllAtendanceResponse;
import ampliedtech.com.attendenceApp.responseDto.AttendanceResponse;
import ampliedtech.com.attendenceApp.responseDto.AuthResponse;
import ampliedtech.com.attendenceApp.responseDto.DeleteResponse;
import ampliedtech.com.attendenceApp.responseDto.RegisterResponse;
import ampliedtech.com.attendenceApp.responseDto.UpdateResponse;
import ampliedtech.com.attendenceApp.responseDto.UserResponse;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    RegisterResponse registerUser(RegisterRequest req);

    AuthResponse loginUser(LoginRequest req);

    UserResponse getCurrentUser();

    DeleteResponse deleteUser(Long id);

    UpdateResponse updateUser(Long id, UpdateRequest updateData);

    Page<UserResponse> getAllUser(int page, int size);

    Page<AttendanceResponse> getAttendance(int page, int size);

    List<AllAtendanceResponse> currentUserAllAttendance();

    List<UserResponse> searchUsers(String keyword);

    List<AttendanceResponse> getUserAttendance(LocalDate date);
}