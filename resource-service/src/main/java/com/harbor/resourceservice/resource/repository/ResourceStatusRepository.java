package com.harbor.resourceservice.resource.repository;

import com.harbor.resourceservice.resource.entity.ResourceStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceStatusRepository extends JpaRepository<ResourceStatus, UUID> {

	Optional<ResourceStatus> findFirstByResourceIdAndEffectiveUntilIsNullOrderByEffectiveFromDesc(UUID resourceId);

	List<ResourceStatus> findByResourceIdOrderByEffectiveFromDesc(UUID resourceId);

	List<ResourceStatus> findTop5ByResourceIdOrderByEffectiveFromDesc(UUID resourceId);
}
