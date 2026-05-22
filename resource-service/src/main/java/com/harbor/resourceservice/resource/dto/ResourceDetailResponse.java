package com.harbor.resourceservice.resource.dto;

import com.harbor.resourceservice.organization.dto.OrganizationSummaryResponse;
import com.harbor.resourceservice.resource.entity.Resource;
import com.harbor.resourceservice.resource.entity.ResourceHour;
import com.harbor.resourceservice.resource.entity.ResourceStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(description = "Detailed public resource record with hours, status, organization, and community verification metadata.")
public record ResourceDetailResponse(
	@Schema(description = "Resource UUID.", example = "11111111-1111-4111-8111-111111111111")
	UUID id,
	@Schema(description = "Resource name.", example = "LCS Food Pantry at St. Stephen's Lutheran Church")
	String name,
	@Schema(description = "Plain-language resource description.", example = "Community food pantry serving Wilmington residents with grocery assistance.")
	String description,
	@Schema(description = "Stable category code.", example = "food")
	String categoryCode,
	@Schema(description = "Human-readable category name.", example = "Food")
	String categoryName,
	@Schema(description = "Street address line 1.", example = "1301 N Broom St")
	String addressLine1,
	@Schema(description = "Street address line 2, when available.", example = "Suite 100")
	String addressLine2,
	@Schema(description = "Resource city.", example = "Wilmington")
	String city,
	@Schema(description = "State or region abbreviation.", example = "DE")
	String region,
	@Schema(description = "Postal code.", example = "19806")
	String postalCode,
	@Schema(description = "ISO country code.", example = "US")
	String countryCode,
	@Schema(description = "Latitude for optional map display.", example = "39.753940")
	BigDecimal latitude,
	@Schema(description = "Longitude for optional map display.", example = "-75.563870")
	BigDecimal longitude,
	@Schema(description = "Public phone number, when available.", example = "(302) 652-7623")
	String phone,
	@Schema(description = "Public website URL, when available.", example = "https://lcsde.org/service/food-pantry-services")
	String websiteUrl,
	@Schema(description = "Eligibility notes users should know before visiting.", example = "Registration may be required. Bring photo ID when available.")
	String eligibilityNotes,
	@Schema(description = "Intake or arrival instructions.", example = "Call before going; distribution times can change around holidays.")
	String intakeNotes,
	@Schema(description = "Accessibility notes for the location.", example = "Call ahead for entrance instructions.")
	String accessibilityNotes,
	@Schema(description = "Source of the record.", example = "seed")
	String dataSource,
	@Schema(description = "Source URL used to seed or verify the listing.", example = "https://lcsde.org/service/food-pantry-services")
	String sourceUrl,
	@Schema(description = "Most recent formal verification timestamp.", example = "2026-05-21T00:00:00Z")
	Instant lastVerifiedAt,
	@Schema(description = "Confidence score from 0.000 to 1.000. Higher means Harbor has more confidence in the listing.", example = "0.850")
	BigDecimal confidenceScore,
	@Schema(description = "Current availability status for the resource.")
	ResourceStatusResponse currentStatus,
	@Schema(description = "Weekly operating hours. dayOfWeek uses 0 for Sunday through 6 for Saturday.")
	List<ResourceHourResponse> hours,
	@Schema(description = "Organization associated with the resource, when known.")
	OrganizationSummaryResponse organization,
	@Schema(description = "Community verification and freshness metadata.")
	VerificationMetadataResponse verification,
	@Schema(description = "Most recent anonymous community reports summarized for display.")
	List<CommunityUpdateResponse> communityUpdates
) {
	public static ResourceDetailResponse from(
		Resource resource,
		ResourceStatus currentStatus,
		List<ResourceHour> hours,
		VerificationMetadataResponse verification,
		List<CommunityUpdateResponse> communityUpdates
	) {
		return new ResourceDetailResponse(
			resource.getId(),
			resource.getName(),
			resource.getDescription(),
			resource.getCategory().getCode(),
			resource.getCategory().getName(),
			resource.getAddressLine1(),
			resource.getAddressLine2(),
			resource.getCity(),
			resource.getRegion(),
			resource.getPostalCode(),
			resource.getCountryCode(),
			resource.getLatitude(),
			resource.getLongitude(),
			resource.getPhone(),
			resource.getWebsiteUrl(),
			resource.getEligibilityNotes(),
			resource.getIntakeNotes(),
			resource.getAccessibilityNotes(),
			resource.getDataSource(),
			resource.getSourceUrl(),
			resource.getLastVerifiedAt(),
			resource.getConfidenceScore(),
			currentStatus == null ? null : ResourceStatusResponse.from(currentStatus),
			hours.stream().map(ResourceHourResponse::from).toList(),
			OrganizationSummaryResponse.from(resource.getOrganization()),
			verification,
			communityUpdates
		);
	}
}
