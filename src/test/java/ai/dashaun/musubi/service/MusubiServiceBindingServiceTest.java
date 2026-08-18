package ai.dashaun.musubi.service;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceAppBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingRequest;

import java.util.Map;

import static ai.dashaun.musubi.catalog.MusubiCatalog.POSTGRESQL_PLAN_ID;
import static ai.dashaun.musubi.catalog.MusubiCatalog.POSTGRESQL_SERVICE_ID;
import static ai.dashaun.musubi.catalog.MusubiCatalog.GARAGE_BROWSER_EVIDENCE_PLAN_ID;
import static ai.dashaun.musubi.catalog.MusubiCatalog.GARAGE_SERVICE_ID;
import static org.assertj.core.api.Assertions.assertThat;

class MusubiServiceBindingServiceTest {

    @Test
    void returnsTheCredentialsForTheRequestedOffering() {
        Map<String, Object> postgres = Map.of(
                "host", "postgresql.kansei-services.svc.cluster.local",
                "port", 5432,
                "username", "kansei",
                "password", "secret");
        MusubiServiceBindingService service = new MusubiServiceBindingService(
                Map.of(POSTGRESQL_SERVICE_ID, postgres));
        CreateServiceInstanceBindingRequest request = bindingRequest();

        CreateServiceInstanceAppBindingResponse response =
                (CreateServiceInstanceAppBindingResponse) service.createServiceInstanceBinding(request).block();

        assertThat(response.getCredentials()).containsAllEntriesOf(postgres);
        assertThat(response.isBindingExisted()).isFalse();
    }

    @Test
    void bindingIsIdempotentUntilItIsDeleted() {
        MusubiServiceBindingService service = new MusubiServiceBindingService(
                Map.of(POSTGRESQL_SERVICE_ID, Map.of("host", "postgresql")));
        CreateServiceInstanceBindingRequest create = bindingRequest();
        DeleteServiceInstanceBindingRequest delete = DeleteServiceInstanceBindingRequest.builder()
                .serviceInstanceId("instance-1")
                .bindingId("binding-1")
                .serviceDefinitionId(POSTGRESQL_SERVICE_ID)
                .planId(POSTGRESQL_PLAN_ID)
                .build();

        service.createServiceInstanceBinding(create).block();
        CreateServiceInstanceAppBindingResponse repeated =
                (CreateServiceInstanceAppBindingResponse) service.createServiceInstanceBinding(create).block();
        service.deleteServiceInstanceBinding(delete).block();
        CreateServiceInstanceAppBindingResponse afterDelete =
                (CreateServiceInstanceAppBindingResponse) service.createServiceInstanceBinding(create).block();

        assertThat(repeated.isBindingExisted()).isTrue();
        assertThat(afterDelete.isBindingExisted()).isFalse();
    }

	@Test void returnsCredentialsForTheExactGarageBucketPlan() {
		Map<String,Object> evidence=Map.of("bucket","kansei-browser-evidence","endpoint","http://juice:3900");
		MusubiServiceBindingService service=new MusubiServiceBindingService(Map.of(
				GARAGE_SERVICE_ID+":"+GARAGE_BROWSER_EVIDENCE_PLAN_ID,evidence));
		CreateServiceInstanceBindingRequest request=CreateServiceInstanceBindingRequest.builder()
				.serviceInstanceId("evidence-1").bindingId("binding-1").serviceDefinitionId(GARAGE_SERVICE_ID)
				.planId(GARAGE_BROWSER_EVIDENCE_PLAN_ID).build();
		CreateServiceInstanceAppBindingResponse response=(CreateServiceInstanceAppBindingResponse)
				service.createServiceInstanceBinding(request).block();
		assertThat(response.getCredentials()).containsEntry("bucket","kansei-browser-evidence");
	}

    private static CreateServiceInstanceBindingRequest bindingRequest() {
        return CreateServiceInstanceBindingRequest.builder()
                .serviceInstanceId("instance-1")
                .bindingId("binding-1")
                .serviceDefinitionId(POSTGRESQL_SERVICE_ID)
                .planId(POSTGRESQL_PLAN_ID)
                .build();
    }
}
