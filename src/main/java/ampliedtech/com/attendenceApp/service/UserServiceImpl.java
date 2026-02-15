package ampliedtech.com.attendenceApp.service;

import ampliedtech.com.attendenceApp.document.AttendanceDocument;
import ampliedtech.com.attendenceApp.entity.User;
import ampliedtech.com.attendenceApp.jpaRepo.UserRepository;
import ampliedtech.com.attendenceApp.mapper.GetAttendanceMapper;
import ampliedtech.com.attendenceApp.mapper.UserResMapper;
import ampliedtech.com.attendenceApp.mongoRepo.AttendanceRepo;
import ampliedtech.com.attendenceApp.requestDto.UpdateRequest;
import ampliedtech.com.attendenceApp.responseDto.AllAtendanceResponse;
import ampliedtech.com.attendenceApp.responseDto.AttendanceResponse;
import ampliedtech.com.attendenceApp.responseDto.DeleteResponse;
import ampliedtech.com.attendenceApp.responseDto.UpdateResponse;
import ampliedtech.com.attendenceApp.responseDto.UserResponse;
import ampliedtech.com.attendenceApp.utils.KeyclaokRoleUtil;
import ampliedtech.com.attendenceApp.configuration.KeycloakAdminConfig;
import org.springframework.security.core.Authentication;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import ampliedtech.com.attendenceApp.mapper.GetUserMapper;
import org.keycloak.admin.client.Keycloak;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final AttendanceRepo attendanceRepo;
   private final KeyclaokRoleUtil roleUtil;
   private final Keycloak keycloak;
   // private final JwtUtil jwtUtil;

    public UserServiceImpl(@Lazy UserRepository userRepository, 
            AttendanceRepo attendanceRepo,KeyclaokRoleUtil roleUtil,
        Keycloak keycloak) {
        this.userRepository = userRepository;
      //  this.jwtUtil = jwtUtil;
        this.attendanceRepo = attendanceRepo;
        this.roleUtil = roleUtil;
        this.keycloak = keycloak;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .build();
    }
    

    @Override
    public DeleteResponse deleteUser(Long id){
        User user = userRepository.findById(id)
        .orElseThrow(()->
    new RuntimeException("User with Id" + id + "does not exist"));
     try {
            keycloak.realm("keycloak-demo")
                    .users()
                    .get(user.getKeycloakId())
                    .remove();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete user from Keycloak", e);
        }
        userRepository.delete(user);
        return new DeleteResponse(id, 
            "User deleted from keycloak and db", 
        LocalDateTime.now());
    }

    @Override
    public UpdateResponse updateUser(Long id, UpdateRequest updateData) {
        log.warn("Updating user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        if (updateData.getName() != null && !updateData.getName().isEmpty()) {
            user.setName(updateData.getName());
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
public void ensureUserExists(Jwt jwt) {

    String keycloakId = jwt.getSubject();
    String email = jwt.getClaimAsString("email");

    userRepository.findByKeycloakId(keycloakId)
        .orElseGet(() -> {
            User user = new User();
            user.setKeycloakId(keycloakId);
            user.setEmail(email);
            user.setName(jwt.getClaimAsString("name"));
            return userRepository.save(user);
        });
}

@Override
public UserResponse getCurrentUser() {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (!(auth.getPrincipal() instanceof Jwt jwt)) {
        throw new RuntimeException("Invalid authentication");
    }

    // 🔑 1️⃣ Extract from token
    String keycloakUserId = jwt.getSubject();
    String email = jwt.getClaimAsString("email");

    // 👤 2️⃣ Find or create user
    User user = userRepository.findByKeycloakId(keycloakUserId)
            .orElseGet(() -> {
                User newUser = new User();
                newUser.setKeycloakId(keycloakUserId);   // ✅ INSERTED HERE
                newUser.setEmail(email);
                newUser.setName(jwt.getClaimAsString("name"));
                return userRepository.save(newUser);
            });

    // 📤 3️⃣ Return response
    return new UserResponse(
            user.getKeycloakId(),
            user.getEmail(),
            user.getName(),
            roleUtil.extractRoles(jwt)
    );
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