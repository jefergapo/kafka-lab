package com.jeff.eventbasedemo.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderNotification(UUID orderId, LocalDateTime orderTime, String tableNumber) {
}