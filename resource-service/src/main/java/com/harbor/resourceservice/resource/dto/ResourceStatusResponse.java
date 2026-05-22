package com.harbor.resourceservice.resource.dto;

import com.harbor.resourceservice.resource.entity.ResourceStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "Current or historical availability status for a resource.")
public record ResourceStatusResponse(
	@Schema(description = "Status row UUID.", example = "efefe165-0055-4857-986e-1cd8a0cc6d7d")
	UUID id,
	@Schema(
		description = "Availability state. open means available, limited means constrained, closed means not currently available, unknown means status is not known.",
		example = "open",
		allowableValues = {"open", "closed", "limited", "unknown", "temporarily_closed"}
	)
	String status,
	@Schema(description = "Reason or note explaining the status.", example = "Seeded MVP listing. Call before visiting to confirm operations.")
	String reason,
	@Schema(description = "When this status became effective.", example = "2026-05-21T00:00:00Z")
	Instant effectiveFrom,
	@Schema(description = "When this status stops applying, or null for current status.", example = "2026-05-22T00:00:00Z")
	Instant effectiveUntil,
	@Schema(description = "Source type that reported the status.", example = "system", allowableValues = {"system", "anonymous", "admin", "organization"})
	String reportedByType
) {
	public static ResourceStatusResponse from(ResourceStatus status) {
		return new ResourceStatusResponse(
			status.getId(),
			status.getStatus().name(),
			status.getReason(),
			status.getEffectiveFrom(),
			status.getEffectiveUntil(),
			status.getReportedByType().name()
		);
	}
}
