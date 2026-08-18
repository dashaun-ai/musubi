package ai.dashaun.musubi.service;

import ai.dashaun.musubi.config.MusubiServicesProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceAppBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MusubiServiceBindingService implements ServiceInstanceBindingService {

    private final Map<String, Map<String, Object>> credentialsByServiceId;
    private final Set<String> bindings = ConcurrentHashMap.newKeySet();

    @Autowired
    public MusubiServiceBindingService(MusubiServicesProperties properties) {
        this(properties.credentialsByServiceId());
    }

    MusubiServiceBindingService(Map<String, Map<String, Object>> credentialsByServiceId) {
        this.credentialsByServiceId = Map.copyOf(credentialsByServiceId);
    }

    @Override
    public Mono<CreateServiceInstanceBindingResponse> createServiceInstanceBinding(
            CreateServiceInstanceBindingRequest request) {
        Map<String, Object> credentials = credentialsByServiceId.get(
                request.getServiceDefinitionId() + ":" + request.getPlanId());
        if (credentials == null) credentials = credentialsByServiceId.get(request.getServiceDefinitionId());
        if (credentials == null) {
            return Mono.error(new IllegalArgumentException(
                    "Unknown Musubi service definition: " + request.getServiceDefinitionId()));
        }
        boolean added = bindings.add(bindingKey(request.getServiceInstanceId(), request.getBindingId()));
        return Mono.just(CreateServiceInstanceAppBindingResponse.builder()
                .credentials(credentials)
                .bindingExisted(!added)
                .build());
    }

    @Override
    public Mono<DeleteServiceInstanceBindingResponse> deleteServiceInstanceBinding(
            DeleteServiceInstanceBindingRequest request) {
        bindings.remove(bindingKey(request.getServiceInstanceId(), request.getBindingId()));
        return Mono.just(DeleteServiceInstanceBindingResponse.builder().build());
    }

    private static String bindingKey(String instanceId, String bindingId) {
        return instanceId + ":" + bindingId;
    }
}
