package payment.processor.service.adapter.kafka.order.pipeline;

import java.util.concurrent.CompletionStage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import akka.Done;
import akka.actor.ActorSystem;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Consumer;
import akka.stream.ActorMaterializer;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Either;
import payment.processor.service.adapter.http.payment.PaymentProviderHttpAdapter;
import payment.processor.service.adapter.http.payment.dto.response.PaymentResponse;
import payment.processor.service.adapter.http.user.UserServiceHttpAdapter;
import payment.processor.service.adapter.kafka.order.model.Order;

public final class OrderPipeline {

    private final ActorMaterializer materializer;
    private final ObjectMapper mapper;
    private final OrderConsumer orderConsumer;
    private final OrderPipelineSink orderPipelineSink;
    private final UserServiceHttpAdapter userServiceHttpAdapter;
    private final PaymentProviderHttpAdapter paymentProviderHttpAdapter;

    public OrderPipeline(
        final ActorSystem system,
        final ActorMaterializer materializer,
        final UserServiceHttpAdapter userServiceHttpAdapter,
        final PaymentProviderHttpAdapter paymentProviderHttpAdapter
    ) {
        this.materializer = materializer;
        this.userServiceHttpAdapter = userServiceHttpAdapter;
        this.paymentProviderHttpAdapter = paymentProviderHttpAdapter;
        this.mapper = new ObjectMapper();
        this.orderConsumer = new OrderConsumer(system.settings().config());
        this.orderPipelineSink = new OrderPipelineSink(system.settings().config());
    }

    public CompletionStage<Done> run() {
        return Consumer.plainSource(orderConsumer.consumerSettings, Subscriptions.topics("order"))
            .map(this::deserializeKafkaMessage)
            .divertTo(orderPipelineSink.errorSink.contramap(this::deserializationErrorMessage), Either::isLeft)
            .map(either -> userServiceHttpAdapter.transformOrder(either.get()))
            .divertTo(orderPipelineSink.errorSink.contramap(this::userServiceError), Either::isLeft)
            .mapAsyncUnordered(4, either -> paymentProviderHttpAdapter.transformPayment(either.get()))
            .divertTo(orderPipelineSink.errorSink.contramap(either -> serializationError(either.getLeft())), Either::isLeft)
            .map(either -> paymentProviderHttpAdapter.toHttpRequest(either.get()))
            .mapAsyncUnordered(4, paymentProviderHttpAdapter::sendRequest)
            .map(paymentProviderHttpAdapter::toJson)
            .divertTo(orderPipelineSink.errorSink.contramap(e -> serializationError(e.getLeft())), Either::isLeft)
            .map(Either::get)
            .map(this::toProducerRecord)
            .runWith(orderPipelineSink.processedPaymentSink, materializer);
    }

    private ProducerRecord<String, String> toProducerRecord(final Tuple2<String, PaymentResponse> response) {
        return new ProducerRecord<>("processed-order", response._2.accountId.toString(), response._1);
    }

    private Either<Tuple2<String, String>, Order> deserializeKafkaMessage(final ConsumerRecord<String, String> record) {
        try {
            return Either.right(mapper.readValue(record.value(), Order.class));
        } catch (final JsonProcessingException ex) {
            return Either.left(Tuple.of(record.key(), "Error while deserializing message with key = " + record.key()));
        }
    }

    private ProducerRecord<String, String> deserializationErrorMessage(final Either<Tuple2<String, String>, Order> error) {
        return new ProducerRecord<>("error-order", error.getLeft()._1, error.getLeft()._2);
    }

    private ProducerRecord<String, String> userServiceError(final Either<String, ElementWithContext> error) {
        return new ProducerRecord<>("error-order", error.getLeft());
    }

    private ProducerRecord<String, String> serializationError(final String error) {
        return new ProducerRecord<>("error-order", error);
    }
}
