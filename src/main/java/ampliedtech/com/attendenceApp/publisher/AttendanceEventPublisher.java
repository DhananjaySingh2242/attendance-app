package ampliedtech.com.attendenceApp.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import ampliedtech.com.attendenceApp.configuration.RabbitMqConfig;
import ampliedtech.com.attendenceApp.event.AttendanceEvent;

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

