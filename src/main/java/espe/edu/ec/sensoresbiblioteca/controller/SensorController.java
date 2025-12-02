package espe.edu.ec.sensoresbiblioteca.controller;

import espe.edu.ec.sensoresbiblioteca.model.SensorReading;
import espe.edu.ec.sensoresbiblioteca.service.SensorService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@RestController
@RequestMapping("/api/sensors")
public class SensorController {
    private final SensorService sensorService;

    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }
    //Guardar una nueva lectura
    @PostMapping
    public Mono<SensorReading> createReading(@RequestBody SensorReading sensorReading) {
        if (sensorReading.getTimestamp() == null) {
            sensorReading.setTimestamp(Instant.now());
        }
        return sensorService.save(sensorReading);
    }

    //Devolver todas las lecturas almacenadas
    @GetMapping
    public Flux<SensorReading> getAllReadings() {
        return sensorService.getAll();
    }

    //Stream de lecturas en tiempo real usando Server-Sent Events
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<SensorReading> streamReadings() {
        return sensorService.streamAll();
    }

    //Devolver el primedio de temperaturas calculadas de forma asincrona
    @GetMapping("/average")
    public Mono<Double> getAverageTemperature() {
        return sensorService.averageTemperature();
    }

    @GetMapping("/{id}/average")
    public Mono<Double> getAverageTemperatureBySensor(@PathVariable String id) {
        return sensorService.averageTemperatureBySensor(id);
    }

    @GetMapping("/generate")
    public Mono<String> generateReadings() {
        return sensorService.generateAutomaticReadings();
    }
}