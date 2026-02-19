package ampliedtech.com.attendanceApp.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import ampliedtech.com.attendanceApp.document.AttendanceLog;
import ampliedtech.com.attendanceApp.event.AttendanceEvent;
import ampliedtech.com.attendanceApp.mongoRepo.AttendanceLogRepository;
import ampliedtech.com.attendanceApp.configuration.RabbitMqConfig;

@Component
public class AttendanceLogConsumer {
    private static final Logger log = LoggerFactory.getLogger(AttendanceLogConsumer.class);
    private final AttendanceLogRepository attendanceLogRepository;

    public AttendanceLogConsumer(AttendanceLogRepository attendanceLogRepository) {
        this.attendanceLogRepository = attendanceLogRepository;
    }

    @RabbitListener(queues = RabbitMqConfig.LOG_QUEUE)
    public void consume(AttendanceEvent event) {
        AttendanceLog attendanceLog = new AttendanceLog();
        attendanceLog.setUserId(event.getKeycloakId());
        attendanceLog.setEmail(event.getEmail());
        attendanceLog.setAction(event.getAction());
        attendanceLog.setTime(event.getTime());
        log.info("Attendance event consumed for user {}", event.getEmail());

        attendanceLogRepository.save(attendanceLog);
    }
}
