package espe.edu.ec.sensoresbiblioteca.handler;

import espe.edu.ec.sensoresbiblioteca.model.SensorReading;
import espe.edu.ec.sensoresbiblioteca.service.SensorService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class SensorFunctionalHandler {
    private final SensorService sensorService;

    public SensorFunctionalHandler(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    // TAREA 6: handler funcional findAllHandler
    public Mono<ServerResponse> findAll(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(sensorService.getAll(), SensorReading.class);
    }

    // TAREA 20: POST funcional
    public Mono<ServerResponse> save(ServerRequest request) {
        return request.bodyToMono(SensorReading.class)
                .flatMap(sensorService::save)
                .flatMap(saved -> ServerResponse.created(request.uri()).bodyValue(saved));
    }
}
