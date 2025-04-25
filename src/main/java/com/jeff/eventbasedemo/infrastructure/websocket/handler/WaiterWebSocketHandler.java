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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class WaiterWebSocketHandler implements WebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(WaiterWebSocketHandler.class);
    private final Sinks.Many<WaiterNotification> notificationSink = Sinks.many().multicast().onBackpressureBuffer();
    private final Flux<WaiterNotification> notifications = notificationSink.asFlux();
    private final ObjectMapper objectMapper;
    private final Map<String, WebSocketSession> waiterSessions = new ConcurrentHashMap<>(); // Registro de sesiones

    public WaiterWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String waiterId = (String) session.getHandshakeInfo().getHeaders().get("waiter-id").getFirst();

        logger.info("New WebSocket connection established for waiter: {}", waiterId);
        waiterSessions.put(waiterId, session); // Registrar la sesión

        return session.send(notifications.map(notification -> {
                    try {
                        return session.textMessage(objectMapper.writeValueAsString(notification));
                    } catch (IOException e) {
                        logger.error("Error serializing notification to JSON", e);
                        return session.textMessage("Error sending notification");
                    }
                }))
                .and(session.receive()
                             .doOnNext(message -> logger.info("Received message from waiter {}: {}", waiterId, message.getType()))
                             .then())
                .doFinally(signalType -> {
                    logger.info("WebSocket session closed for waiter: {}", waiterId);
                    waiterSessions.remove(waiterId); // Eliminar la sesión al cerrar
                });
    }

    public void notifyWaiters(WaiterNotification notification) {
        logger.info("Broadcasting waiter notification: {}", notification);
        notificationSink.tryEmitNext(notification).orThrow();
    }

    public void notifySpecificWaiter(String waiterId, WaiterNotification notification) {
        WebSocketSession session = waiterSessions.get(waiterId);
        if (session != null && session.isOpen()) {
            try {
                String message = objectMapper.writeValueAsString(notification);
                logger.info("Sending specific notification {} to waiter {}", notification, waiterId);
                session.send(Mono.just(session.textMessage(message))).subscribe();
            } catch (IOException e) {
                logger.error("Error sending specific notification to waiter {}", waiterId, e);
            }
        } else {
            logger.info("Could not send notification {} to waiter {}, session not found or closed. notifying al waiters", notification, waiterId);
            this.notifyWaiters(notification);
        }
    }
}