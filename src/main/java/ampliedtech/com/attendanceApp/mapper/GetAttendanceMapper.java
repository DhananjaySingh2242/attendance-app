package ampliedtech.com.attendanceApp.mapper;

import ampliedtech.com.attendanceApp.document.AttendanceDocument;
import ampliedtech.com.attendanceApp.responseDto.AttendanceResponse;

public class GetAttendanceMapper {
    private GetAttendanceMapper(){}
    public static AttendanceResponse toDto(AttendanceDocument attendanceDocument){
        return new AttendanceResponse(attendanceDocument.getKeycloakId(), attendanceDocument.getEmail(),
        attendanceDocument.getDate(),attendanceDocument.getStatus());
    }
}
