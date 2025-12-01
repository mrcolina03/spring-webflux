package espe.edu.ec.sensoresbiblioteca.config;

import espe.edu.ec.sensoresbiblioteca.handler.SensorFunctionalHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class SensorRouterConfig {

    // TAREA 6 y 20: definición de rutas funcionales
    @Bean
    public RouterFunction<ServerResponse> sensorRoutes(SensorFunctionalHandler handler) {
        return RouterFunctions.route()
                .GET("/api/gptcodex/sensors-functional", handler::findAll)
                .POST("/api/gptcodex/sensors-functional", handler::save)
                .build();
    }
}
