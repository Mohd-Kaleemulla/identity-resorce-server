package com.kaleem.identity.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderDltConsumerService {

    private static final Logger log =
            LoggerFactory.getLogger(OrderDltConsumerService.class);

    @KafkaListener(
            topics = "order-events.DLT",
            groupId = "${spring.kafka.consumer.group-id}-dlt"
    )
    public void consumeFromDlt(ConsumerRecord<String, Object> record) {
        log.error("DLT message received. Key [{}] partition [{}] offset [{}] value [{}]",
                record.key(),
                record.partition(),
                record.offset(),
                record.value());

        // in production: alert, store to DB for manual review,
        // trigger a notification, etc.
    }
}