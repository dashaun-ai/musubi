package ai.dashaun.musubi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MusubiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MusubiApplication.class, args);
    }
}
