package com.harbor.resourceservice.resource.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "Recent anonymous community update shown on resource detail pages.")
public record CommunityUpdateResponse(
	@Schema(description = "Verification report UUID.", example = "91bbbc63-df20-48b1-96d4-1dc7ce6d1899")
	UUID id,
	@Schema(description = "Verification report type.", example = "incorrect_hours")
	String reportType,
	@Schema(description = "Plain-language summary safe for display.", example = "Hours reported incorrect")
	String message,
	@Schema(description = "Time the report was submitted.", example = "2026-05-22T00:32:10.152423Z")
	Instant createdAt
) {
}
