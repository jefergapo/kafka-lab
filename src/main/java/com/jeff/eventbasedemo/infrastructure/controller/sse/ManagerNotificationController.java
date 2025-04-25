package com.jeff.eventbasedemo.infrastructure.controller.sse;

import com.jeff.eventbasedemo.domain.model.OrderNotification;
import com.jeff.eventbasedemo.infrastructure.kafka.consumer.OrderConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/manager/notifications")
public class ManagerNotificationController {

    private final Logger logger = LoggerFactory.getLogger(ManagerNotificationController.class);

    private final OrderConsumer orderConsumer;

    public ManagerNotificationController(OrderConsumer orderConsumer) {
        this.orderConsumer = orderConsumer;
    }

    @GetMapping(value = "/orders", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<OrderNotification> orderNotifications() {
        return orderConsumer.getOrderNotifications()
                .map(notification -> {
                    logger.info("Sending order notification to manager: {}", notification);
                    return notification;
                });
    }
}