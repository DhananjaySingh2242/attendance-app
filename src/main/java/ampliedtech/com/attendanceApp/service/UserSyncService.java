package ampliedtech.com.attendanceApp.service;

import java.util.List;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import ampliedtech.com.attendanceApp.entity.Role;
import ampliedtech.com.attendanceApp.entity.User;
import ampliedtech.com.attendanceApp.jpaRepo.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class UserSyncService {

    private final UserRepository userRepository;

    public UserSyncService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void syncUser(Jwt jwt) {

        String keycloakId = jwt.getSubject();
        String email = jwt.getClaimAsString("email");

        List<String> jwtRoles = extractRealmRoles(jwt);
        Role role = jwtRoles.contains("ROLE_ADMIN")
                ? Role.ROLE_ADMIN
                : Role.ROLE_USER;

        userRepository.findByKeycloakId(keycloakId)
                .orElseGet(() -> {
                    User user = User.builder()
                            .keycloakId(keycloakId)
                            .email(email)
                            .role(role)
                            .build();

                    return userRepository.save(user);
                });
    }

    private List<String> extractRealmRoles(Jwt jwt) {
        var realmAccess = jwt.getClaimAsMap("realm_access");

        if (realmAccess == null)
            return List.of();

        Object roles = realmAccess.get("roles");

        if (roles instanceof List<?> roleList) {
            return roleList.stream()
                    .map(Object::toString)
                    .toList();
        }

        return List.of();
    }
}
