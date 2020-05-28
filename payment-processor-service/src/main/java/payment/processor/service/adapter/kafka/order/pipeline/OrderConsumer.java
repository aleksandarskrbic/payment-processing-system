package payment.processor.service.adapter.kafka.order.pipeline;

import com.typesafe.config.Config;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import akka.kafka.ConsumerSettings;

public class OrderConsumer {

    public final ConsumerSettings<String, String> consumerSettings;

    public OrderConsumer(final Config config) {
        this.consumerSettings = ConsumerSettings
            .create(config.getConfig("akka.kafka.consumer"), new StringDeserializer(), new StringDeserializer())
            .withBootstrapServers("localhost:9092")
            .withGroupId("payment-processors-id")
            .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    }
}
