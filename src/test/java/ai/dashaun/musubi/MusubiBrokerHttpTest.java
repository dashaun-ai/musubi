package ai.dashaun.musubi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "musubi.broker.username=musubi",
                "musubi.broker.password=musubi-dev-secret"
        })
class MusubiBrokerHttpTest {

    @LocalServerPort
    int port;

    @Test
    void catalogRequiresBrokerAuthentication() {
        HttpStatus status = RestClient.create().get()
                .uri("http://localhost:" + port + "/v2/catalog")
                .header("X-Broker-API-Version", "2.15")
                .exchange((request, response) -> HttpStatus.valueOf(response.getStatusCode().value()));

        assertThat(status).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void authenticatedCatalogAdvertisesAllOfferings() {
        String body = RestClient.builder()
                .defaultHeaders(headers -> headers.setBasicAuth("musubi", "musubi-dev-secret"))
                .build()
                .get()
                .uri("http://localhost:" + port + "/v2/catalog")
                .header("X-Broker-API-Version", "2.15")
                .retrieve()
                .body(String.class);

        assertThat(body)
                .contains("\"name\":\"postgresql\"")
                .contains("\"name\":\"rabbitmq\"")
                .contains("\"name\":\"redis\"")
                .contains("\"name\":\"grafana-lgtm\"");
    }
}
