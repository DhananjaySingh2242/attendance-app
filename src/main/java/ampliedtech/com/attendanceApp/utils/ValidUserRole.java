package ampliedtech.com.attendanceApp.utils;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class ValidUserRole {

    public void validateUserRole(Jwt jwt) {

        List<String> roles = extractRoles(jwt);

        if (!roles.contains("USER")) {
            throw new AccessDeniedException("Only USER can mark attendance");
        }
    }


    private List<String> extractRoles(Jwt jwt) {

        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        if (realmAccess == null) {
            return List.of();
        }

        Object rolesObj = realmAccess.get("roles");

        if (rolesObj instanceof List<?> roles) {
            return roles.stream().map(String::valueOf).toList();
        }

        return List.of();
    }

   
    public String extractPrimaryRole(Jwt jwt) {

        List<String> roles = extractRoles(jwt);

        if (roles.contains("ADMIN")) return "ADMIN";
        if (roles.contains("USER")) return "USER";

        return "UNKNOWN";
    }
}