package com.harbor.resourceservice.organization.repository;

import com.harbor.resourceservice.organization.entity.Organization;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

	List<Organization> findAllByOrderByNameAsc();
}
