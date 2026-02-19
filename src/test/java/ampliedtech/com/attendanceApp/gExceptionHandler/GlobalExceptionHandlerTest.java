package ampliedtech.com.attendanceApp.gExceptionHandler;

import ampliedtech.com.attendanceApp.exception.AlreadyCheckedInException;
import ampliedtech.com.attendanceApp.exception.UserNotFoundException;
import ampliedtech.com.attendanceApp.responseDto.ApiError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleApiException_returnsCorrectStatusAndBody() {
        AlreadyCheckedInException ex = new AlreadyCheckedInException();
        ResponseEntity<ApiError> response = handler.handleApiException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("ALREADY_CHECKED_IN");
        assertThat(response.getBody().getMessage()).isEqualTo("Attendance already marked for today");
        assertThat(response.getBody().getStatus()).isEqualTo(409);
    }

    @Test
    void handleUserNotFoundException_returnsNotFound() {
        UserNotFoundException ex = new UserNotFoundException(1L);
        ResponseEntity<ApiError> response = handler.handleApiException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("USER_NOT_FOUND");
    }
}
