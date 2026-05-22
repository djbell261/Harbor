package com.harbor.resourceservice.verification.controller;

import com.harbor.resourceservice.verification.dto.ReviewVerificationReportRequest;
import com.harbor.resourceservice.verification.dto.VerificationReportResponse;
import com.harbor.resourceservice.verification.enums.VerificationStatus;
import com.harbor.resourceservice.verification.service.VerificationReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/verification-reports")
@Tag(name = "Admin Verification Reports", description = "No-auth MVP admin review operations for community verification reports.")
public class AdminVerificationReportController {

	private final VerificationReportService reportService;

	public AdminVerificationReportController(VerificationReportService reportService) {
		this.reportService = reportService;
	}

	@GetMapping
	@Operation(summary = "List verification reports by review status")
	public List<VerificationReportResponse> listReports(
		@RequestParam(defaultValue = "pending") VerificationStatus status
	) {
		return reportService.findReports(status);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get one verification report for admin review")
	public VerificationReportResponse getReport(@PathVariable UUID id) {
		return reportService.findReport(id);
	}

	@PostMapping("/{id}/accept")
	@Operation(summary = "Accept a verification report")
	public VerificationReportResponse acceptReport(
		@PathVariable UUID id,
		@Valid @org.springframework.web.bind.annotation.RequestBody(required = false) ReviewVerificationReportRequest request
	) {
		return reportService.acceptReport(id, request);
	}

	@PostMapping("/{id}/reject")
	@Operation(summary = "Reject a verification report")
	public VerificationReportResponse rejectReport(
		@PathVariable UUID id,
		@Valid @org.springframework.web.bind.annotation.RequestBody(required = false) ReviewVerificationReportRequest request
	) {
		return reportService.rejectReport(id, request);
	}
}
