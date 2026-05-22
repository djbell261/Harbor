package com.harbor.resourceservice.resource.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.harbor.resourceservice.category.entity.ResourceCategory;
import com.harbor.resourceservice.resource.dto.ResourceSearchResponse;
import com.harbor.resourceservice.resource.entity.Resource;
import com.harbor.resourceservice.resource.repository.ResourceHourRepository;
import com.harbor.resourceservice.resource.repository.ResourceRepository;
import com.harbor.resourceservice.resource.repository.ResourceStatusRepository;
import com.harbor.resourceservice.verification.repository.VerificationReportRepository;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

class ResourceQueryServiceTest {

	@Test
	void searchResourcesReturnsPaginationMetadata() {
		Resource resource = resource();
		ResourceQueryService service = new ResourceQueryService(
			resourceRepositoryReturningSearch(resource),
			proxy(ResourceHourRepository.class, (proxy, method, args) -> defaultValue(method.getReturnType())),
			proxy(ResourceStatusRepository.class, (proxy, method, args) -> defaultValue(method.getReturnType())),
			proxy(VerificationReportRepository.class, (proxy, method, args) -> defaultValue(method.getReturnType())),
			new ResourceFreshnessService(30)
		);

		ResourceSearchResponse response = service.searchResources(null, null, null, 1, 1);

		assertThat(response.items()).hasSize(1);
		assertThat(response.page()).isEqualTo(1);
		assertThat(response.size()).isEqualTo(1);
		assertThat(response.totalItems()).isEqualTo(3);
		assertThat(response.totalPages()).isEqualTo(3);
		assertThat(response.hasNext()).isTrue();
		assertThat(response.hasPrevious()).isTrue();
	}

	private ResourceRepository resourceRepositoryReturningSearch(Resource resource) {
		return proxy(ResourceRepository.class, (proxy, method, args) -> {
			if (method.getName().equals("searchPublicResources")) {
				return new PageImpl<>(List.of(resource), PageRequest.of(1, 1), 3);
			}
			return defaultValue(method.getReturnType());
		});
	}

	@SuppressWarnings("unchecked")
	private <T> T proxy(Class<T> type, InvocationHandler handler) {
		return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type}, handler);
	}

	private Object defaultValue(Class<?> returnType) {
		if (returnType.equals(boolean.class)) {
			return false;
		}
		if (returnType.equals(long.class)) {
			return 0L;
		}
		if (returnType.equals(int.class)) {
			return 0;
		}
		if (returnType.equals(List.class)) {
			return List.of();
		}
		if (returnType.equals(Optional.class)) {
			return Optional.empty();
		}
		return null;
	}

	private Resource resource() {
		ResourceCategory category = new ResourceCategory();
		category.setCode("food");
		category.setName("Food");

		Resource resource = new Resource();
		resource.setId(UUID.randomUUID());
		resource.setName("Test Pantry");
		resource.setCategory(category);
		resource.setCity("Wilmington");
		resource.setRegion("DE");
		resource.setCountryCode("US");
		resource.setLastVerifiedAt(Instant.parse("2026-05-22T00:00:00Z"));
		resource.setConfidenceScore(BigDecimal.valueOf(0.800));
		return resource;
	}
}
