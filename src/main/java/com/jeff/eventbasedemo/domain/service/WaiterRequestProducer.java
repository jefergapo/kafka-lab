package com.jeff.eventbasedemo.domain.service;


import com.jeff.eventbasedemo.domain.model.WaiterRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class WaiterRequestProducer {

    private static final Logger logger = LoggerFactory.getLogger(WaiterRequestProducer.class);

    private final KafkaTemplate<String, WaiterRequest> kafkaTemplate;

    @Value("${kafka.topic.request}")
    private String waiterRequestTopic;

    public WaiterRequestProducer(KafkaTemplate<String, WaiterRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public Mono<Void> sendWaiterRequest(WaiterRequest request) {
        return Mono.fromRunnable(() -> {
            logger.info("Sending waiter request: {}", request);
            kafkaTemplate.send(waiterRequestTopic, request.requestId().toString(), request);
        }).then();
    }
}