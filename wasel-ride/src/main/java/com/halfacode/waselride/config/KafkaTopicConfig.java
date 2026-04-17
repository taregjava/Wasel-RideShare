package com.halfacode.waselride.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic rideRequestedTopic(
            @Value("${app.kafka.topics.ride-requested}") String topicName,
            @Value("${app.kafka.topic.partitions:3}") int partitions,
            @Value("${app.kafka.topic.replicas:1}") int replicas
    ) {
        return TopicBuilder.name(topicName)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }
    @Bean
    public NewTopic rideMatchedTopic(
            @Value("${app.kafka.topics.ride-matched}") String topicName,
            @Value("${app.kafka.topic.partitions:3}") int partitions,
            @Value("${app.kafka.topic.replicas:1}") int replicas
    ) {
        return TopicBuilder.name(topicName)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

}

