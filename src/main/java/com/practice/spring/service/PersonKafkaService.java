package com.practice.spring.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PersonKafkaService {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final Logger logger = LoggerFactory.getLogger(PersonKafkaService.class);

    public void sendMessage(String message) {
        logger.info(String.format("$$ -> Producing message --> %s", message));
        this.kafkaTemplate.send("users", message);
    }


    @KafkaListener(topics = "users", groupId = "group_id")
    public void consume(String message) {
        logger.info(String.format("$$ -> Consumed Message -> %s", message));
    }
}
