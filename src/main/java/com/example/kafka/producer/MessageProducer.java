package com.example.kafka.producer;

import com.example.entities.UserEntity;
import com.example.events.CreateUserEvent;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class MessageProducer {
    @Autowired
    private KafkaTemplate<String, CreateUserEvent> kafkaTemplate;

    public void sendMessage(String topic, String key, CreateUserEvent userEvent) {
        kafkaTemplate.send(topic, key, userEvent);
    }
}
