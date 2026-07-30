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
                                "Shared Grafana LGTM OpenTelemetry backend", "lgtm", "observability"))
                .build();
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
