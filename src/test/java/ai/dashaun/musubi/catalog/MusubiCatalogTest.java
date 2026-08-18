package ai.dashaun.musubi.catalog;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.servicebroker.model.catalog.Catalog;

import static org.assertj.core.api.Assertions.assertThat;

class MusubiCatalogTest {

    @Test
    void advertisesInfrastructureAndBucketSpecificGaragePlans() {
        Catalog catalog = new MusubiCatalog().catalog();

        assertThat(catalog.getServiceDefinitions())
                .extracting(service -> service.getName())
                .containsExactly("postgresql", "rabbitmq", "redis", "grafana-lgtm", "garage");
        assertThat(catalog.getServiceDefinitions())
                .allSatisfy(service -> {
                    assertThat(service.isBindable()).isTrue();
                    assertThat(service.getPlans()).allSatisfy(plan -> assertThat(plan.isFree()).isTrue());
                });
        assertThat(catalog.getServiceDefinitions().getLast().getPlans())
                .extracting(plan -> plan.getName())
                .containsExactly("run-artifacts", "browser-evidence", "encrypted-backups");
    }

    @Test
    void usesStableUniqueServiceAndPlanIds() {
        Catalog first = new MusubiCatalog().catalog();
        Catalog second = new MusubiCatalog().catalog();

        assertThat(first.getServiceDefinitions())
                .extracting(service -> service.getId())
                .doesNotHaveDuplicates()
                .containsExactlyElementsOf(second.getServiceDefinitions().stream()
                        .map(service -> service.getId())
                        .toList());
        assertThat(first.getServiceDefinitions())
                .flatExtracting(service -> service.getPlans())
                .extracting(plan -> plan.getId())
                .doesNotHaveDuplicates();
    }
}
