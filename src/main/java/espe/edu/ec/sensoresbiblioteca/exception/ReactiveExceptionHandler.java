package espe.edu.ec.sensoresbiblioteca.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import espe.edu.ec.sensoresbiblioteca.model.ApiError;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
public class ReactiveExceptionHandler implements WebExceptionHandler {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status;
        if (ex instanceof ValidationException) {
            status = HttpStatus.BAD_REQUEST;
        } else if (ex instanceof SensorNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        ApiError body = new ApiError(status.value(), ex.getMessage(), exchange.getRequest().getPath().value());
        try {
            byte[] bytes = mapper.writeValueAsBytes(body);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (Exception e) {
            return exchange.getResponse().setComplete();
        }
    }
}
