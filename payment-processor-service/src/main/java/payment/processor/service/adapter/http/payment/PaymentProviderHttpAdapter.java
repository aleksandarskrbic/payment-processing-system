package payment.processor.service.adapter.http.payment;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import com.fasterxml.jackson.core.JsonProcessingException;
import akka.actor.ActorSystem;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.HttpEntities;
import akka.http.javadsl.model.HttpRequest;
import akka.stream.ActorMaterializer;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Either;
import payment.processor.service.adapter.kafka.order.pipeline.ElementWithContext;
import payment.processor.service.adapter.http.HttpAdapter;
import payment.processor.service.adapter.http.payment.dto.request.PaymentRequest;
import payment.processor.service.adapter.http.payment.dto.response.PaymentResponse;

public class PaymentProviderHttpAdapter extends HttpAdapter {

    private static final String ENDPOINT = "http://127.0.0.1:9999/payment";

    public PaymentProviderHttpAdapter(final ActorSystem system, final ActorMaterializer materializer) {
        super(system, materializer);
    }

    public CompletionStage<Either<String, String>> transformPayment(final ElementWithContext elementWithContext) {
        return elementWithContext.userByEmailResponse.thenCompose(
            userByEmailResponse -> CompletableFuture.completedFuture(
                toJson(new PaymentRequest(userByEmailResponse.accountId, elementWithContext.order.getAmount()))
            )
        );
    }

    public CompletionStage<PaymentResponse> sendRequest(final HttpRequest httpRequest) {
        return httpClient.singleRequest(httpRequest)
            .thenCompose(response -> Jackson.unmarshaller(PaymentResponse.class).unmarshal(response.entity(), materializer));
    }

    public HttpRequest toHttpRequest(final String json) {
        return HttpRequest.GET(ENDPOINT).withEntity(HttpEntities.create(ContentTypes.APPLICATION_JSON, json));
    }

    public Either<String, Tuple2<String, PaymentResponse>> toJson(final PaymentResponse paymentResponse) {
        try {
            return Either.right(Tuple.of(mapper.writeValueAsString(paymentResponse), paymentResponse));
        } catch (final JsonProcessingException ex) {
            return Either.left("Failed to parse PaymentRequest with accountId = " + paymentResponse.accountId + ".");
        }
    }

    private Either<String, String> toJson(final PaymentRequest paymentRequest) {
        try {
            return Either.right(mapper.writeValueAsString(paymentRequest));
        } catch (final JsonProcessingException ex) {
            return Either.left("Failed to parse PaymentRequest with accountId = " + paymentRequest.accountId + ".");
        }
    }
}
