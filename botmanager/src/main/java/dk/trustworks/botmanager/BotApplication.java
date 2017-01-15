package dk.trustworks.botmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by hans on 06/01/2017.
 */
@EnableScheduling
@SpringBootApplication(scanBasePackages = {"me.ramswaroop.jbot", "dk.trustworks.botmanager"})
public class BotApplication {
    public static void main(String[] args) {
        SpringApplication.run(BotApplication.class, args);
    }
}
