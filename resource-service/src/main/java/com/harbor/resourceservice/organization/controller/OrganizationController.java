package com.harbor.resourceservice.organization.controller;

import com.harbor.resourceservice.common.response.ApiErrorResponse;
import com.harbor.resourceservice.organization.dto.OrganizationResponse;
import com.harbor.resourceservice.organization.service.OrganizationQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/organizations")
@Tag(name = "Organizations", description = "Public organization records associated with Harbor resource listings.")
public class OrganizationController {

	private final OrganizationQueryService organizationQueryService;

	public OrganizationController(OrganizationQueryService organizationQueryService) {
		this.organizationQueryService = organizationQueryService;
	}

	@GetMapping
	@Operation(
		summary = "List organizations",
		description = """
			Returns organizations associated with Harbor resources. Organization records
			help users understand who maintains or operates a resource listing.
			"""
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "Organizations associated with resources.",
			headers = @Header(name = "X-Correlation-Id", description = "Request correlation ID."),
			content = @Content(
				array = @ArraySchema(schema = @Schema(implementation = OrganizationResponse.class)),
				examples = @ExampleObject(value = """
					[
					  {
					    "id": "aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaa1",
					    "name": "Lutheran Community Services Delaware",
					    "description": "Community organization supporting Wilmington residents.",
					    "websiteUrl": "https://lcsde.org/",
					    "phone": "(302) 654-8886",
					    "email": null,
					    "trustedStatus": "verified"
					  }
					]
					""")
			)
		),
		@ApiResponse(
			responseCode = "500",
			description = "Unexpected API error.",
			content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
		)
	})
	public List<OrganizationResponse> listOrganizations() {
		return organizationQueryService.findOrganizations();
	}
}
