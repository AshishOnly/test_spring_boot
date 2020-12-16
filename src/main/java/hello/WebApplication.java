package hello;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;


@RestController
@RequestMapping("/home")
public class WebApplication {

    @RequestMapping(method = RequestMethod.GET)
    public String sendMessage(@RequestParam("message") String message) throws IOException, ExecutionException, InterruptedException {

        String filePath = System.getenv("ConfigPath");

        Properties props = new Properties();
        props.put("bootstrap.servers", "kaas-test-ctc-a.optum.com:443");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Properties sec_props = new Properties();

        String secFilePath = Paths.get(filePath, "Producer-ssl-java.properties").toString();
        sec_props.load(new FileInputStream(secFilePath));

        sec_props.forEach((k, v) -> props.put(k, Paths.get(v.toString().replace("${path}", filePath)).toString()));

        Producer<String, String> producer = new KafkaProducer<>(props);

        long time = System.currentTimeMillis();

        ProducerRecord<String, String> record = new ProducerRecord<String, String>("kaas.eVisor.jobEvent", "test", message);

        RecordMetadata metadata = producer.send(record).get();

        long elapsedTime = System.currentTimeMillis() - time;

        String result = String.format("sent record(key=%s value=%s) " +
                        "meta(partition=%d, offset=%d) time=%d ms\n",
                record.key(), record.value(), metadata.partition(),
                metadata.offset(), elapsedTime);

        producer.close();

        return result;
    }


}