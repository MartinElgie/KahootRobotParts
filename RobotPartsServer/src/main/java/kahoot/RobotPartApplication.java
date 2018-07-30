package kahoot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import kahoot.services.RobotPartService;
import kahoot.services.RobotPartServiceImpl;

@SpringBootApplication
@EnableJpaRepositories("kahoot.schema.repositories")
public class RobotPartApplication {

    public static void main(String... args) {
        SpringApplication.run(RobotPartApplication.class, args);
    }

    @Bean
    public RobotPartService robotPartService() {
        return new RobotPartServiceImpl();
    }
}
