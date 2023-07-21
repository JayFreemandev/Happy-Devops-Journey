package org.wiki.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class Producer {

    private static final Logger log = LoggerFactory.getLogger(Producer.class.getSimpleName());

    public static void main(String[] args) {
        UserInfo userInfo = new UserInfo();

        // Producer Properties
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "cluster.playground.cdkt.io:9092");
        properties.setProperty("security.protocol", "SASL_SSL");
        properties.setProperty("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\""+userInfo.getUserName()+"\" password=\""+userInfo.getPassword()+"\";");
        properties.setProperty("sasl.mechanism", "PLAIN");
        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer", StringSerializer.class.getName());

        // Producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        // Record
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>("demo_java", "toto");

        producer.send(producerRecord);
        producer.flush();
        producer.close();
    }
}