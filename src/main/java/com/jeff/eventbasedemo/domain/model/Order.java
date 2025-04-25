package com.jeff.eventbasedemo.domain.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record Order(UUID orderId, LocalDateTime orderTime, String tableNumber, List<OrderItem> items) {
    public Order(String tableNumber, List<OrderItem> items) {
        this(UUID.randomUUID(), LocalDateTime.now(), tableNumber, items);
    }
}