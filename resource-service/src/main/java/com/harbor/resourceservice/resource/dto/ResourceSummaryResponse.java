package com.harbor.resourceservice.resource.dto;

import com.harbor.resourceservice.resource.entity.Resource;
import com.harbor.resourceservice.resource.entity.ResourceStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "Compact resource listing returned by search endpoints.")
public record ResourceSummaryResponse(
	@Schema(description = "Resource UUID.", example = "11111111-1111-4111-8111-111111111111")
	UUID id,
	@Schema(description = "Resource name.", example = "LCS Food Pantry at St. Stephen's Lutheran Church")
	String name,
	@Schema(description = "Stable category code used for filtering.", example = "food")
	String categoryCode,
	@Schema(description = "Human-readable category name.", example = "Food")
	String categoryName,
	@Schema(description = "Resource city.", example = "Wilmington")
	String city,
	@Schema(description = "State or region abbreviation.", example = "DE")
	String region,
	@Schema(description = "Postal code.", example = "19806")
	String postalCode,
	@Schema(description = "Latitude for optional map display.", example = "39.753940")
	BigDecimal latitude,
	@Schema(description = "Longitude for optional map display.", example = "-75.563870")
	BigDecimal longitude,
	@Schema(description = "Public phone number, when available.", example = "(302) 652-7623")
	String phone,
	@Schema(
		description = "Current resource status. open means available, limited means constrained, closed means not currently available, unknown means status is not known.",
		example = "open",
		allowableValues = {"open", "closed", "limited", "unknown", "temporarily_closed"}
	)
	String status,
	@Schema(description = "Most recent formal verification timestamp.", example = "2026-05-21T00:00:00Z")
	Instant lastVerifiedAt,
	@Schema(description = "Confidence score from 0.000 to 1.000. Higher means Harbor has more confidence in the listing.", example = "0.850")
	BigDecimal confidenceScore,
	@Schema(description = "True when the resource has never been verified or is past the configured verification freshness threshold.", example = "false")
	boolean stale,
	@Schema(description = "True when the resource is stale or has pending community reports that need admin review.", example = "true")
	boolean needsVerification,
	@Schema(description = "Community verification and freshness metadata.")
	VerificationMetadataResponse verification
) {
	public static ResourceSummaryResponse from(
		Resource resource,
		ResourceStatus currentStatus,
		VerificationMetadataResponse verification,
		boolean stale
	) {
		return new ResourceSummaryResponse(
			resource.getId(),
			resource.getName(),
			resource.getCategory().getCode(),
			resource.getCategory().getName(),
			resource.getCity(),
			resource.getRegion(),
			resource.getPostalCode(),
			resource.getLatitude(),
			resource.getLongitude(),
			resource.getPhone(),
			currentStatus == null ? "unknown" : currentStatus.getStatus().name(),
			resource.getLastVerifiedAt(),
			resource.getConfidenceScore(),
			stale,
			stale || verification.pendingReportCount() > 0,
			verification
		);
	}
}
