package ai.dashaun.musubi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("musubi.broker")
public record MusubiBrokerProperties(String username, String password) {
}
