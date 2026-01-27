package ampliedtech.com.attendenceApp.mapper;

import ampliedtech.com.attendenceApp.document.AttendanceDocument;
import ampliedtech.com.attendenceApp.responseDto.AllAtendanceResponse;

public class GetAllAtendanceMapper {
private GetAllAtendanceMapper(){}
  public static AllAtendanceResponse toDto(AttendanceDocument attendanceDocument){
        return new AllAtendanceResponse(attendanceDocument.getDate(),attendanceDocument.getStatus());
    }
}
