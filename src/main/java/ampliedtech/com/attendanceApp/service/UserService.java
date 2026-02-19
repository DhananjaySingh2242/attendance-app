package ampliedtech.com.attendanceApp.service;

import ampliedtech.com.attendanceApp.entity.User;
import ampliedtech.com.attendanceApp.requestDto.UpdateRequest;
import ampliedtech.com.attendanceApp.responseDto.AllAtendanceResponse;
import ampliedtech.com.attendanceApp.responseDto.AttendanceResponse;
import ampliedtech.com.attendanceApp.responseDto.DeleteResponse;
import ampliedtech.com.attendanceApp.responseDto.UpdateResponse;
import ampliedtech.com.attendanceApp.responseDto.UserResponse;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;

public interface UserService extends UserDetailsService {

    /** Finds or creates a user from JWT and returns the entity. Used by attendance flow. */
    User findOrCreateUserByJwt(Jwt jwt);

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