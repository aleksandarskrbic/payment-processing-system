package order.generator.service;

import java.util.concurrent.CompletionStage;
import com.typesafe.config.Config;
import org.apache.kafka.common.serialization.StringSerializer;
import akka.Done;
import akka.actor.ActorSystem;
import akka.kafka.ProducerSettings;
import akka.kafka.javadsl.Producer;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Source;
import io.vavr.collection.List;
import io.vavr.control.Try;
import order.generator.service.model.Order;

public final class Application {

    public static void main(final String[] args) {
        final ActorSystem system = ActorSystem.create("order-generator");
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        final Config config = system.settings().config().getConfig("akka.kafka.producer");
        final ProducerSettings<String, String> producerSettings =ProducerSettings.create(
            config,
            new StringSerializer(),
            new StringSerializer()
        ).withBootstrapServers("localhost:9092");

        final CompletionStage<Done> completion = Source.from(orders)
            .map(ProducerOps::toJson)
            .filter(Try::isSuccess)
            .map(Try::get)
            .map(ProducerOps::toProducerRecord)
            .runWith(Producer.plainSink(producerSettings), materializer);


        completion.whenComplete((done, ex) -> system.terminate());
    }


    private static List<Order> orders = List.of(
        new Order("johh.doe@gmail.com", 100d),
        new Order("johh.doe@gmail.com", 10d),
        new Order("jane.doe@gmail.com", 100d),
        new Order("jane.doe@gmail.com", 350d),
        new Order("jack.sparrow@yahoo.com", 550d)
    );
}
