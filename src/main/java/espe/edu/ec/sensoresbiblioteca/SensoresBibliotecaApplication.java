package espe.edu.ec.sensoresbiblioteca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import espe.edu.ec.sensoresbiblioteca.repository.SensorReadingRepository;


@SpringBootApplication
public class SensoresBibliotecaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SensoresBibliotecaApplication.class, args);
    }

    @Bean
    public SensorReadingRepository sensorReadingRepository() {
        return new SensorReadingRepository();
    }
}
