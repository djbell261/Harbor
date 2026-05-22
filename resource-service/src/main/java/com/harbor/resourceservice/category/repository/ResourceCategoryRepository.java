package com.harbor.resourceservice.category.repository;

import com.harbor.resourceservice.category.entity.ResourceCategory;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceCategoryRepository extends JpaRepository<ResourceCategory, UUID> {

	List<ResourceCategory> findByActiveTrueOrderBySortOrderAscNameAsc();
}
