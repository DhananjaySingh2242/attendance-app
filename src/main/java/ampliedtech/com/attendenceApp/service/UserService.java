package ampliedtech.com.attendenceApp.service;

import ampliedtech.com.attendenceApp.requestDto.RegisterRequest;
import ampliedtech.com.attendenceApp.requestDto.UpdateRequest;
import ampliedtech.com.attendenceApp.responseDto.AllAtendanceResponse;
import ampliedtech.com.attendenceApp.responseDto.AttendanceResponse;
import ampliedtech.com.attendenceApp.responseDto.DeleteResponse;
import ampliedtech.com.attendenceApp.responseDto.RegisterResponse;
import ampliedtech.com.attendenceApp.responseDto.UpdateResponse;
import ampliedtech.com.attendenceApp.responseDto.UserResponse;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;

public interface UserService extends UserDetailsService {

    UserResponse getCurrentUser();

    DeleteResponse deleteUser(Long id);

    UpdateResponse updateUser(Long id, UpdateRequest updateData);

    Page<UserResponse> getAllUser(int page, int size);

    Page<AttendanceResponse> getAttendance(int page, int size);

    List<AllAtendanceResponse> currentUserAllAttendance();

    List<UserResponse> searchUsers(String keyword);

    List<AttendanceResponse> getUserAttendance(LocalDate date);
    void ensureUserExists(Jwt jwt);
}