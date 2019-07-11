package com.practice.spring.service;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Service
public class PersonKafkaService {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "logs";

    private static final Logger logger = LoggerFactory.getLogger(PersonKafkaService.class);

    public void sendMessage(String message) {
        logger.info(String.format("$$ -> Producing message --> %s", message));
        this.kafkaTemplate.send(TOPIC, message + new Date().getTime());
    }

    @KafkaListener(topics = "users", groupId = "group_id")
    public void consume(String message) {
        logger.info(String.format("$$ -> Consumed Message -> %s", message));
    }

    public Consumer createConsumer(){
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG,
                "KafkaExampleConsumer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());

        final Consumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(TOPIC));
        return consumer;
    }

    public void getLastNRecords(int numRecords) {
        Consumer consumer = createConsumer();
        ConsumerRecords<String, String> consumerRecords = null;
        do{
            consumerRecords = consumer.poll(Duration.ofSeconds(1));
            numRecords -= consumerRecords.count();
            consumerRecords.forEach(record -> {
                System.out.printf("Consumer Record:(%d, %s, %d, %d)\n", record.key(), record.value(), record.partition(), record.offset());

            });
        }while (numRecords>0 && consumerRecords!=null && !consumerRecords.isEmpty());
        consumer.close();
        System.out.println("DONE");
    }

    public void getLastNDays(int days){
        Consumer consumer = createConsumer();
        List<PartitionInfo> partitionInfoList = consumer.partitionsFor(TOPIC);
        for (PartitionInfo partitionInfo : partitionInfoList) {
            Map<TopicPartition, Long> map = new HashMap();
            TopicPartition tp = new TopicPartition(partitionInfo.topic(),partitionInfo.partition());
            map.put(tp, days*24*60*60*1000L);
            Map<TopicPartition, OffsetAndTimestamp>  offsetMap = consumer.offsetsForTimes(map);
            // Build map of partition => offset
            for (Map.Entry<TopicPartition, OffsetAndTimestamp> entry: offsetMap.entrySet()) {
                consumer.unsubscribe();
                consumer.assign(Arrays.asList(entry.getKey()));
                consumer.seek(entry.getKey(),entry.getValue().offset());
                ConsumerRecords<String, String> consumerRecords = null;
                do{
                    consumerRecords = consumer.poll(Duration.ofSeconds(1));
                    consumerRecords.forEach(record -> {
                        System.out.printf("Consumer Record:(%d, %s, %d, %d)\n", record.key(), record.value(), record.partition(), record.offset());
                    });
                }while (consumerRecords!=null && !consumerRecords.isEmpty());
            }

        }
    }

}
