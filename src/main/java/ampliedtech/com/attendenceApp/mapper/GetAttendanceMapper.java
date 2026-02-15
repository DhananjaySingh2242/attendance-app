package ampliedtech.com.attendenceApp.mapper;

import ampliedtech.com.attendenceApp.document.AttendanceDocument;
import ampliedtech.com.attendenceApp.responseDto.AttendanceResponse;

public class GetAttendanceMapper {
    private GetAttendanceMapper(){}
    public static AttendanceResponse toDto(AttendanceDocument attendanceDocument){
        return new AttendanceResponse(attendanceDocument.getKeycloakId(), attendanceDocument.getEmail(),
        attendanceDocument.getDate(),attendanceDocument.getStatus());
    }
}
