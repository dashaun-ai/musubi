package ai.dashaun.musubi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

import static ai.dashaun.musubi.catalog.MusubiCatalog.LGTM_SERVICE_ID;
import static ai.dashaun.musubi.catalog.MusubiCatalog.POSTGRESQL_SERVICE_ID;
import static ai.dashaun.musubi.catalog.MusubiCatalog.RABBITMQ_SERVICE_ID;
import static ai.dashaun.musubi.catalog.MusubiCatalog.REDIS_SERVICE_ID;
import static ai.dashaun.musubi.catalog.MusubiCatalog.*;

@ConfigurationProperties("musubi.services")
public record MusubiServicesProperties(
        PostgreSql postgresql,
        RabbitMq rabbitmq,
        Redis redis,
        Lgtm lgtm,
        Garage garage) {

    public Map<String, Map<String, Object>> credentialsByServiceId() {
        var credentials = new java.util.HashMap<String, Map<String,Object>>();
        credentials.put(POSTGRESQL_SERVICE_ID, postgresql.credentials());
        credentials.put(RABBITMQ_SERVICE_ID, rabbitmq.credentials());
        credentials.put(REDIS_SERVICE_ID, redis.credentials());
        credentials.put(LGTM_SERVICE_ID, lgtm.credentials());
        if (garage != null) {
            credentials.put(GARAGE_SERVICE_ID + ":" + GARAGE_RUN_ARTIFACTS_PLAN_ID, garage.credentials(garage.runArtifacts()));
            credentials.put(GARAGE_SERVICE_ID + ":" + GARAGE_BROWSER_EVIDENCE_PLAN_ID, garage.credentials(garage.browserEvidence()));
            credentials.put(GARAGE_SERVICE_ID + ":" + GARAGE_ENCRYPTED_BACKUPS_PLAN_ID, garage.credentials(garage.encryptedBackups()));
        }
        return Map.copyOf(credentials);
    }

    public record PostgreSql(String host, int port, String database, String username, String password) {
        Map<String, Object> credentials() {
            String uri = "postgresql://" + username + ":" + password + "@" + host + ":" + port + "/" + database;
            return Map.of(
                    "host", host,
                    "port", port,
                    "database", database,
                    "username", username,
                    "password", password,
                    "uri", uri,
                    "jdbcUrl", "jdbc:postgresql://" + host + ":" + port + "/" + database);
        }
    }

    public record RabbitMq(String host, int port, String username, String password, String virtualHost) {
        Map<String, Object> credentials() {
            return Map.of(
                    "host", host,
                    "hostname", host,
                    "port", port,
                    "username", username,
                    "password", password,
                    "vhost", virtualHost,
                    "virtualHost", virtualHost);
        }
    }

    public record Redis(String host, int port) {
        Map<String, Object> credentials() {
            return Map.of("host", host, "port", port, "uri", "redis://" + host + ":" + port);
        }
    }

    public record Lgtm(String host, int otlpHttpPort, int otlpGrpcPort, String grafanaUrl) {
        Map<String, Object> credentials() {
            return Map.of(
                    "host", host,
                    "otlpHttpEndpoint", "http://" + host + ":" + otlpHttpPort,
                    "otlpGrpcEndpoint", "http://" + host + ":" + otlpGrpcPort,
                    "grafanaUrl", grafanaUrl);
        }
    }

    public record Garage(String endpoint, String region, Bucket runArtifacts, Bucket browserEvidence,
                         Bucket encryptedBackups) {
        Map<String,Object> credentials(Bucket bucket) {
            return Map.of("endpoint", endpoint, "region", region, "bucket", bucket.name(),
                    "accessKeyId", bucket.accessKeyId(), "secretAccessKey", bucket.secretAccessKey(),
                    "pathStyleAccess", true);
        }
    }

    public record Bucket(String name, String accessKeyId, String secretAccessKey) { }
}
