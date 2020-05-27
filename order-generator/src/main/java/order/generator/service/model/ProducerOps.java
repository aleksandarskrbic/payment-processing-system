package order.generator.service.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import io.vavr.control.Try;

public abstract class ProducerOps {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static Try<String> toJson(final Order order) {
        return Try.of(() -> mapper.writeValueAsString(order));
    }

    public static ProducerRecord<String, String> toProducerRecord(final String orderJson) {
        return new ProducerRecord<>("order", orderJson);
    }
}
