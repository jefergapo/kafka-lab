package com.jeff.eventbasedemo.infrastructure;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic waiterRequestTopic() {
        return TopicBuilder.name("waiter-requests")
                .partitions(1) // Adjust as needed
                .replicas(1)   // Adjust as needed for production
                .build();
    }

    @Bean
    public NewTopic waiterNotificationTopic() {
        return TopicBuilder.name("waiter-notifications")
                .partitions(1) // Adjust as needed
                .replicas(1)   // Adjust as needed for production
                .build();
    }
}