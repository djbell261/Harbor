package com.harbor.resourceservice.verification.service;

import com.harbor.resourceservice.resource.entity.Resource;
import com.harbor.resourceservice.resource.repository.ResourceRepository;
import com.harbor.resourceservice.verification.dto.CreateVerificationReportRequest;
import com.harbor.resourceservice.verification.dto.ReviewVerificationReportRequest;
import com.harbor.resourceservice.verification.dto.VerificationReportResponse;
import com.harbor.resourceservice.verification.entity.VerificationReport;
import com.harbor.resourceservice.verification.enums.VerificationStatus;
import com.harbor.resourceservice.verification.repository.VerificationReportRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
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
		report.setDescription(trimToNull(request.description()));
		report.setSuggestedValue(request.suggestedValue());

		return VerificationReportResponse.from(reportRepository.save(report));
	}

	@Transactional(readOnly = true)
	public List<VerificationReportResponse> findReports(VerificationStatus status) {
		return reportRepository.findByStatusOrderByCreatedAtAsc(status)
			.stream()
			.map(VerificationReportResponse::from)
			.toList();
	}

	@Transactional(readOnly = true)
	public VerificationReportResponse findReport(UUID id) {
		return VerificationReportResponse.from(findReportWithResource(id));
	}

	@Transactional
	public VerificationReportResponse acceptReport(UUID id, ReviewVerificationReportRequest request) {
		VerificationReport report = findReportWithResource(id);
		ensurePending(report);
		applyReview(report, VerificationStatus.accepted, request);
		Resource resource = report.getResource();
		Instant now = Instant.now();
		resource.setLastVerifiedAt(now);
		resource.setUpdatedAt(now);
		resource.setConfidenceScore(increaseConfidence(resource.getConfidenceScore()));
		return VerificationReportResponse.from(reportRepository.save(report));
	}

	@Transactional
	public VerificationReportResponse rejectReport(UUID id, ReviewVerificationReportRequest request) {
		VerificationReport report = findReportWithResource(id);
		ensurePending(report);
		applyReview(report, VerificationStatus.rejected, request);
		return VerificationReportResponse.from(reportRepository.save(report));
	}

	private VerificationReport findReportWithResource(UUID id) {
		return reportRepository.findWithResourceById(id)
			.orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Verification report not found"));
	}

	private void applyReview(
		VerificationReport report,
		VerificationStatus decision,
		ReviewVerificationReportRequest request
	) {
		report.setStatus(decision);
		report.setReviewedAt(Instant.now());
		report.setReviewDecision(decision.name());
		report.setReviewNotes(request == null ? null : trimToNull(request.reviewNotes()));
		String reviewedBy = request == null ? null : request.reviewedBy();
		String trimmedReviewedBy = trimToNull(reviewedBy);
		report.setReviewedBy(trimmedReviewedBy == null ? "admin" : trimmedReviewedBy);
	}

	private void ensurePending(VerificationReport report) {
		if (report.getStatus() != VerificationStatus.pending
			|| report.getReviewedAt() != null
			|| report.getReviewDecision() != null) {
			throw new ResponseStatusException(
				org.springframework.http.HttpStatus.CONFLICT,
				"Verification report has already been reviewed"
			);
		}
	}

	private BigDecimal increaseConfidence(BigDecimal confidenceScore) {
		BigDecimal current = confidenceScore == null ? BigDecimal.valueOf(0.500) : confidenceScore;
		return current.add(BigDecimal.valueOf(0.050)).min(BigDecimal.ONE);
	}

	private String trimToNull(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return value.trim();
	}
}
