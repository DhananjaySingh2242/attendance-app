package ampliedtech.com.attendenceApp.mapper;

import ampliedtech.com.attendenceApp.entity.User;
import ampliedtech.com.attendenceApp.responseDto.UserResponse;

public class GetUserMapper {
    private GetUserMapper() {}

    public static UserResponse toDto(User user){
        return new UserResponse(user.getId(), user.getEmail(), user.getName(), user.getRole());
    }
}
