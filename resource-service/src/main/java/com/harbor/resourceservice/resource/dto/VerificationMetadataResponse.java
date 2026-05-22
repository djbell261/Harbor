package com.harbor.resourceservice.resource.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "Lightweight community verification and freshness metadata for a resource.")
public record VerificationMetadataResponse(
	@Schema(description = "Total anonymous verification reports submitted for this resource.", example = "3")
	long reportCount,
	@Schema(description = "Anonymous reports still waiting for admin review.", example = "1")
	long pendingReportCount,
	@Schema(description = "Timestamp of the most recent community report, or null when there are no reports.", example = "2026-05-22T00:32:10.152423Z")
	Instant lastCommunityReportAt,
	@Schema(description = "True when the resource has at least one community report in the last 30 days.", example = "true")
	boolean communityConfirmed,
	@Schema(description = "True when the resource record was updated in the last 7 days.", example = "true")
	boolean recentlyUpdated,
	@Schema(description = "Configured days after which a resource is considered stale.", example = "30")
	int staleThresholdDays
) {
}
