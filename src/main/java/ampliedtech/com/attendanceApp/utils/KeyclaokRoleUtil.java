package ampliedtech.com.attendanceApp.utils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class KeyclaokRoleUtil {

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(Jwt jwt) {

        Map<String, Object> realmAccess =
                (Map<String, Object>) jwt.getClaims().get("realm_access");

        if (realmAccess == null) {
            return Collections.emptyList();
        }

        return (List<String>) realmAccess.getOrDefault("roles", List.of());
    }
}
