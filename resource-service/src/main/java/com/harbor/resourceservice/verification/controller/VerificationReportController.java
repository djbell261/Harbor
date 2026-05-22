package com.harbor.resourceservice.verification.controller;

import com.harbor.resourceservice.common.response.ApiErrorResponse;
import com.harbor.resourceservice.verification.dto.CreateVerificationReportRequest;
import com.harbor.resourceservice.verification.dto.VerificationReportResponse;
import com.harbor.resourceservice.verification.service.VerificationReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resources/{resourceId}/verification-reports")
@Tag(name = "Verification Reports", description = "Anonymous community reports about resource availability and data quality.")
public class VerificationReportController {

	private final VerificationReportService reportService;

	public VerificationReportController(VerificationReportService reportService) {
		this.reportService = reportService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(
		summary = "Submit an anonymous verification report",
		description = """
			Accepts anonymous community reports about resource availability, safety,
			accessibility, hours, or incorrect information. Reports are stored as pending
			and feed lightweight community freshness indicators. No account is required.
			"""
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "201",
			description = "Verification report accepted for review.",
			headers = @Header(name = "X-Correlation-Id", description = "Request correlation ID."),
			content = @Content(
				schema = @Schema(implementation = VerificationReportResponse.class),
				examples = @ExampleObject(value = """
					{
					  "id": "464c933c-d264-410a-8883-f2adae35e334",
					  "resourceId": "11111111-1111-4111-8111-111111111111",
					  "reportType": "food_unavailable",
					  "status": "pending",
					  "description": "Pantry sign said food was unavailable today.",
					  "createdAt": "2026-05-22T02:24:25.569589878Z"
					}
					""")
			)
		),
		@ApiResponse(
			responseCode = "400",
			description = "Malformed request body, unsupported report type, invalid UUID, or validation failure.",
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
	public VerificationReportResponse createReport(
		@Parameter(description = "Public resource UUID receiving the report.", example = "11111111-1111-4111-8111-111111111111")
		@PathVariable UUID resourceId,
		@Valid
		@RequestBody(
			description = "Anonymous verification report payload. reportType is required; description is optional and limited to 2000 characters.",
			required = true,
			content = @Content(
				schema = @Schema(implementation = CreateVerificationReportRequest.class),
				examples = {
					@ExampleObject(
						name = "Food unavailable",
						value = """
							{
							  "reportType": "food_unavailable",
							  "description": "Pantry sign said food was unavailable today.",
							  "suggestedValue": {
							    "reporterKind": "anonymous"
							  }
							}
							"""
					),
					@ExampleObject(
						name = "Incorrect hours",
						value = """
							{
							  "reportType": "incorrect_hours",
							  "description": "Posted hours say the site closes at 5 PM on Fridays.",
							  "suggestedValue": {
							    "fridayClosesAt": "17:00"
							  }
							}
							"""
					)
				}
			)
		)
		@org.springframework.web.bind.annotation.RequestBody CreateVerificationReportRequest request
	) {
		return reportService.createReport(resourceId, request);
	}
}
