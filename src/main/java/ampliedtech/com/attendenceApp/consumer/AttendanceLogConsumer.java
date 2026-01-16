package ampliedtech.com.attendenceApp.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import ampliedtech.com.attendenceApp.document.AttendanceLog;
import ampliedtech.com.attendenceApp.event.AttendanceEvent;
import ampliedtech.com.attendenceApp.repository.AttendanceLogRepository;
import ampliedtech.com.attendenceApp.configuration.RabbitMqConfig;

@Component
public class AttendanceLogConsumer {
private final AttendanceLogRepository attendanceLogRepository;
public AttendanceLogConsumer(AttendanceLogRepository attendanceLogRepository){
    this.attendanceLogRepository = attendanceLogRepository;
}
@RabbitListener(queues = RabbitMqConfig.LOG_QUEUE)
public void consume(AttendanceEvent event){
    AttendanceLog attendanceLog = new AttendanceLog();
    attendanceLog.setUserId(event.getId());
    attendanceLog.setEmail(event.getEmail());
    attendanceLog.setAction(event.getAction());
    attendanceLog.setTime(event.getTime());

    attendanceLogRepository.save(attendanceLog);
}
}
