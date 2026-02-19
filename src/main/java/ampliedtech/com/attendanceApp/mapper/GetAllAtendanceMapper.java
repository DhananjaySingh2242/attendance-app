package ampliedtech.com.attendanceApp.mapper;

import ampliedtech.com.attendanceApp.document.AttendanceDocument;
import ampliedtech.com.attendanceApp.responseDto.AllAtendanceResponse;

public class GetAllAtendanceMapper {
private GetAllAtendanceMapper(){}
  public static AllAtendanceResponse toDto(AttendanceDocument attendanceDocument){
        return new AllAtendanceResponse(attendanceDocument.getDate(),attendanceDocument.getStatus());
    }
}
