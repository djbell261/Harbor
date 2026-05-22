package com.harbor.resourceservice.resource.controller;

import com.harbor.resourceservice.common.response.ApiErrorResponse;
import com.harbor.resourceservice.resource.dto.ResourceDetailResponse;
import com.harbor.resourceservice.resource.dto.ResourceSummaryResponse;
import com.harbor.resourceservice.resource.service.ResourceQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.UUID;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resources")
@Validated
@Tag(name = "Resources", description = "Public Harbor resource search and detail APIs.")
public class ResourceController {

	private final ResourceQueryService resourceQueryService;

	public ResourceController(ResourceQueryService resourceQueryService) {
		this.resourceQueryService = resourceQueryService;
	}

	@GetMapping
	@Operation(
		summary = "Search public resources",
		description = """
			Returns public resource summaries filtered by category, city, or postal code.
			The response is intentionally a simple array for frontend compatibility.
			Use page and size for lightweight pagination; page is zero-based and size is limited to 100.
			An empty array is returned when no matching resources are found.
			"""
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "Matching public resources, possibly empty.",
			headers = @Header(name = "X-Correlation-Id", description = "Request correlation ID."),
			content = @Content(
				array = @ArraySchema(schema = @Schema(implementation = ResourceSummaryResponse.class)),
				examples = @ExampleObject(value = """
					[
					  {
					    "id": "11111111-1111-4111-8111-111111111111",
					    "name": "LCS Food Pantry at St. Stephen's Lutheran Church",
					    "categoryCode": "food",
					    "categoryName": "Food",
					    "city": "Wilmington",
					    "region": "DE",
					    "postalCode": "19806",
					    "latitude": 39.753940,
					    "longitude": -75.563870,
					    "phone": "(302) 652-7623",
					    "status": "open",
					    "lastVerifiedAt": "2026-05-21T00:00:00Z",
					    "confidenceScore": 0.850,
					    "verification": {
					      "reportCount": 1,
					      "lastCommunityReportAt": "2026-05-22T00:32:10.152423Z",
					      "communityConfirmed": true,
					      "recentlyUpdated": true
					    }
					  }
					]
					""")
			)
		),
		@ApiResponse(
			responseCode = "400",
			description = "Invalid query parameter, such as page < 0 or size outside 1-100.",
			content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
		),
		@ApiResponse(
			responseCode = "500",
			description = "Unexpected API error.",
			content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
		)
	})
	public List<ResourceSummaryResponse> listResources(
		@Parameter(description = "Optional category code, for example food, shelter, clinic, restroom, wifi, transportation.", example = "food")
		@RequestParam(required = false) String category,
		@Parameter(description = "Optional city filter. Current seed data uses Wilmington.", example = "Wilmington")
		@RequestParam(required = false) String city,
		@Parameter(description = "Optional postal code filter.", example = "19806")
		@RequestParam(required = false) String postalCode,
		@Parameter(description = "Zero-based page number.", example = "0")
		@RequestParam(defaultValue = "0") @Min(0) int page,
		@Parameter(description = "Maximum number of resources to return. Must be between 1 and 100.", example = "25")
		@RequestParam(defaultValue = "50") @Min(1) @Max(100) int size
	) {
		return resourceQueryService.findResources(category, city, postalCode, page, size);
	}

	@GetMapping("/{id}")
	@Operation(
		summary = "Get resource details",
		description = """
			Returns full public details for one resource, including current status,
			hours, organization relationship, confidence score, freshness indicators,
			and recent anonymous community updates.
			"""
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "Resource details.",
			headers = @Header(name = "X-Correlation-Id", description = "Request correlation ID."),
			content = @Content(
				schema = @Schema(implementation = ResourceDetailResponse.class),
				examples = @ExampleObject(value = """
					{
					  "id": "11111111-1111-4111-8111-111111111111",
					  "name": "LCS Food Pantry at St. Stephen's Lutheran Church",
					  "description": "Community food pantry serving Wilmington residents with grocery assistance.",
					  "categoryCode": "food",
					  "categoryName": "Food",
					  "addressLine1": "1301 N Broom St",
					  "city": "Wilmington",
					  "region": "DE",
					  "postalCode": "19806",
					  "countryCode": "US",
					  "phone": "(302) 652-7623",
					  "websiteUrl": "https://lcsde.org/service/food-pantry-services",
					  "lastVerifiedAt": "2026-05-21T00:00:00Z",
					  "confidenceScore": 0.850,
					  "currentStatus": {
					    "status": "open",
					    "reason": "Seeded MVP listing. Call before visiting to confirm pantry operations."
					  },
					  "organization": {
					    "name": "Lutheran Community Services Delaware",
					    "trustedStatus": "verified"
					  },
					  "verification": {
					    "reportCount": 1,
					    "communityConfirmed": true,
					    "recentlyUpdated": true
					  },
					  "communityUpdates": [
					    {
					      "reportType": "wrong_hours",
					      "message": "Hours reported incorrect"
					    }
					  ]
					}
					""")
			)
		),
		@ApiResponse(
			responseCode = "400",
			description = "Resource ID is not a valid UUID.",
			content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
		),
		@ApiResponse(
			responseCode = "404",
			description = "Resource was not found or is not public.",
			content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
		),
		@ApiResponse(
			responseCode = "500",
			description = "Unexpected API error.",
			content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
		)
	})
	public ResourceDetailResponse getResource(
		@Parameter(description = "Public resource UUID.", example = "11111111-1111-4111-8111-111111111111")
		@PathVariable UUID id
	) {
		return resourceQueryService.findResource(id);
	}
}
