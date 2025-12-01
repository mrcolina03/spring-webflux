package espe.edu.ec.sensoresbiblioteca.model;

import java.time.Instant;

public class SensorReading {
    private String id;
    private double temperature;
    private Instant timestamp;

    //constructor vacío, lleno y getter y setter
    public SensorReading() {
    }

    public SensorReading(String id, double temperature, Instant timestamp) {
        this.id = id;
        this.temperature = temperature;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
    
}

