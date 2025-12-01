package espe.edu.ec.sensoresbiblioteca.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class ElapsedTimeFilter implements WebFilter {
    private static final Logger log = LoggerFactory.getLogger(ElapsedTimeFilter.class);
    private static final String PATH_PREFIX = "/api/gptcodex/sensors";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if (!request.getPath().value().startsWith(PATH_PREFIX)) {
            return chain.filter(exchange);
        }
        long start = System.currentTimeMillis();
        exchange.getResponse().beforeCommit(() -> {
            long elapsed = System.currentTimeMillis() - start;
            exchange.getResponse().getHeaders().add("X-Elapsed-Time-Reactive", String.valueOf(elapsed));
            return Mono.empty();
        });
        return chain.filter(exchange)
                .doFinally(signalType -> {
                    long elapsed = System.currentTimeMillis() - start;
                    log.info("[REACTIVE] {} {} -> {} en {} ms", request.getMethod(), request.getURI().getPath(), exchange.getResponse().getStatusCode(), elapsed);
                });
    }
}
