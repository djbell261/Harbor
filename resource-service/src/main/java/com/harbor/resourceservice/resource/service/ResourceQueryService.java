package com.harbor.resourceservice.resource.service;

import com.harbor.resourceservice.resource.dto.ResourceDetailResponse;
import com.harbor.resourceservice.resource.dto.ResourceSummaryResponse;
import com.harbor.resourceservice.resource.dto.CommunityUpdateResponse;
import com.harbor.resourceservice.resource.dto.VerificationMetadataResponse;
import com.harbor.resourceservice.resource.entity.Resource;
import com.harbor.resourceservice.resource.entity.ResourceStatus;
import com.harbor.resourceservice.resource.repository.ResourceHourRepository;
import com.harbor.resourceservice.resource.repository.ResourceRepository;
import com.harbor.resourceservice.resource.repository.ResourceStatusRepository;
import com.harbor.resourceservice.verification.entity.VerificationReport;
import com.harbor.resourceservice.verification.repository.VerificationReportRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class ResourceQueryService {

	private final ResourceRepository resourceRepository;
	private final ResourceHourRepository hourRepository;
	private final ResourceStatusRepository statusRepository;
	private final VerificationReportRepository reportRepository;

	public ResourceQueryService(
		ResourceRepository resourceRepository,
		ResourceHourRepository hourRepository,
		ResourceStatusRepository statusRepository,
		VerificationReportRepository reportRepository
	) {
		this.resourceRepository = resourceRepository;
		this.hourRepository = hourRepository;
		this.statusRepository = statusRepository;
		this.reportRepository = reportRepository;
	}

	public List<ResourceSummaryResponse> findResources(
		String category,
		String city,
		String postalCode,
		int page,
		int size
	) {
		Pageable pageable = PageRequest.of(Math.max(page, 0), clamp(size, 1, 100));
		return resourceRepository.findPublicResources(
				blankToNull(category),
				blankToNull(city),
				blankToNull(postalCode),
				pageable
			)
			.stream()
			.map(resource -> ResourceSummaryResponse.from(
				resource,
				findCurrentStatus(resource.getId()),
				buildVerificationMetadata(resource)
			))
			.toList();
	}

	public ResourceDetailResponse findResource(UUID id) {
		Resource resource = resourceRepository.findPublicById(id)
			.orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Resource not found"));

		return ResourceDetailResponse.from(
			resource,
			findCurrentStatus(resource.getId()),
			hourRepository.findByResourceIdOrderByDayOfWeekAscOpensAtAsc(resource.getId()),
			buildVerificationMetadata(resource),
			findCommunityUpdates(resource.getId())
		);
	}

	private ResourceStatus findCurrentStatus(UUID resourceId) {
		return statusRepository.findFirstByResourceIdAndEffectiveUntilIsNullOrderByEffectiveFromDesc(resourceId)
			.orElse(null);
	}

	private String blankToNull(String value) {
		return value == null || value.isBlank() ? null : value.trim();
	}

	private int clamp(int value, int min, int max) {
		return Math.max(min, Math.min(max, value));
	}

	private VerificationMetadataResponse buildVerificationMetadata(Resource resource) {
		UUID resourceId = resource.getId();
		long reportCount = reportRepository.countByResourceId(resourceId);
		Instant lastCommunityReportAt = reportRepository.findFirstByResourceIdOrderByCreatedAtDesc(resourceId)
			.map(VerificationReport::getCreatedAt)
			.orElse(null);
		boolean communityConfirmed = reportRepository.existsByResourceIdAndCreatedAtAfter(
			resourceId,
			Instant.now().minus(Duration.ofDays(30))
		);
		boolean recentlyUpdated = resource.getUpdatedAt() != null
			&& resource.getUpdatedAt().isAfter(Instant.now().minus(Duration.ofDays(7)));

		return new VerificationMetadataResponse(
			reportCount,
			lastCommunityReportAt,
			communityConfirmed,
			recentlyUpdated
		);
	}

	private List<CommunityUpdateResponse> findCommunityUpdates(UUID resourceId) {
		return reportRepository.findTop5ByResourceIdOrderByCreatedAtDesc(resourceId)
			.stream()
			.map(report -> new CommunityUpdateResponse(
				report.getId(),
				report.getReportType().name(),
				buildCommunityMessage(report),
				report.getCreatedAt()
			))
			.toList();
	}

	private String buildCommunityMessage(VerificationReport report) {
		return switch (report.getReportType()) {
			case food_unavailable -> "Food pantry reported unavailable";
			case shelter_full -> "Shelter reported full";
			case restroom_closed -> "Restroom reported closed";
			case wifi_offline -> "Wi-Fi reported offline";
			case unsafe_location, unsafe -> "Safety concern reported";
			case incorrect_hours, wrong_hours -> "Hours reported incorrect";
			case inaccessible -> "Accessibility issue reported";
			case closed -> "Resource reported closed";
			case wrong_address -> "Address reported incorrect";
			case wrong_phone -> "Phone number reported incorrect";
			case duplicate -> "Possible duplicate reported";
			case other -> "Community update reported";
		};
	}
}
