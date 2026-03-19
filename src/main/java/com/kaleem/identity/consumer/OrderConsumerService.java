package com.kaleem.identity.consumer;

import com.kaleem.identity.model.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumerService {

    private static final Logger log = LoggerFactory.getLogger(OrderConsumerService.class);

    @KafkaListener(
            topics = "${app.kafka.topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(
            @Payload OrderEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        log.info("Received order [{}] from topic [{}] partition [{}] offset [{}]",
                event.getOrderId(),
                topic,
                partition,
                offset);

        processOrder(event);
    }

    private void processOrder(OrderEvent event) {
        log.info("Processing order [{}] product [{}] quantity [{}] status [{}]",
                event.getOrderId(),
                event.getProduct(),
                event.getQuantity(),
                event.getStatus());

        // persist to DB, trigger downstream logic, etc.
    }
}