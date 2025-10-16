package it.paoloadesso.gestionetavoli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GestioneTavoliApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestioneTavoliApplication.class, args);
    }

}
