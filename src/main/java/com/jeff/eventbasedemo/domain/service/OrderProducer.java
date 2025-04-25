package com.jeff.eventbasedemo.domain.service;

import com.jeff.eventbasedemo.domain.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class OrderProducer {

    private static final Logger logger = LoggerFactory.getLogger(OrderProducer.class);

    private final KafkaTemplate<String, Order> kafkaTemplate;

    @Value("${kafka.topic.orders}")
    private String ordersTopic;

    public OrderProducer(KafkaTemplate<String, Order> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public Mono<Void> sendOrder(Order order) {
        return Mono.fromRunnable(() -> {
            logger.info("Sending order: {}", order);
            kafkaTemplate.send(ordersTopic, order.orderId().toString(), order);
        }).then();
    }
}