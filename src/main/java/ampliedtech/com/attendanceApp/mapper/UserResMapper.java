package ampliedtech.com.attendanceApp.mapper;

import ampliedtech.com.attendanceApp.entity.User;
import ampliedtech.com.attendanceApp.responseDto.UpdateResponse;

public class UserResMapper {
    private UserResMapper(){}
    public static UpdateResponse toDto(User user) {
        UpdateResponse res = new UpdateResponse();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setMessage("User Updated");
        return res;
    }
}
