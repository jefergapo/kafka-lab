package com.jeff.eventbasedemo.infrastructure.websocket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeff.eventbasedemo.domain.model.WaiterNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.io.IOException;

@Component
public class WaiterWebSocketHandler implements WebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(WaiterWebSocketHandler.class);
    private final Sinks.Many<WaiterNotification> notificationSink = Sinks.many().multicast().onBackpressureBuffer();
    private final Flux<WaiterNotification> notifications = notificationSink.asFlux();
    private final ObjectMapper objectMapper;

    public WaiterWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        logger.info("New WebSocket connection established for waiter.");
        return session.send(notifications.map(notification -> {
                    try {
                        return session.textMessage(objectMapper.writeValueAsString(notification));
                    } catch (IOException e) {
                        logger.error("Error serializing notification to JSON", e);
                        return session.textMessage("Error sending notification");
                    }
                }))
                .and(session.receive() // Handle any messages *from* the waiter (e.g., acknowledging a request)
                             .doOnNext(message -> logger.info("Received message from waiter: {}", message.getPayloadAsText()))
                             .then())
                .doFinally(signalType -> logger.info("WebSocket session closed for waiter."));
    }

    public void notifyWaiters(WaiterNotification notification) {
        logger.info("Broadcasting waiter notification: {}", notification);
        notificationSink.tryEmitNext(notification).orThrow();
    }
}