package com.jeff.eventbasedemo.infrastructure.controller;

import com.jeff.eventbasedemo.domain.model.Order;
import com.jeff.eventbasedemo.domain.model.OrderItem;
import com.jeff.eventbasedemo.domain.service.OrderProducer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderProducer orderProducer;

    public OrderController(OrderProducer orderProducer) {
        this.orderProducer = orderProducer;
    }

    @PostMapping(value = "/place", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> placeOrder(@RequestBody OrderPayload payload) {
        Order order = new Order(payload.tableNumber(), payload.items());
        return orderProducer.sendOrder(order);
    }

    public record OrderPayload(String tableNumber, List<OrderItem> items) {}
}