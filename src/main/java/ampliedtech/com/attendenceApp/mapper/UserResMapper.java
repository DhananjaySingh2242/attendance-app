package ampliedtech.com.attendenceApp.mapper;

import ampliedtech.com.attendenceApp.entity.User;
import ampliedtech.com.attendenceApp.responseDto.UpdateResponse;

public class UserResMapper {
public static UpdateResponse toDto(User user){
    UpdateResponse res = new UpdateResponse();
    res.setId(user.getId());
    res.setEmail(user.getEmail());
    res.setName(user.getName());
    res.setPassword(user.getPassword());
    return res;
}
}
