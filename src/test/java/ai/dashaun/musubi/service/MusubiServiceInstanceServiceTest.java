package ai.dashaun.musubi.service;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceRequest;

import static ai.dashaun.musubi.catalog.MusubiCatalog.POSTGRESQL_PLAN_ID;
import static ai.dashaun.musubi.catalog.MusubiCatalog.POSTGRESQL_SERVICE_ID;
import static org.assertj.core.api.Assertions.assertThat;

class MusubiServiceInstanceServiceTest {

    @Test
    void provisioningIsIdempotent() {
        MusubiServiceInstanceService service = new MusubiServiceInstanceService();
        CreateServiceInstanceRequest request = CreateServiceInstanceRequest.builder()
                .serviceInstanceId("instance-1")
                .serviceDefinitionId(POSTGRESQL_SERVICE_ID)
                .planId(POSTGRESQL_PLAN_ID)
                .build();

        assertThat(service.createServiceInstance(request).block().isInstanceExisted()).isFalse();
        assertThat(service.createServiceInstance(request).block().isInstanceExisted()).isTrue();
    }

    @Test
    void anInstanceCanBeProvisionedAgainAfterDeletion() {
        MusubiServiceInstanceService service = new MusubiServiceInstanceService();
        CreateServiceInstanceRequest create = CreateServiceInstanceRequest.builder()
                .serviceInstanceId("instance-1")
                .serviceDefinitionId(POSTGRESQL_SERVICE_ID)
                .planId(POSTGRESQL_PLAN_ID)
                .build();
        DeleteServiceInstanceRequest delete = DeleteServiceInstanceRequest.builder()
                .serviceInstanceId("instance-1")
                .serviceDefinitionId(POSTGRESQL_SERVICE_ID)
                .planId(POSTGRESQL_PLAN_ID)
                .build();

        service.createServiceInstance(create).block();
        service.deleteServiceInstance(delete).block();

        assertThat(service.createServiceInstance(create).block().isInstanceExisted()).isFalse();
    }
}
