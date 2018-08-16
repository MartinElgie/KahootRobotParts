package net.nostalogic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import net.nostalogic.services.RobotPartService;
import net.nostalogic.services.RobotPartServiceImpl;

@SpringBootApplication
@EnableJpaRepositories("net.nostalogic.schema.repositories")
public class InventoryServiceApplication {

    public static void main(String... args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

    @Bean
    public RobotPartService robotPartService() {
        return new RobotPartServiceImpl();
    }
}
