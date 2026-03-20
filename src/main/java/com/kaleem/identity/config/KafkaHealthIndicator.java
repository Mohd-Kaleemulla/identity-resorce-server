package com.kaleem.identity.config;

import org.apache.kafka.clients.admin.AdminClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class KafkaHealthIndicator implements HealthIndicator {

    private static final Logger log = LoggerFactory.getLogger(KafkaHealthIndicator.class);

    private final KafkaAdmin kafkaAdmin;

    public KafkaHealthIndicator(KafkaAdmin kafkaAdmin) {
        this.kafkaAdmin = kafkaAdmin;
    }

    @Override
    public Health health() {
        try (AdminClient client = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            client.listTopics()
                    .names()
                    .get(3, TimeUnit.SECONDS);   // fail fast after 3s
            return Health.up().withDetail("broker", "reachable").build();
        } catch (Exception ex) {
            log.warn("Kafka broker unreachable: {}", ex.getMessage());
            return Health.down()
                    .withDetail("broker", "unreachable")
                    .withDetail("reason", ex.getMessage())
                    .build();
        }
    }
}
