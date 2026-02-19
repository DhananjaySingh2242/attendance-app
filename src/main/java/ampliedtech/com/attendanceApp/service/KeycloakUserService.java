package ampliedtech.com.attendanceApp.service;

import ampliedtech.com.attendanceApp.entity.Role;
import ampliedtech.com.attendanceApp.entity.User;
import ampliedtech.com.attendanceApp.exception.KeycloakIntegrationException;
import ampliedtech.com.attendanceApp.jpaRepo.UserRepository;
import ampliedtech.com.attendanceApp.requestDto.RegisterRequest;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class KeycloakUserService {

    private static final Logger log = LoggerFactory.getLogger(KeycloakUserService.class);
    private static final String DEFAULT_REALM_ROLE = "USER";

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final Keycloak keycloakAdmin;

    @Value("${keycloak.auth-server-url:http://localhost:8080}")
    private String keycloakUrl;

    @Value("${keycloak.realm:keycloak-demo}")
    private String realm;

    @Value("${keycloak.admin.client-id:admin-cli}")
    private String clientId;

    @Value("${keycloak.admin.client-secret:}")
    private String clientSecret;

    public KeycloakUserService(RestTemplate restTemplate, UserRepository userRepository, Keycloak keycloakAdmin) {
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
        this.keycloakAdmin = keycloakAdmin;
    }


    private String getAdminToken() {
        String tokenUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<?, ?> response = restTemplate.postForObject(
                tokenUrl,
                new HttpEntity<>(body, headers),
                Map.class
        );

        if (response == null || response.get("access_token") == null) {
            throw new KeycloakIntegrationException("Failed to get admin token from Keycloak");
        }

        return response.get("access_token").toString();
    }


    public void registerUser(RegisterRequest request) {

        Objects.requireNonNull(request.getEmail(), "Email is required");
        Objects.requireNonNull(request.getPassword(), "Password is required");

        String adminToken = getAdminToken();

        String url = keycloakUrl + "/admin/realms/" + realm + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("username", request.getEmail());
        body.put("email", request.getEmail());
        body.put("enabled", true);

        if (request.getFirstName() != null) {
            body.put("firstName", request.getFirstName());
        }
        if (request.getLastName() != null) {
            body.put("lastName", request.getLastName());
        }

        Map<String, Object> credential = new HashMap<>();
        credential.put("type", "password");
        credential.put("value", request.getPassword());
        credential.put("temporary", false);

        body.put("credentials", List.of(credential));

        ResponseEntity<Void> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                Void.class
        );

        String userId = null;
        String location = response.getHeaders().getFirst(HttpHeaders.LOCATION);
        if (location != null && location.contains("/users/")) {
            userId = location.substring(location.lastIndexOf("/") + 1);
        }

        if (userId == null) {
            String userSearchUrl = keycloakUrl + "/admin/realms/" + realm +
                    "/users?username=" + request.getEmail() + "&exact=true";
            List<Map<String, Object>> users = restTemplate.exchange(
                    userSearchUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    List.class
            ).getBody();
            if (users == null || users.isEmpty()) {
                throw new KeycloakIntegrationException("User created but not found in Keycloak");
            }
            userId = users.get(0).get("id").toString();
        }

        String name = Stream.of(request.getFirstName(), request.getLastName())
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .reduce((a, b) -> a + " " + b)
                .orElse(request.getEmail());
        User user = new User();
        user.setKeycloakId(userId);
        user.setEmail(request.getEmail());
        user.setName(name);
        user.setRole(Role.ROLE_USER);
        userRepository.save(user);

        assignRealmRole(userId, DEFAULT_REALM_ROLE);
    }

    /**
     * Assigns a realm role to a user using the Keycloak Admin Client (full admin permissions).
     * The RestTemplate + attendance-admin-client often lacks role-assignment permissions.
     */
    private void assignRealmRole(String userId, String roleName) {
        try {
            RoleRepresentation roleRep = keycloakAdmin.realm(realm).roles().get(roleName).toRepresentation();
            keycloakAdmin.realm(realm).users().get(userId).roles().realmLevel().add(List.of(roleRep));
            log.info("Assigned realm role {} to user {}", roleName, userId);
        } catch (Exception e) {
            String msg = e.getMessage() != null && (e.getMessage().contains("404") || e.getMessage().contains("Not Found"))
                    ? "Role not found: " + roleName + ". Create the USER realm role in Keycloak."
                    : "Failed to assign role " + roleName + ": " + e.getMessage();
            throw new KeycloakIntegrationException(msg, e);
        }
    }
}
