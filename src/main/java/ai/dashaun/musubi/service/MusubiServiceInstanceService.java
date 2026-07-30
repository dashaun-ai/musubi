package ai.dashaun.musubi.service;

import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MusubiServiceInstanceService implements ServiceInstanceService {

    private final Set<String> instances = ConcurrentHashMap.newKeySet();

    @Override
    public Mono<CreateServiceInstanceResponse> createServiceInstance(CreateServiceInstanceRequest request) {
        boolean added = instances.add(request.getServiceInstanceId());
        return Mono.just(CreateServiceInstanceResponse.builder()
                .instanceExisted(!added)
                .build());
    }

    @Override
    public Mono<DeleteServiceInstanceResponse> deleteServiceInstance(DeleteServiceInstanceRequest request) {
        instances.remove(request.getServiceInstanceId());
        return Mono.just(DeleteServiceInstanceResponse.builder().build());
    }
}
