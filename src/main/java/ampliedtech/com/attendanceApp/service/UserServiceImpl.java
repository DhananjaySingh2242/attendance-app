package ampliedtech.com.attendanceApp.service;

import ampliedtech.com.attendanceApp.document.AttendanceDocument;
import ampliedtech.com.attendanceApp.entity.User;
import ampliedtech.com.attendanceApp.exception.KeycloakIntegrationException;
import ampliedtech.com.attendanceApp.exception.UnauthorizedException;
import ampliedtech.com.attendanceApp.exception.UserNotFoundException;
import ampliedtech.com.attendanceApp.jpaRepo.UserRepository;
import ampliedtech.com.attendanceApp.mapper.GetAttendanceMapper;
import ampliedtech.com.attendanceApp.mapper.GetUserMapper;
import ampliedtech.com.attendanceApp.mapper.UserResMapper;
import ampliedtech.com.attendanceApp.mongoRepo.AttendanceRepo;
import ampliedtech.com.attendanceApp.requestDto.UpdateRequest;
import ampliedtech.com.attendanceApp.responseDto.AllAtendanceResponse;
import ampliedtech.com.attendanceApp.responseDto.AttendanceResponse;
import ampliedtech.com.attendanceApp.responseDto.DeleteResponse;
import ampliedtech.com.attendanceApp.responseDto.UpdateResponse;
import ampliedtech.com.attendanceApp.responseDto.UserResponse;
import ampliedtech.com.attendanceApp.entity.Role;
import ampliedtech.com.attendanceApp.utils.KeyclaokRoleUtil;
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
import ampliedtech.com.attendanceApp.mapper.GetUserMapper;
import org.springframework.beans.factory.annotation.Value;
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
    private final String keycloakRealm;

    public UserServiceImpl(@Lazy UserRepository userRepository,
            AttendanceRepo attendanceRepo, KeyclaokRoleUtil roleUtil,
            Keycloak keycloak,
            @Value("${keycloak.realm:keycloak-demo}") String keycloakRealm) {
        this.userRepository = userRepository;
        this.attendanceRepo = attendanceRepo;
        this.roleUtil = roleUtil;
        this.keycloak = keycloak;
        this.keycloakRealm = keycloakRealm;
    }

    @Override
    public User findOrCreateUserByJwt(Jwt jwt) {
        String keycloakId = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        String name = jwt.getClaimAsString("name");
        List<String> roles = roleUtil.extractRoles(jwt);
        Role role = roles.stream().anyMatch(r -> "ADMIN".equalsIgnoreCase(r)) ? Role.ROLE_ADMIN : Role.ROLE_USER;

        return userRepository.findByKeycloakId(keycloakId)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setKeycloakId(keycloakId);
                    newUser.setEmail(email);
                    newUser.setName(name);
                    newUser.setRole(role);
                    return userRepository.save(newUser);
                });
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
    public DeleteResponse deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        try {
            keycloak.realm(keycloakRealm)
                    .users()
                    .get(user.getKeycloakId())
                    .remove();
        } catch (Exception e) {
            throw new KeycloakIntegrationException("Failed to delete user from Keycloak", e);
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
                .orElseThrow(() -> new UserNotFoundException(id));
        if (updateData.getName() != null && !updateData.getName().isEmpty()) {
            user.setName(updateData.getName());
        }
        User savedUser = userRepository.save(user);
        return UserResMapper.toDto(savedUser);
    }

    @Override
    public Page<UserResponse> getAllUser(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
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
            return List.of();
        }
        return attendanceList.stream()
                .map(attendance -> new AllAtendanceResponse(
                        attendance.getDate(),
                        attendance.getStatus()))
                .toList();
    }
    @Override
    public void ensureUserExists(Jwt jwt) {
        findOrCreateUserByJwt(jwt);
    }

    @Override
    public UserResponse getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth.getPrincipal() instanceof Jwt jwt)) {
            throw new UnauthorizedException("Invalid authentication");
        }
        User user = findOrCreateUserByJwt(jwt);
    return new UserResponse(
            user.getId(),
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
                .findByEmailContainingIgnoreCaseOrNameContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(GetUserMapper::toDto)
                .toList();
    }

    @Override
    public List<AttendanceResponse> getUserAttendance(LocalDate date) {
        return attendanceRepo.findByDate(date).stream()
                .map(GetAttendanceMapper::toDto)
                .toList();
    }
}