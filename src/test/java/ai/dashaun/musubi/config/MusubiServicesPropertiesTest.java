package ai.dashaun.musubi.config;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static ai.dashaun.musubi.catalog.MusubiCatalog.RABBITMQ_SERVICE_ID;
import static org.assertj.core.api.Assertions.assertThat;

class MusubiServicesPropertiesTest {

    @Test
    void rabbitBindingIncludesTheVhostFieldExpectedByJavaCfEnv() {
        MusubiServicesProperties properties = new MusubiServicesProperties(
                new MusubiServicesProperties.PostgreSql("postgresql", 5432, "events", "kansei", "secret"),
                new MusubiServicesProperties.RabbitMq("rabbitmq", 5672, "kansei", "secret", "/"),
                new MusubiServicesProperties.Redis("redis", 6379),
                new MusubiServicesProperties.Lgtm("lgtm", 4318, 4317, "https://grafana.example"));

        Map<String, Object> credentials = properties.credentialsByServiceId().get(RABBITMQ_SERVICE_ID);

        assertThat(credentials)
                .containsEntry("vhost", "/")
                .doesNotContainKey("uri");
    }
}
