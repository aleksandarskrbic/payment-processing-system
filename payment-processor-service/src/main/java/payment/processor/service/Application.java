package payment.processor.service;

import java.util.concurrent.CompletionStage;
import com.fasterxml.jackson.core.JsonProcessingException;
import akka.Done;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;;
import payment.processor.service.adapter.http.payment.PaymentProviderHttpAdapter;
import payment.processor.service.adapter.http.user.UserServiceHttpAdapter;
import payment.processor.service.adapter.kafka.order.pipeline.OrderPipeline;

public class Application {

    public static void main(final String[] args) throws JsonProcessingException {
        final ActorSystem system = ActorSystem.create("payment-processor-server");
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        final UserServiceHttpAdapter userServiceHttpAdapter = new UserServiceHttpAdapter(system, materializer);
        final PaymentProviderHttpAdapter paymentProviderHttpAdapter = new PaymentProviderHttpAdapter(system, materializer);

        final OrderPipeline orderPipeline = new OrderPipeline(system, materializer, userServiceHttpAdapter, paymentProviderHttpAdapter);

        final CompletionStage<Done> control = orderPipeline.run();

        control.whenComplete((done, ex) -> system.terminate());
    }

}
