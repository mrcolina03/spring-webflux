package espe.edu.ec.sensoresbiblioteca.model;

public class SensorStats {
    private final long totalLecturas;
    private final double temperaturaMinima;
    private final double temperaturaMaxima;
    private final double promedioGlobal;
    private final long sensoresUnicos;

    public SensorStats(long totalLecturas, double temperaturaMinima, double temperaturaMaxima, double promedioGlobal, long sensoresUnicos) {
        this.totalLecturas = totalLecturas;
        this.temperaturaMinima = temperaturaMinima;
        this.temperaturaMaxima = temperaturaMaxima;
        this.promedioGlobal = promedioGlobal;
        this.sensoresUnicos = sensoresUnicos;
    }

    public long getTotalLecturas() {
        return totalLecturas;
    }

    public double getTemperaturaMinima() {
        return temperaturaMinima;
    }

    public double getTemperaturaMaxima() {
        return temperaturaMaxima;
    }

    public double getPromedioGlobal() {
        return promedioGlobal;
    }

    public long getSensoresUnicos() {
        return sensoresUnicos;
    }
}
