package ampliedtech.com.attendanceApp.exception;

import org.springframework.http.HttpStatus;

public class KeycloakIntegrationException extends ApiException {

    public KeycloakIntegrationException(String message) {
        super(message, HttpStatus.BAD_GATEWAY, "KEYCLOAK_ERROR");
    }

    public KeycloakIntegrationException(String message, Throwable cause) {
        super(message, HttpStatus.BAD_GATEWAY, "KEYCLOAK_ERROR");
        initCause(cause);
    }
}
