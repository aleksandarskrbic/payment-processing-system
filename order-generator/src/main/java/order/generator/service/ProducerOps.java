package order.generator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import io.vavr.control.Try;
import order.generator.service.model.Order;

public abstract class ProducerOps {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static Try<String> toJson(final Order order) {
        return Try.of(() -> mapper.writeValueAsString(order));
    }

    public static ProducerRecord<String, String> toProducerRecord(final String orderJson) {
        return new ProducerRecord<>("order", orderJson);
    }
}
