package espe.edu.ec.sensoresbiblioteca.model;

import java.time.Instant;

public class ApiError {
    private final Instant timestamp = Instant.now();
    private final int status;
    private final String error;
    private final String path;

    public ApiError(int status, String error, String path) {
        this.status = status;
        this.error = error;
        this.path = path;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getPath() {
        return path;
    }
}
