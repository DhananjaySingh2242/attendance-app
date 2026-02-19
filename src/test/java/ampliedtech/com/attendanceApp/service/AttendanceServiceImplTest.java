package ampliedtech.com.attendanceApp.service;

import ampliedtech.com.attendanceApp.entity.User;
import ampliedtech.com.attendanceApp.exception.AlreadyCheckedInException;
import ampliedtech.com.attendanceApp.mongoRepo.AttendanceRepo;
import ampliedtech.com.attendanceApp.publisher.AttendanceEventPublisher;
import ampliedtech.com.attendanceApp.utils.ValidUserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceImplTest {

    @Mock
    private UserService userService;
    @Mock
    private AttendanceRepo attendanceRepo;
    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private AttendanceEventPublisher attendanceEventPublisher;
    @Mock
    private ValidUserRole validUserRole;
    @Mock
    private Jwt jwt;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setKeycloakId("kc-1");
        user.setEmail("u@example.com");
        lenient().when(redisTemplate.hasKey(anyString())).thenReturn(false);
    }

    @Test
    void checkIn_throwsAlreadyCheckedInException_whenRedisHasKey() {
        when(userService.findOrCreateUserByJwt(jwt)).thenReturn(user);
        when(redisTemplate.hasKey("attendance:active:kc-1")).thenReturn(true);

        AttendanceServiceImpl service = new AttendanceServiceImpl(
                userService, attendanceRepo, redisTemplate, attendanceEventPublisher, validUserRole);

        assertThatThrownBy(() -> service.checkIn(jwt))
                .isInstanceOf(AlreadyCheckedInException.class);

        verify(validUserRole).validateUserRole(jwt);
        verify(userService).findOrCreateUserByJwt(jwt);
    }
}
