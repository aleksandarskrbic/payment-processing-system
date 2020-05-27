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
import user.service.http.rest.UserApi;
import user.service.persistence.UserRepository;
import user.service.service.UserService;

public final class Server {

    public static void main(final String[] args) {
        // Akka components
        final ActorSystem system = ActorSystem.create("user-server");
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        final Http http = Http.get(system);

        // App dependencies
        final UserRepository userRepository = new UserRepository();
        final UserService userService = new UserService(userRepository);
        final UserApi userApi = new UserApi(userService);

        // Akka HTTP
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = userApi.routes().flow(system, materializer);
        final CompletionStage<ServerBinding> futureBinding = http.bindAndHandle(
            routeFlow, ConnectHttp.toHost("localhost", 6666), materializer
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
