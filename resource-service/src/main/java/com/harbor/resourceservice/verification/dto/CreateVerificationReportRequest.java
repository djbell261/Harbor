package com.harbor.resourceservice.verification.dto;

import com.harbor.resourceservice.verification.enums.VerificationReportType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Map;

@Schema(description = "Anonymous community verification report request.")
public record CreateVerificationReportRequest(
	@Schema(
		description = "Required report type. Use the most specific value available so Harbor can summarize community updates clearly.",
		example = "food_unavailable",
		implementation = VerificationReportType.class
	)
	@NotNull VerificationReportType reportType,
	@Schema(description = "Optional report details. Maximum 2000 characters.", example = "Pantry sign said food was unavailable today.", maxLength = 2000)
	@Size(max = 2000) String description,
	@Schema(
		description = "Optional structured suggestion data. The MVP stores this as JSON for later review without requiring accounts.",
		example = "{\"reporterKind\":\"anonymous\",\"fridayClosesAt\":\"17:00\"}"
	)
	Map<String, Object> suggestedValue
) {
}
