package com.harbor.resourceservice.resource.repository;

import com.harbor.resourceservice.resource.entity.ResourceHour;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceHourRepository extends JpaRepository<ResourceHour, UUID> {

	List<ResourceHour> findByResourceIdOrderByDayOfWeekAscOpensAtAsc(UUID resourceId);
}
