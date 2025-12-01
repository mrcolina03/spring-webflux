package espe.edu.ec.sensoresbiblioteca.service;

import espe.edu.ec.sensoresbiblioteca.model.SensorReading;
import espe.edu.ec.sensoresbiblioteca.repository.SensorReadingRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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
}