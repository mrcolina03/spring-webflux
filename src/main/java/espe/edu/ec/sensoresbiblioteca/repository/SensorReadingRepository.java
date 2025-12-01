package espe.edu.ec.sensoresbiblioteca.repository;

import espe.edu.ec.sensoresbiblioteca.model.SensorReading;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SensorReadingRepository {

    //Creamos la lista
    private final List<SensorReading> storage = new CopyOnWriteArrayList<>();

    //Creamos el set
    //Aki se aplica el consepto de fluido
    private final Sinks.Many<SensorReading> sink = Sinks.many().multicast().onBackpressureBuffer();


    //Guarda una lectura y la emite al stream
    public void save(SensorReading reading){
        storage.add(reading);//guarda la lectura en la lista
        sink.tryEmitNext(reading);//emite la lectura al stream

    }

    //Devuelve todas las lecturas como un flux
    public Flux<SensorReading> findAll(){
        return Flux.fromIterable(storage);//devuelve la lista como un flux de forma iterativa
    }


    //Devuelve un stream de lecturas donde cada nueva lectura de va a emitir

    public Flux<SensorReading> streamAll(){
        return sink.asFlux();//devuelve el stream de lecturas
}
}
