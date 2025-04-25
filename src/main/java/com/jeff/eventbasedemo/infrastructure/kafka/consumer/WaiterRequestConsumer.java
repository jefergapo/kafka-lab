package com.jeff.eventbasedemo.infrastructure.kafka.consumer;

import com.jeff.eventbasedemo.domain.model.WaiterNotification;
import com.jeff.eventbasedemo.domain.model.WaiterRequest;
import com.jeff.eventbasedemo.infrastructure.websocket.handler.WaiterWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class WaiterRequestConsumer {

    private static final Logger logger = LoggerFactory.getLogger(WaiterRequestConsumer.class);

    private final WaiterWebSocketHandler webSocketHandler;

    @Value("${kafka.topic.notification}")
    private String waiterNotificationTopic;

    public WaiterRequestConsumer(WaiterWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    @KafkaListener(topics = "${kafka.topic.request}", groupId = "waiter-service-group")
    public void consumeWaiterRequest(WaiterRequest request) {
        logger.info("Received waiter request: {}", request);
        // Process the request (e.g., store in a database, log, etc.)
        // Then, notify the waiters via WebSocket
        WaiterNotification notification = new WaiterNotification(request.requestId(), request.tableNumber());
        webSocketHandler.notifyWaiters(notification);
    }
}