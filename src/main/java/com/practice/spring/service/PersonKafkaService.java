package com.practice.spring.service;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PersonKafkaService {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "logs";

    private static final Logger logger = LoggerFactory.getLogger(PersonKafkaService.class);

    public Producer createProducer(){
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "localhost:9092");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        Producer<String, String> producer = new KafkaProducer<>(props);
        return producer;
    }

    public void sendMessage(String message) {
        logger.info(String.format("$$ -> Producing message --> %s", message));
        this.kafkaTemplate.send(TOPIC, message + new Date().getTime());
    }

    public void produceMessage(String message) {
        Producer<Long, String> producer = createProducer();
        producer.send(new ProducerRecord<Long, String>(TOPIC,new Date().getTime(),message));
    }

    @KafkaListener(topics = "users", groupId = "group_id")
    public void consume(String message) {
        logger.info(String.format("$$ -> Consumed Message -> %s", message));
    }

    public Consumer createConsumer() {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG,
                "KafkaExampleConsumer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                LongDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());

        final Consumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(TOPIC));
        return consumer;
    }

    public List<String> getLastNMessages(int numRecords) {
        Consumer consumer = createConsumer();
        List<String> result = new ArrayList<>();
        consumer.unsubscribe();
        List<PartitionInfo> partitionInfoList = consumer.partitionsFor(TOPIC);
        List<TopicPartition> partitionsList = new ArrayList<>();
        for (PartitionInfo p : partitionInfoList) {
            partitionsList.add(new TopicPartition(p.topic(), p.partition()));
        }
        consumer.assign(partitionsList);
        consumer.seekToEnd(partitionsList);
        for (TopicPartition topicPartition : partitionsList) {
            long startPos = consumer.position(topicPartition) - numRecords;
            consumer.seek(topicPartition, startPos < 0 ? 0 : startPos);
            ConsumerRecords<String, String> consumerRecords = null;
            do {
                consumerRecords = consumer.poll(Duration.ofSeconds(1));
                consumerRecords.forEach(record -> {
                    result.add(String.format("Consumer Record:(%d, %s, %d, %d)", record.key(), record.value(), record.partition(), record.offset()));
                });
            } while (consumerRecords != null && !consumerRecords.isEmpty());
        }
        consumer.close();
        System.out.println("DONE");
        return result;
    }

    public List<String> getLastNDays(int days) {
        Consumer consumer = createConsumer();
        consumer.unsubscribe();
        List<String> result = new ArrayList<>();
        // Get the list of partitions
        List<PartitionInfo> partitionInfos = consumer.partitionsFor(TOPIC);
        // Transform PartitionInfo into TopicPartition
        List<TopicPartition> topicPartitionList = partitionInfos.stream().map(info -> new TopicPartition(TOPIC, info.partition())).collect(Collectors.toList());
        // Assign the consumer to these partitions
        consumer.assign(topicPartitionList);
        // Look for offsets based on timestamp
        Map<TopicPartition, Long> partitionTimestampMap = topicPartitionList.stream()
                .collect(Collectors.toMap(tp -> tp, tp -> new Date().getTime() - days * 24 * 60 * 60 * 1000L));
        Map<TopicPartition, OffsetAndTimestamp> partitionOffsetMap = consumer.offsetsForTimes(partitionTimestampMap);
        if(partitionOffsetMap==null)
            return new ArrayList<>();
        // Force the consumer to seek for those offsets
        partitionOffsetMap.forEach((tp, offsetAndTimestamp) -> consumer.seek(tp, offsetAndTimestamp.offset()));
        ConsumerRecords<String, String> consumerRecords = null;
        do {
            consumerRecords = consumer.poll(Duration.ofSeconds(1));
            consumerRecords.forEach(record -> {
                result.add(String.format("Consumer Record:(%d, %s, %d, %d)", record.key(), record.value(), record.partition(), record.offset()));
            });
        } while (consumerRecords != null && !consumerRecords.isEmpty());
        System.out.println("DONE");
        return result;
    }
}
