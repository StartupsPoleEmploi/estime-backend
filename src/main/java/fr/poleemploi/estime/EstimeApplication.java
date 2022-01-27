package fr.poleemploi.estime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@ComponentScan({"fr.poleemploi.estime"})
@EntityScan({"fr.poleemploi.estime.donnees.entities"})
@EnableJpaRepositories({"fr.poleemploi.estime.donnees.repositories.jpa"})
@EnableRetry
public class EstimeApplication {

    public static void main(String[] args) {
	SpringApplication.run(EstimeApplication.class, args);
    }
}
