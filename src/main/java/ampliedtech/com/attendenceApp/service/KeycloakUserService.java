package ampliedtech.com.attendenceApp.service;

import ampliedtech.com.attendenceApp.requestDto.RegisterRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class KeycloakUserService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${keycloak.auth-server-url}")
    private String keycloakUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.admin.client-id}")
    private String clientId;

    @Value("${keycloak.admin.client-secret}")
    private String clientSecret;


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
            throw new RuntimeException("Failed to get admin token from Keycloak");
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

        restTemplate.postForEntity(
                url,
                new HttpEntity<>(body, headers),
                Void.class
        );


        String userSearchUrl = keycloakUrl + "/admin/realms/" + realm +
                "/users?username=" + request.getEmail();

        List<Map<String, Object>> users = restTemplate.exchange(
                userSearchUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                List.class
        ).getBody();

        if (users == null || users.isEmpty()) {
            throw new RuntimeException("User created but not found in Keycloak");
        }

        String userId = users.get(0).get("id").toString();

    }


    private String getRoleId(String roleName, String adminToken) {
        String url = keycloakUrl + "/admin/realms/" + realm + "/roles/" + roleName;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        Map<?, ?> role = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class
        ).getBody();

        if (role == null || role.get("id") == null) {
            throw new RuntimeException("Role not found: " + roleName);
        }

        return role.get("id").toString();
    }

    private void assignRole(String userId, String roleName, String adminToken) {

        String roleId = getRoleId(roleName, adminToken);

        String url = keycloakUrl + "/admin/realms/" + realm +
                "/users/" + userId + "/role-mappings/realm";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> role = new HashMap<>();
        role.put("id", roleId);
        role.put("name", roleName);

        restTemplate.postForEntity(
                url,
                new HttpEntity<>(List.of(role), headers),
                Void.class
        );
    }
}
