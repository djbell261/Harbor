package com.harbor.resourceservice.verification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Admin review metadata for accepting or rejecting a verification report.")
public record ReviewVerificationReportRequest(
	@Schema(description = "Optional internal review notes.", example = "Confirmed by calling the listed provider.", maxLength = 2000)
	@Size(max = 2000) String reviewNotes,
	@Schema(description = "Reviewer placeholder until auth is added.", example = "admin")
	@Size(max = 120) String reviewedBy
) {
}
