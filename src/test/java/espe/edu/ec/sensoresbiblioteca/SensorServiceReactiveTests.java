package espe.edu.ec.sensoresbiblioteca;

import espe.edu.ec.sensoresbiblioteca.model.SensorReading;
import espe.edu.ec.sensoresbiblioteca.repository.SensorReadingRepository;
import espe.edu.ec.sensoresbiblioteca.service.SensorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.Instant;

class SensorServiceReactiveTests {

    private SensorReadingRepository repository;
    private SensorService service;

    @BeforeEach
    void setUp() {
        repository = new SensorReadingRepository();
        service = new SensorService(repository);
    }

    // TAREA 16: verificar promedio por sensor
    @Test
    void averageBySensorShouldReturnExpectedValue() {
        repository.save(new SensorReading("sensor-1", 10, Instant.now()));
        repository.save(new SensorReading("sensor-1", 20, Instant.now()));

        StepVerifier.create(service.averageTemperatureBySensor("sensor-1"))
                .thenAwait(Duration.ofSeconds(2))
                .expectNext(15.0)
                .verifyComplete();
    }

    // TAREA 16: verificar generación de lecturas
    @Test
    void generateReadingsShouldEmit() {
        StepVerifier.withVirtualTime(() -> service.generateReadings(Duration.ofSeconds(1), 3))
                .thenAwait(Duration.ofSeconds(3))
                .expectNext("Generación completada")
                .verifyComplete();
    }

    // TAREA 16: verificar que el stream SSE emite datos
    @Test
    void streamWithMetadataShouldEmitEvents() {
        StepVerifier.create(service.streamWithMetadata().next())
                .then(() -> repository.save(new SensorReading("sensor-2", 25, Instant.now())))
                .assertNext(event -> {
                    // Comentario TAREA 16: validamos metadatos
                    assert event.getData() != null;
                })
                .verifyComplete();
    }
}
