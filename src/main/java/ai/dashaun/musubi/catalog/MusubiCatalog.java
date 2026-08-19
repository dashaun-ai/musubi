package ai.dashaun.musubi.catalog;

import org.springframework.cloud.servicebroker.model.catalog.Catalog;
import org.springframework.cloud.servicebroker.model.catalog.Plan;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration(proxyBeanMethods = false)
public class MusubiCatalog {

    public static final String POSTGRESQL_SERVICE_ID = "accd69e5-4e75-40f1-bb3d-d86b479b42a4";
    public static final String POSTGRESQL_PLAN_ID = "c19c39b8-6dcc-4208-81dd-e9bda6ce7379";
    public static final String RABBITMQ_SERVICE_ID = "b6ce1da3-86d5-402e-92a7-6f40c087ab8e";
    public static final String RABBITMQ_PLAN_ID = "944c5ca7-2a06-412b-9cb3-b2bf879e9633";
    public static final String REDIS_SERVICE_ID = "95fc922f-4b7d-4fb4-921e-02ba8139b90f";
    public static final String REDIS_PLAN_ID = "7aba5a6f-42a2-4fd2-aca5-1fd739e47190";
    public static final String LGTM_SERVICE_ID = "881c8337-663f-46ac-88fd-b250c136cd6f";
    public static final String LGTM_PLAN_ID = "ad1ce5ff-de74-493c-b0b1-653fe368bc23";
    public static final String GARAGE_SERVICE_ID = "0d710b3a-7c33-4f0a-9804-f62dbc9797c9";
    public static final String GARAGE_RUN_ARTIFACTS_PLAN_ID = "5376aebd-5db3-49ec-bada-b3e896011b81";
    public static final String GARAGE_WORK_ARTIFACTS_PLAN_ID = "1edcf2ef-e477-4428-a1da-a4cd1c743bbe";
    public static final String GARAGE_BROWSER_EVIDENCE_PLAN_ID = "2889a6ee-bceb-494c-a1c2-289dc81303cd";
    public static final String GARAGE_ENCRYPTED_BACKUPS_PLAN_ID = "46d72de0-0bd6-4e22-b9b3-41ab56af053c";

    @Bean
    public Catalog catalog() {
        return Catalog.builder()
                .serviceDefinitions(
                        service(POSTGRESQL_SERVICE_ID, POSTGRESQL_PLAN_ID, "postgresql",
                                "PostgreSQL for Kansei application state", "postgresql", "relational"),
                        service(RABBITMQ_SERVICE_ID, RABBITMQ_PLAN_ID, "rabbitmq",
                                "RabbitMQ for Kansei activity events", "rabbitmq", "messaging"),
                        service(REDIS_SERVICE_ID, REDIS_PLAN_ID, "redis",
                                "Redis Stack for Kansei memory and vector search", "redis", "key-value"),
                        service(LGTM_SERVICE_ID, LGTM_PLAN_ID, "grafana-lgtm",
                                "Shared Grafana LGTM OpenTelemetry backend", "lgtm", "observability"),
                        garage())
                .build();
    }

    private static ServiceDefinition garage() {
        return ServiceDefinition.builder().id(GARAGE_SERVICE_ID).name("garage")
                .description("Garage S3-compatible encrypted artifact storage").bindable(true)
                .tags("garage", "s3", "object-storage")
                .plans(garagePlan(GARAGE_RUN_ARTIFACTS_PLAN_ID, "run-artifacts", "90-day run artifacts"),
                        garagePlan(GARAGE_WORK_ARTIFACTS_PLAN_ID, "work-artifacts", "Encrypted Work review diffs"),
                        garagePlan(GARAGE_BROWSER_EVIDENCE_PLAN_ID, "browser-evidence", "Seven-day browser evidence"),
                        garagePlan(GARAGE_ENCRYPTED_BACKUPS_PLAN_ID, "encrypted-backups", "Encrypted recovery backups"))
                .build();
    }

    private static Plan garagePlan(String id, String name, String description) {
        return Plan.builder().id(id).name(name).description(description).free(true).build();
    }

    private static ServiceDefinition service(
            String serviceId, String planId, String name, String description, String... tags) {
        Plan plan = Plan.builder()
                .id(planId)
                .name("dev")
                .description("Shared local-development plan")
                .free(true)
                .build();
        return ServiceDefinition.builder()
                .id(serviceId)
                .name(name)
                .description(description)
                .bindable(true)
                .tags(List.of(tags))
                .plans(plan)
                .build();
    }
}
