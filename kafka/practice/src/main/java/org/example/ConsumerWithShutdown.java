package org.example;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ConsumerWithShutdown {

    private static final Logger log = LoggerFactory.getLogger(ConsumerWithShutdown.class.getSimpleName());

    public static void main(String[] args) {
        log.info("It's Consumer Graceful Shutdown");
        UserInfo userInfo = new UserInfo();
        String groupId = "my-java-application";
        String topic = "demo_java";

        // Consumer Properties
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "cluster.playground.cdkt.io:9092");
        properties.setProperty("security.protocol", "SASL_SSL");
        properties.setProperty("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\""+userInfo.getUserName()+"\" password=\""+userInfo.getPassword()+"\";");
        properties.setProperty("sasl.mechanism", "PLAIN");
        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer", StringSerializer.class.getName());
        properties.setProperty("group.id", groupId);
        properties.setProperty("auto.offset.reset", "earliest");

        // Consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

        final Thread mainThread = Thread.currentThread();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run(){
                log.info("Dected a shutdown! -> exit by calling consumer wake up");
                consumer.wakeup();

                try{
                    mainThread.join();
                } catch (Exception exception){
                    exception.getStackTrace();
                }
            }
        });

        try {
            consumer.subscribe(Arrays.asList(topic));
            while (true) {
                log.info("Polling 뽈링");
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, String> record : records) {
                    log.info("recieve new metadata \n" +
                            "Key" + record.key() + "\n" +
                            "Value" + record.value() + "\n" +
                            "Partition" + record.partition() + "\n" +
                            "Offset" + record.offset() + "\n");
                }
            }
        } catch (WakeupException wakeupException){
            log.info("Consumer suhtdown... -> " + wakeupException);
        } catch (Exception exception){
            log.info("Serios Exception -> " + exception);
        } finally {
            consumer.close();
            log.info("Finished Graceful Shutdown");
        }
    }
}