package com.kaleem.identity.producer;

import com.kaleem.identity.config.KafkaHealthIndicator;
import com.kaleem.identity.model.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Status;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;


@Service
public class OrderProducerService {

    private static final Logger log = LoggerFactory.getLogger(OrderProducerService.class);

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;
    private final KafkaHealthIndicator kafkaHealthIndicator;

    @Value("${app.kafka.topic}")
    private String topic;

    public OrderProducerService(KafkaTemplate<String, OrderEvent> kafkaTemplate,KafkaHealthIndicator kafkaHealthIndicator) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaHealthIndicator = kafkaHealthIndicator;
    }

    public void publishOrder(OrderEvent event) {
        CompletableFuture<SendResult<String, OrderEvent>> future =
                kafkaTemplate.send(topic, event.getOrderId(), event);

        // fail fast — don't even attempt if broker is known to be down
        if (kafkaHealthIndicator.health().getStatus()
                .equals(Status.DOWN)) {
            log.error("Broker is down. Order [{}] cannot be published.", event.getOrderId());
            throw new RuntimeException("Kafka broker unavailable. Please try again later.");
        }

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Published order [{}] to topic [{}] partition [{}] offset [{}]",
                        event.getOrderId(),
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Failed to publish order [{}] reason [{}]",
                        event.getOrderId(),
                        ex.getMessage());
            }
        });
    }
}