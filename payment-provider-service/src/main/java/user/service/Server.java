package user.service;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletionStage;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import io.vavr.control.Option;
import user.service.http.rest.PaymentApi;
import user.service.persistence.CreditCardRepository;
import user.service.service.CreditCardService;

public final class Server {

    public static void main(final String[] args) {
        // Akka components
        final ActorSystem system = ActorSystem.create("payment-provider-server");
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        final Http http = Http.get(system);

        // App dependencies
        final CreditCardRepository creditCardRepository = new CreditCardRepository();
        final CreditCardService creditCardService = new CreditCardService(creditCardRepository);
        final PaymentApi paymentApi = new PaymentApi(creditCardService);

        // Akka HTTP
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = paymentApi.routes().flow(system, materializer);
        final CompletionStage<ServerBinding> futureBinding = http.bindAndHandle(
            routeFlow, ConnectHttp.toHost("localhost", 9999), materializer
        );

        futureBinding.whenComplete((nullableBinding, exception) ->
            Option.of(nullableBinding)
                .map(serverBinding -> {
                    final InetSocketAddress address = serverBinding.localAddress();
                    system.log().info("Server online at http://{}:{}/", address.getHostString(), address.getPort());
                    return serverBinding;
                })
                .orElse(() -> {
                    system.log().error("Failed to bind HTTP endpoint, terminating system", exception);
                    system.terminate();
                    return null;
                })
        );
    }
}
