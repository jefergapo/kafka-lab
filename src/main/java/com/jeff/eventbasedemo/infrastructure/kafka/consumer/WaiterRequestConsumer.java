package com.jeff.eventbasedemo.infrastructure.kafka.consumer;

import com.jeff.eventbasedemo.domain.model.WaiterNotification;
import com.jeff.eventbasedemo.domain.model.WaiterRequest;
import com.jeff.eventbasedemo.domain.service.WaiterAssignmentService;
import com.jeff.eventbasedemo.infrastructure.websocket.handler.WaiterWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WaiterRequestConsumer {

    private static final Logger logger = LoggerFactory.getLogger(WaiterRequestConsumer.class);

    private final WaiterWebSocketHandler webSocketHandler;
    private final WaiterAssignmentService waiterAssignmentService; // Nuevo servicio para asignar meseros

    @Value("${kafka.topic.notification}")
    private String waiterNotificationTopic;

    public WaiterRequestConsumer(WaiterWebSocketHandler webSocketHandler, WaiterAssignmentService waiterAssignmentService) {
        this.webSocketHandler = webSocketHandler;
        this.waiterAssignmentService = waiterAssignmentService;
    }

    @KafkaListener(topics = "${kafka.topic.request}", groupId = "waiter-service-group")
    public void consumeWaiterRequest(WaiterRequest request) {
        logger.info("Received waiter request: {}", request);

        Optional<String> assignedWaiterId = waiterAssignmentService.assignWaiter(request.tableNumber());

        WaiterNotification notification = new WaiterNotification(request.requestId(), request.tableNumber());
        assignedWaiterId.ifPresentOrElse(waiterId -> {
            logger.info("Sending specific waiter request {} to waiter: {}", request.requestId(), waiterId);
            webSocketHandler.notifySpecificWaiter(waiterId , notification);
        }, () -> {
            webSocketHandler.notifyWaiters(notification); // Notifica a todos
        });


    }
}