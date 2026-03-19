package com.kaleem.identity.controller;

import com.kaleem.identity.model.OrderEvent;
import com.kaleem.identity.producer.OrderProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/api/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final OrderProducerService producerService;

    public OrderController(OrderProducerService producerService) {
        this.producerService = producerService;
    }

    @PostMapping
    public ResponseEntity<String> placeOrder(@RequestBody OrderEvent event) {
        log.info("Order received via REST [{}]", event.getOrderId());
        producerService.publishOrder(event);
        return ResponseEntity.accepted().body("Order accepted: " + event.getOrderId());
    }
}
