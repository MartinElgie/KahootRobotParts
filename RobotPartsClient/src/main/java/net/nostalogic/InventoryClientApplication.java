package net.nostalogic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import net.nostalogic.services.WebClientService;

@SpringBootApplication
public class InventoryClientApplication {

    public static void main(String... args) {
        ConfigurableApplicationContext run = SpringApplication.run(InventoryClientApplication.class, args);
        run.getBean(WebClientService.class).beginClient();
    }

}
