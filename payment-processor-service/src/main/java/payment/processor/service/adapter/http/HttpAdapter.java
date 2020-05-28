package payment.processor.service.adapter.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.stream.ActorMaterializer;

public abstract class HttpAdapter {

    protected final ActorSystem system;
    protected final ActorMaterializer materializer;
    protected final Http httpClient;
    protected final ObjectMapper mapper;

    public HttpAdapter(final ActorSystem system, final ActorMaterializer materializer) {
        this.system = system;
        this.materializer = materializer;
        this.httpClient = Http.get(system);
        this.mapper = new ObjectMapper();
    }
}
