package payment.processor.service.adapter.http.user;

import java.util.concurrent.CompletionStage;
import com.fasterxml.jackson.core.JsonProcessingException;
import akka.actor.ActorSystem;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.HttpEntities;
import akka.http.javadsl.model.HttpRequest;
import akka.stream.ActorMaterializer;;
import io.vavr.control.Either;
import payment.processor.service.adapter.kafka.order.pipeline.ElementWithContext;
import payment.processor.service.adapter.http.HttpAdapter;
import payment.processor.service.adapter.http.user.dto.request.UserByEmailRequest;
import payment.processor.service.adapter.http.user.dto.response.UserByEmailResponse;
import payment.processor.service.adapter.kafka.order.model.Order;

public class UserServiceHttpAdapter extends HttpAdapter {

    private static final String ENDPOINT = "http://127.0.0.1:6666/user";

    public UserServiceHttpAdapter(final ActorSystem system, final ActorMaterializer materializer) {
        super(system, materializer);
    }

    public Either<String, ElementWithContext> transformOrder(final Order order) {
        return toJson(new UserByEmailRequest(order.getEmail()))
            .map(this::toHttpRequest)
            .map(this::sendRequest)
            .map(responseCompletionStage -> ElementWithContext.Builder.withOrder(order).withUserByEmailResponse(responseCompletionStage).build());
    }

    private CompletionStage<UserByEmailResponse> sendRequest(final HttpRequest httpRequest) {
        return httpClient.singleRequest(httpRequest)
            .thenCompose(response -> Jackson.unmarshaller(UserByEmailResponse.class).unmarshal(response.entity(), materializer));
    }

    private HttpRequest toHttpRequest(final String json) {
        return HttpRequest.GET(ENDPOINT).withEntity(HttpEntities.create(ContentTypes.APPLICATION_JSON, json));
    }

    private Either<String, String> toJson(final UserByEmailRequest userByEmailRequest) {
        try {
            return Either.right(mapper.writeValueAsString(userByEmailRequest));
        } catch (final JsonProcessingException ex) {
            return Either.left("Failed to parse UserByEmailRequest with email = " + userByEmailRequest.email + ".");
        }
    }

}
