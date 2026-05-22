package com.harbor.resourceservice.verification.service;

import com.harbor.resourceservice.resource.entity.Resource;
import com.harbor.resourceservice.resource.repository.ResourceRepository;
import com.harbor.resourceservice.verification.dto.CreateVerificationReportRequest;
import com.harbor.resourceservice.verification.dto.VerificationReportResponse;
import com.harbor.resourceservice.verification.entity.VerificationReport;
import com.harbor.resourceservice.verification.repository.VerificationReportRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class VerificationReportService {

	private final ResourceRepository resourceRepository;
	private final VerificationReportRepository reportRepository;

	public VerificationReportService(
		ResourceRepository resourceRepository,
		VerificationReportRepository reportRepository
	) {
		this.resourceRepository = resourceRepository;
		this.reportRepository = reportRepository;
	}

	@Transactional
	public VerificationReportResponse createReport(UUID resourceId, CreateVerificationReportRequest request) {
		Resource resource = resourceRepository.findPublicById(resourceId)
			.orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Resource not found"));

		VerificationReport report = new VerificationReport();
		report.setResource(resource);
		report.setReportType(request.reportType());
		report.setDescription(request.description());
		report.setSuggestedValue(request.suggestedValue());

		return VerificationReportResponse.from(reportRepository.save(report));
	}
}
