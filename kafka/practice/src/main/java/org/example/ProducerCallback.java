package org.example;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerCallback {

    private static final Logger log = LoggerFactory.getLogger(ProducerCallback.class.getSimpleName());

    public static void main(String[] args) {
        log.info("It's Producer Callback");
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

        producer.send(producerRecord, new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if(exception == null) { // success
                    log.info("recieve new metadata \n" +
                            "Topic"  + metadata.topic() + "\n" +
                            "Partition"  + metadata.partition() + "\n" +
                            "Offset"  + metadata.offset() + "\n" +
                            "TimeStamp"  + metadata.timestamp() + "\n");
                }else{
                    log.info("Exception -> " + exception);
                }
            }
        });
        producer.flush();
        producer.close();
    }
}