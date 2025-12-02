package espe.edu.ec.sensoresbiblioteca.service;

import espe.edu.ec.sensoresbiblioteca.model.SensorReading;
import espe.edu.ec.sensoresbiblioteca.repository.SensorReadingRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;

@Service
public class SensorService {
    private final SensorReadingRepository repository;

    public SensorService(SensorReadingRepository repository){
        this.repository = repository;
    }

    // Guarda una lectura y devuelve un Mono con la lectura guardada
    public Mono<SensorReading> save(SensorReading sensorReading){
        repository.save(sensorReading);
        return Mono.just(sensorReading);
    }

    // Devuelve todas las lecturas como un Flux
    public Flux<SensorReading> getAll(){
        return repository.findAll();
    }

    // Devuelve un stream de lecturas donde cada nueva lectura se emitirá
    public Flux<SensorReading> streamAll(){
        return repository.streamAll();
    }

    // Calcula el promedio de temperaturas de forma asíncrona
    public Mono<Double> averageTemperature(){
        return repository.findAll()
                .map(SensorReading::getTemperature)
                .collectList()
                .flatMap(list -> Mono.fromCallable(() -> {
                    Thread.sleep(1000); // Simula una operación costosa

                    return list.stream()
                            .mapToDouble(Double::doubleValue)
                            .average()
                            .orElse(Double.NaN);
                }).subscribeOn(Schedulers.boundedElastic()));
    }


    public Mono<Double> averageTemperatureBySensor(String sensorId) {
        return repository.findAll()
                .filter(reading -> reading.getId().equals(sensorId)) // Filtra por ID del sensor
                .map(SensorReading::getTemperature) // Extrae solo las temperaturas
                .collectList() // Convierte el Flux en una lista
                .flatMap(list -> {
                    if (list.isEmpty()) {
                        return Mono.just(Double.NaN); // Si no hay lecturas, retorna NaN
                    }
                    // Operación asíncrona en hilo elástico
                    return Mono.fromCallable(() -> {
                        Thread.sleep(2000); // Simula operación costosa de 2 segundos

                        // Calcula el promedio
                        return list.stream()
                                .mapToDouble(Double::doubleValue)
                                .average()
                                .orElse(Double.NaN);
                    }).subscribeOn(Schedulers.boundedElastic()); // No bloquea el event-loop
                });
    }

    public Mono<String> generateAutomaticReadings() {
        // Array de IDs de sensores para simular diferentes sensores
        String[] sensorIds = {"sensor-1", "sensor-2", "sensor-3", "sensor-4"};

        return Flux.interval(java.time.Duration.ofSeconds(1)) // Emite cada 1 segundo
                .take(10) // Solo 10 emisiones (10 segundos)
                .map(i -> {
                    // Genera una lectura aleatoria
                    String sensorId = sensorIds[(int) (i % sensorIds.length)];
                    double temperature = 15.0 + (Math.random() * 15.0); // Temp entre 15°C y 30°C
                    Instant timestamp = Instant.now();

                    SensorReading reading = new SensorReading(sensorId, temperature, timestamp);

                    // Guarda la lectura (esto automáticamente la emite al stream)
                    repository.save(reading);

                    return reading;
                })
                .then(Mono.just("Generación de lecturas completada. Se generaron 10 lecturas automáticas."))
                .subscribeOn(Schedulers.boundedElastic()); // Ejecuta en hilo separado
    }
}