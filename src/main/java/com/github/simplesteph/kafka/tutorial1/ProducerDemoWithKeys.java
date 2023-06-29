package com.github.simplesteph.kafka.tutorial1;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemoWithKeys {
    public static void main(String[] args) {

        Logger logger = LoggerFactory.getLogger(ProducerDemoWithKeys.class);
        //create Producer properties

        String bootstrapServers = "127.0.0.1:9092";

        Properties properties = new Properties();
        /*
        ------- a hardcoded way to create properties

        properties.setProperty("bootstrap.servers", bootstrapServers);
        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer", StringSerializer.class.getName());
         */

        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        //create the producer

        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        for (int i = 0; i < 10; i++) {

            String topic = "first_topic";
            String value = "hello world " + i;
            String key = "id_" + i;

            //create a producer record
            ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, key, value);
            //Send data -- asychronous
            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    //executes every time a record is successfully sent or an exception is thrown

                    if (e == null) {
                        //the record successfully sent
                        logger.info(
                                "received new metadata. Topic = {}, Partition = {} Offset = {}, timestamp = {}, key = {}",
                                recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset(),
                                recordMetadata.timestamp(), key);
                    } else {
                        logger.error("error while producing");
                    }
                }
            });
        }

        //flush data
        producer.flush();

        //flush data and close
        producer.close();


    }
}
