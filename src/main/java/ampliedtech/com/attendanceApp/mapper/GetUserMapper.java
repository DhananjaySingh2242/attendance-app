package ampliedtech.com.attendanceApp.mapper;

import java.util.Collections;
import java.util.List;

import ampliedtech.com.attendanceApp.entity.User;
import ampliedtech.com.attendanceApp.responseDto.UserResponse;

public class GetUserMapper {
    private GetUserMapper() {
    }

    public static UserResponse toDto(User user) {

        List<String> roles = user.getRole() != null
                ? List.of(user.getRole().name())
                : Collections.emptyList();

        return new UserResponse(
                user.getId(),
                user.getKeycloakId(),
                user.getEmail(),
                user.getName(),
                roles);
    }
}
