package com.harbor.resourceservice.verification.dto;

import com.harbor.resourceservice.verification.entity.VerificationReport;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Schema(description = "Verification report accepted by Harbor.")
public record VerificationReportResponse(
	@Schema(description = "Verification report UUID.", example = "464c933c-d264-410a-8883-f2adae35e334")
	UUID id,
	@Schema(description = "Resource UUID the report belongs to.", example = "11111111-1111-4111-8111-111111111111")
	UUID resourceId,
	@Schema(description = "Submitted report type.", example = "food_unavailable")
	String reportType,
	@Schema(description = "Review status for the report.", example = "pending", allowableValues = {"pending", "accepted", "rejected", "needs_more_info"})
	String status,
	@Schema(description = "Optional report details from the anonymous reporter.", example = "Pantry sign said food was unavailable today.")
	String description,
	@Schema(description = "Optional structured suggestion data submitted with the report.")
	Map<String, Object> suggestedValue,
	@Schema(description = "Report creation timestamp.", example = "2026-05-22T02:24:25.569589878Z")
	Instant createdAt,
	@Schema(description = "Timestamp when an admin reviewed this report.", example = "2026-05-22T03:12:00Z")
	Instant reviewedAt,
	@Schema(description = "Internal admin review notes.", example = "Provider confirmed updated hours.")
	String reviewNotes,
	@Schema(description = "Admin review decision.", example = "accepted", allowableValues = {"accepted", "rejected"})
	String reviewDecision,
	@Schema(description = "Reviewer placeholder until auth exists.", example = "admin")
	String reviewedBy
) {
	public static VerificationReportResponse from(VerificationReport report) {
		return new VerificationReportResponse(
			report.getId(),
			report.getResource().getId(),
			report.getReportType().name(),
			report.getStatus().name(),
			report.getDescription(),
			report.getSuggestedValue(),
			report.getCreatedAt(),
			report.getReviewedAt(),
			report.getReviewNotes(),
			report.getReviewDecision(),
			report.getReviewedBy()
		);
	}
}
