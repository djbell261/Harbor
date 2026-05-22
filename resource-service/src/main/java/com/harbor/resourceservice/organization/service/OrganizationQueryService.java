package com.harbor.resourceservice.organization.service;

import com.harbor.resourceservice.organization.dto.OrganizationResponse;
import com.harbor.resourceservice.organization.repository.OrganizationRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OrganizationQueryService {

	private final OrganizationRepository organizationRepository;

	public OrganizationQueryService(OrganizationRepository organizationRepository) {
		this.organizationRepository = organizationRepository;
	}

	public List<OrganizationResponse> findOrganizations() {
		return organizationRepository.findAllByOrderByNameAsc()
			.stream()
			.map(OrganizationResponse::from)
			.toList();
	}
}
