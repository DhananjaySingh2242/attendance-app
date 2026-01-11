package ampliedtech.com.attendenceApp.service;

import ampliedtech.com.attendenceApp.entity.Role;
import ampliedtech.com.attendenceApp.entity.User;
import ampliedtech.com.attendenceApp.mapper.UserResMapper;
import ampliedtech.com.attendenceApp.repository.UserRepository;
import ampliedtech.com.attendenceApp.requestDto.LoginRequest;
import ampliedtech.com.attendenceApp.requestDto.RegisterRequest;
import ampliedtech.com.attendenceApp.requestDto.UpdateRequest;
import ampliedtech.com.attendenceApp.responseDto.AuthResponse;
import ampliedtech.com.attendenceApp.responseDto.DeleteResponse;
import ampliedtech.com.attendenceApp.responseDto.RegisterResponse;
import ampliedtech.com.attendenceApp.responseDto.UpdateResponse;
import ampliedtech.com.attendenceApp.responseDto.UserResponse;
import ampliedtech.com.attendenceApp.configuration.JwtUtil;

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

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(@Lazy UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
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
        if (userRepository.findByEmail(request.email.trim()).isPresent()) {
            log.warn("Registration failed. Email already exists: {}",
                    request.getEmail());
            throw new RuntimeException("Email already registered");
        }
        User user = new User();
        user.setEmail(request.email.trim());
        user.setName(request.name);
        user.setRole(Role.ROLE_USER);
        user.setPassword(passwordEncoder.encode(request.password));

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
        String token = jwtUtil.generateToken(user.getEmail());
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
    public Page<User> getAllUser(int page, int size) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
            return userRepository.findAll(pageable);
        }
}

