package com.kaleem.identity.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;

@Configuration
public class KafkaErrorHandlerConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaErrorHandlerConfig.class);

    @Bean
    public DefaultErrorHandler errorHandler(
            DeadLetterPublishingRecoverer deadLetterPublishingRecoverer) {

        // retry up to 3 times with exponential backoff
        // 1st retry after 1s, 2nd after 2s, 3rd after 4s
        ExponentialBackOffWithMaxRetries backOff =
                new ExponentialBackOffWithMaxRetries(3);
        backOff.setInitialInterval(1_000L);
        backOff.setMultiplier(2.0);
        backOff.setMaxInterval(5_000L);

        DefaultErrorHandler handler =
                new DefaultErrorHandler(deadLetterPublishingRecoverer, backOff);

        // do NOT retry deserialization errors — the message itself is broken,
        // retrying will never fix it. send straight to DLT.
        handler.addNotRetryableExceptions(
                org.springframework.kafka.support.converter
                        .ConversionException.class,
                org.springframework.kafka.listener
                        .ListenerExecutionFailedException.class
        );

        handler.setRetryListeners((record, ex, attempt) ->
                log.warn("Retry attempt [{}] for message [{}] reason [{}]",
                        attempt,
                        record.key(),
                        ex.getMessage()));

        return handler;
    }

    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(
            KafkaTemplate<Object, Object> kafkaTemplate) {

        return new DeadLetterPublishingRecoverer(kafkaTemplate,
                (record, ex) -> {
                    log.error("Message failed all retries. Sending to DLT. " +
                                    "Topic [{}] partition [{}] offset [{}] reason [{}]",
                            record.topic(),
                            record.partition(),
                            record.offset(),
                            ex.getMessage());

                    // Spring automatically appends .DLT to your topic name
                    // order-events → order-events.DLT
                    return new org.apache.kafka.common.TopicPartition(
                            record.topic() + ".DLT", record.partition());
                });
    }
}