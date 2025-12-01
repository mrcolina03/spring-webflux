package espe.edu.ec.sensoresbiblioteca.controller;

import espe.edu.ec.sensoresbiblioteca.model.SensorReading;
import espe.edu.ec.sensoresbiblioteca.service.SensorService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@RestController
@RequestMapping("/api/gptcodex/sensors")
public class SensorController {
    private final SensorService sensorService;

    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    // TAREA 5: Guardar una nueva lectura con validaciones reactivas
    @PostMapping
    public Mono<SensorReading> createReading(@RequestBody SensorReading sensorReading) {
        return sensorService.save(sensorReading);
    }

    // TAREA 6 (apoyo): Devolver todas las lecturas almacenadas
    @GetMapping
    public Flux<SensorReading> getAllReadings() {
        return sensorService.getAll();
    }

    // TAREA 3: Stream SSE en caliente con metadatos
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<?>> streamReadings() {
        return sensorService.streamWithMetadata().cast(ServerSentEvent.class);
    }

    // TAREA 1: Promedio por sensor con retardo controlado
    @GetMapping("/{id}/average")
    public Mono<Double> averageBySensor(@PathVariable("id") String sensorId) {
        return sensorService.averageTemperatureBySensor(sensorId);
    }

    // TAREA 2: Generar lecturas artificiales durante 10 segundos
    @GetMapping("/generate")
    public Mono<String> generate() {
        return sensorService.generateDefaultReadings();
    }

    // TAREA 4: Estadísticas globales del sistema de sensores
    @GetMapping("/stats")
    public Mono<espe.edu.ec.sensoresbiblioteca.model.SensorStats> stats() {
        return sensorService.stats();
    }

    // TAREA 8: Stream filtrado en caliente
    @GetMapping(value = "/stream/hot", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<SensorReading>> hotStream(@RequestParam(defaultValue = "30") double threshold) {
        return sensorService.hotStreamFiltered(threshold);
    }

    // TAREA 9: Últimos N valores
    @GetMapping("/last/{n}")
    public Flux<SensorReading> lastReadings(@PathVariable int n) {
        return sensorService.lastReadings(n);
    }

    // TAREA 10: Conversión a Fahrenheit
    @GetMapping("/fahrenheit")
    public Flux<Double> fahrenheit() {
        return sensorService.temperaturesInFahrenheit();
    }

    // TAREA 11: Agrupación por sensor
    @GetMapping("/grouped")
    public Mono<java.util.Map<String, java.util.List<SensorReading>>> grouped() {
        return sensorService.groupedBySensor();
    }

    // TAREA 12: Promedio global con error controlado
    @GetMapping("/average")
    public Mono<Double> getAverageTemperature() {
        return sensorService.averageTemperature();
    }

    // TAREA 14: paginación simple
    @GetMapping("/page")
    public Flux<SensorReading> page(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "5") int size) {
        return sensorService.findPage(page, size);
    }

    // TAREA 15: sensores únicos
    @GetMapping("/unique")
    public Flux<String> unique() {
        return sensorService.uniqueSensors();
    }

    // TAREA 17: borrar lecturas
    @DeleteMapping
    public Mono<Void> deleteAll() {
        return sensorService.deleteAll();
    }

    // TAREA 18: histórico por rango de fechas
    @GetMapping("/history")
    public Flux<SensorReading> history(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        Instant fromInstant = from.toInstant(ZoneOffset.UTC);
        Instant toInstant = to.toInstant(ZoneOffset.UTC);
        return sensorService.history(fromInstant, toInstant);
    }

    // TAREA 19: alertas SSE
    @GetMapping(value = "/alerts", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<SensorReading>> alerts(@RequestParam(defaultValue = "35") double threshold) {
        return sensorService.alerts(threshold);
    }
}