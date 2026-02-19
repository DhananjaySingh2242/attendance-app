package ampliedtech.com.attendanceApp.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import ampliedtech.com.attendanceApp.configuration.RabbitMqConfig;
import ampliedtech.com.attendanceApp.event.AttendanceEvent;

@Component
public class AttendanceEventPublisher {
private final RabbitTemplate rabbitTemplate;
public AttendanceEventPublisher(RabbitTemplate rabbitTemplate){
    this.rabbitTemplate = rabbitTemplate;
}

public void publish(AttendanceEvent event){
    rabbitTemplate.convertAndSend(
        RabbitMqConfig.EXCHANGE,
        "",
        event
    );
}
}