package espe.edu.ec.sensoresbiblioteca.service;

import espe.edu.ec.sensoresbiblioteca.exception.SensorNotFoundException;
import espe.edu.ec.sensoresbiblioteca.exception.ValidationException;
import espe.edu.ec.sensoresbiblioteca.model.SensorEvent;
import espe.edu.ec.sensoresbiblioteca.model.SensorReading;
import espe.edu.ec.sensoresbiblioteca.model.SensorStats;
import espe.edu.ec.sensoresbiblioteca.repository.SensorReadingRepository;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class SensorService {
    private static final double MIN_TEMP = -50;
    private static final double MAX_TEMP = 100;
    private final SensorReadingRepository repository;

    public SensorService(SensorReadingRepository repository){
        this.repository = repository;
    }

    // TAREA 5: validaciones al guardar
    public Mono<SensorReading> save(SensorReading sensorReading){
        return Mono.just(sensorReading)
                .flatMap(reading -> {
                    if (reading.getId() == null || reading.getId().isBlank()) {
                        return Mono.error(new ValidationException("El id del sensor no puede ser vacío"));
                    }
                    if (reading.getTemperature() < MIN_TEMP || reading.getTemperature() > MAX_TEMP) {
                        return Mono.error(new ValidationException("Temperatura fuera de rango permitido (-50 a 100)"));
                    }
                    if (reading.getTimestamp() == null) {
                        reading.setTimestamp(Instant.now());
                    }
                    repository.save(reading);
                    return Mono.just(reading);
                });
    }

    public Flux<SensorReading> getAll(){
        return repository.findAll();
    }

    public Flux<SensorReading> streamAll(){
        return repository.streamAll();
    }

    // TAREA 13: cálculo de promedio global no bloqueante
    public Mono<Double> averageTemperature(){
        return repository.findAll()
                .map(SensorReading::getTemperature)
                .collectList()
                .flatMap(list -> {
                    if (list.isEmpty()) {
                        return Mono.error(new SensorNotFoundException("No existen lecturas para calcular el promedio global"));
                    }
                    return Mono.fromCallable(() -> {
                        Thread.sleep(1000); // Simula una operación costosa de forma diferida
                        return list.stream()
                                .mapToDouble(Double::doubleValue)
                                .average()
                                .orElse(Double.NaN);
                    }).subscribeOn(Schedulers.boundedElastic());
                });
    }

    // TAREA 1: promedio por sensor específico con retardo no bloqueante
    public Mono<Double> averageTemperatureBySensor(String sensorId){
        return repository.findAll()
                .filter(reading -> reading.getId().equals(sensorId))
                .map(SensorReading::getTemperature)
                .collectList()
                .flatMap(list -> {
                    if (list.isEmpty()) {
                        return Mono.error(new SensorNotFoundException("No existen lecturas para el sensor " + sensorId));
                    }
                    double average = list.stream().mapToDouble(Double::doubleValue).average().orElse(Double.NaN);
                    return Mono.just(average)
                            .delayElement(Duration.ofSeconds(1))
                            .subscribeOn(Schedulers.parallel());
                });
    }

    // TAREA 2: generador de lecturas artificiales
    public Mono<String> generateReadings(Duration interval, long totalTicks){
        return Flux.interval(interval)
                .take(totalTicks)
                .map(tick -> new SensorReading("sensor-" + tick % 3,
                        15 + (tick % 10),
                        Instant.now()))
                .doOnNext(repository::save)
                .then(Mono.just("Generación completada"));
    }

    public Mono<String> generateDefaultReadings(){
        return generateReadings(Duration.ofSeconds(1), 10);
    }

    // TAREA 3: stream SSE con metadatos adicionales
    public Flux<ServerSentEvent<SensorEvent>> streamWithMetadata(){
        AtomicLong counter = new AtomicLong();
        return repository.streamAll()
                .map(reading -> new SensorEvent(UUID.randomUUID().toString(), counter.incrementAndGet(), Instant.now(), reading))
                .map(event -> ServerSentEvent.<SensorEvent>builder()
                        .id(event.getSseId())
                        .event("sensor-reading")
                        .data(event)
                        .build());
    }

    // TAREA 4: estadísticas globales
    public Mono<SensorStats> stats(){
        Flux<SensorReading> cached = repository.findAll().cache();
        Mono<Long> total = cached.count();
        Mono<Double> min = cached.map(SensorReading::getTemperature).reduce(Double::min);
        Mono<Double> max = cached.map(SensorReading::getTemperature).reduce(Double::max);
        Mono<Double> avg = cached.map(SensorReading::getTemperature)
                .collectList()
                .flatMap(list -> {
                    if (list.isEmpty()) {
                        return Mono.error(new SensorNotFoundException("No existen lecturas para calcular estadísticas"));
                    }
                    return Mono.just(list.stream().mapToDouble(Double::doubleValue).average().orElse(Double.NaN));
                });
        Mono<Long> uniqueSensors = cached.map(SensorReading::getId).distinct().count();

        return Mono.zip(total, min.defaultIfEmpty(Double.NaN), max.defaultIfEmpty(Double.NaN), avg, uniqueSensors)
                .map(tuple -> new SensorStats(tuple.getT1(), tuple.getT2(), tuple.getT3(), tuple.getT4(), tuple.getT5()));
    }

    // TAREA 8: stream filtrado en caliente
    public Flux<ServerSentEvent<SensorReading>> hotStreamFiltered(double threshold){
        return repository.streamAll()
                .filter(reading -> reading.getTemperature() > threshold)
                .map(reading -> ServerSentEvent.<SensorReading>builder()
                        .event("hot-reading")
                        .id(reading.getId() + "-" + reading.getTimestamp())
                        .data(reading)
                        .build());
    }

    // TAREA 9: últimas N lecturas
    public Flux<SensorReading> lastReadings(int n){
        return repository.findAll()
                .takeLast(n);
    }

    // TAREA 10: temperaturas en Fahrenheit
    public Flux<Double> temperaturesInFahrenheit(){
        return repository.findAll()
                .map(SensorReading::getTemperature)
                .map(celsius -> celsius * 9 / 5 + 32);
    }

    // TAREA 11: agrupación por sensor
    public Mono<Map<String, java.util.List<SensorReading>>> groupedBySensor(){
        return repository.findAll()
                .collectMultimap(SensorReading::getId)
                .map(multimap -> multimap.entrySet()
                        .stream()
                        .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, entry -> List.copyOf(entry.getValue()))));
    }

    // TAREA 14: paginación simple
    public Flux<SensorReading> findPage(int page, int size){
        int skip = Math.max(page, 0) * Math.max(size, 0);
        return repository.findAll()
                .skip(skip)
                .take(size);
    }

    // TAREA 15: sensores únicos
    public Flux<String> uniqueSensors(){
        return repository.findAll()
                .map(SensorReading::getId)
                .distinct();
    }

    // TAREA 17: borrar lecturas
    public Mono<Void> deleteAll(){
        return repository.clear();
    }

    // TAREA 18: histórico por rango de fechas
    public Flux<SensorReading> history(Instant from, Instant to){
        return repository.findAll()
                .filter(reading -> !reading.getTimestamp().isBefore(from) && !reading.getTimestamp().isAfter(to));
    }

    // TAREA 19: alertas en caliente
    public Flux<ServerSentEvent<SensorReading>> alerts(double threshold){
        return repository.streamAll()
                .filter(reading -> reading.getTemperature() > threshold)
                .map(reading -> ServerSentEvent.<SensorReading>builder()
                        .event("alert")
                        .id(UUID.randomUUID().toString())
                        .data(reading)
                        .build());
    }
}