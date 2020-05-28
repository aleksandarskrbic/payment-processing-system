package payment.processor.service.adapter.kafka.order.pipeline;

import java.util.concurrent.CompletionStage;
import com.typesafe.config.Config;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import akka.Done;
import akka.kafka.ProducerSettings;
import akka.kafka.javadsl.Producer;
import akka.stream.javadsl.Sink;

public class OrderPipelineSink {

    public final Sink<ProducerRecord<String, String>, CompletionStage<Done>> errorSink;
    public final Sink<ProducerRecord<String, String>, CompletionStage<Done>> processedPaymentSink;

    public OrderPipelineSink(final Config config) {
        ProducerSettings<String, String> producerSettings = ProducerSettings.create(
            config.getConfig("akka.kafka.producer"),
            new StringSerializer(),
            new StringSerializer()
        ).withBootstrapServers("localhost:9092");
        this.errorSink = Producer.plainSink(producerSettings);
        this.processedPaymentSink = Producer.plainSink(producerSettings);
    }
}
