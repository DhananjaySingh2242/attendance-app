package ampliedtech.com.attendenceApp.service;

import ampliedtech.com.attendenceApp.document.AttendanceDocument;
import ampliedtech.com.attendenceApp.entity.Role;
import ampliedtech.com.attendenceApp.entity.User;
import ampliedtech.com.attendenceApp.mapper.GetAttendanceMapper;
import ampliedtech.com.attendenceApp.mapper.GetUserMapper;
import ampliedtech.com.attendenceApp.mapper.UserResMapper;
import ampliedtech.com.attendenceApp.repository.AttendanceRepo;
import ampliedtech.com.attendenceApp.repository.UserRepository;
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
import ampliedtech.com.attendenceApp.security.JwtUtil;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AttendanceRepo attendanceRepo;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(@Lazy UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
            AttendanceRepo attendanceRepo) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.attendanceRepo = attendanceRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name().replace("ROLE_", ""))
                .build();
    }

    @Override
    public RegisterResponse registerUser(RegisterRequest request) {
        log.info("Attempting to register user with email: {}", request.getEmail());
        if (userRepository.findByEmail(request.getEmail().trim()).isPresent()) {
            log.warn("Registration failed. Email already exists: {}",
                    request.getEmail());
            throw new RuntimeException("Email already registered");
        }
        User user = new User();
        user.setEmail(request.getEmail().trim());
        user.setName(request.getName());
        user.setRole(Role.ROLE_USER);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        try {
            User savedUser = userRepository.save(user);
            log.info("User registered successfully with email: {}",
                    request.getEmail());

            RegisterResponse response = new RegisterResponse();
            response.setUserId(savedUser.getId());
            response.setMessage("User registered successfully");
            response.setTimestamp(LocalDateTime.now());
            return response;
        } catch (Exception ex) {
            log.error("Error while registering user with email: {}",
                    request.getEmail(), ex);
            throw ex;
        }
    }

    @Override
    public AuthResponse loginUser(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Login failed. User not found: {}",
                            request.getEmail());
                    return new RuntimeException("Invalid credentials");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        String token = jwtUtil.generateToken(user);
        log.info("Login successful for email: {}", request.getEmail());
        return new AuthResponse(token);
    }

    @Override
    public UserResponse getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User Not found"));
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole());
    }

    @Override
    public DeleteResponse deleteUser(Long id) {
        log.warn("Deleting user with ID: {}", id);
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Error: User with ID " + id + " does not exist.");
        }
        userRepository.deleteById(id);
        return new DeleteResponse(id, "User Deleted", LocalDateTime.now());
    }

    @Override
    public UpdateResponse updateUser(Long id, UpdateRequest updateData) {
        log.warn("Updating user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        if (updateData.getName() != null && !updateData.getName().isEmpty()) {
            user.setName(updateData.getName());
        }

        if (updateData.getPassword() != null && !updateData.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateData.getPassword()));
        }
        User savedUser = userRepository.save(user);
        return UserResMapper.toDto(savedUser);
    }

    @Override
    public Page<UserResponse> getAllUser(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<User> userPage = userRepository.findAll(pageable);
        return userPage.map(GetUserMapper::toDto);
    }

    @Override
    public Page<AttendanceResponse> getAttendance(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<AttendanceDocument> attendancePage = attendanceRepo.findAll(pageable);
        return attendancePage.map(GetAttendanceMapper::toDto);
    }

    @Override
    public List<AllAtendanceResponse> currentUserAllAttendance() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        List<AttendanceDocument> attendanceList = attendanceRepo.findAllByEmailOrderByDateDesc(email);

        if (attendanceList.isEmpty()) {
            throw new RuntimeException("No attendance records found");
        }
        return attendanceList.stream()
                .map(attendance -> new AllAtendanceResponse(
                        attendance.getDate(),
                        attendance.getStatus()))
                .toList();
    }

    @Override
    public List<UserResponse> searchUsers(String keyword) {
        keyword = keyword.trim();
        return userRepository
                .findByEmailContainingIgnoreCaseOrNameContainingIgnoreCase(
                        keyword, keyword);
    }
    @Override
    public List<AttendanceResponse> getUserAttendance(LocalDate date){
        return attendanceRepo.findByDate(date);
    }
}