package com.jeff.eventbasedemo.infrastructure.kafka.consumer;

import com.jeff.eventbasedemo.domain.model.Order;
import com.jeff.eventbasedemo.domain.model.OrderNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;

@Service
public class OrderConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OrderConsumer.class);

    private final Sinks.Many<OrderNotification> orderNotificationSink = Sinks.many().multicast().onBackpressureBuffer();;
    private final Flux<OrderNotification> orderNotifications = orderNotificationSink.asFlux();

    public OrderConsumer() {
    }

    @KafkaListener(topics = "${kafka.topic.orders}", groupId = "order-service-group")
    public void consumeOrder(Order order) {
        logger.info("Received new order: {}", order);
        OrderNotification notification = new OrderNotification(order.orderId(), order.orderTime(), order.tableNumber());
        this.orderNotificationSink.tryEmitNext(notification); // Enviar notificación SSE
    }

    public Flux<OrderNotification> getOrderNotifications() {
        return this.orderNotifications;
    }
}