package espe.edu.ec.sensoresbiblioteca.model;

import java.time.Instant;

public class SensorEvent {
    private final String sseId;
    private final long sequence;
    private final Instant emittedAt;
    private final SensorReading payload;

    public SensorEvent(String sseId, long sequence, Instant emittedAt, SensorReading payload) {
        this.sseId = sseId;
        this.sequence = sequence;
        this.emittedAt = emittedAt;
        this.payload = payload;
    }

    public String getSseId() {
        return sseId;
    }

    public long getSequence() {
        return sequence;
    }

    public Instant getEmittedAt() {
        return emittedAt;
    }

    public SensorReading getPayload() {
        return payload;
    }
}
