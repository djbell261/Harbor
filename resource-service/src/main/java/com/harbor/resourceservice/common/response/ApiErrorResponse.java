package com.harbor.resourceservice.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "Structured error response returned by Harbor API failures.")
public record ApiErrorResponse(
	@Schema(description = "UTC timestamp when the error response was created.", example = "2026-05-22T02:30:00Z")
	Instant timestamp,
	@Schema(description = "HTTP status code.", example = "400")
	int status,
	@Schema(description = "HTTP status reason phrase.", example = "Bad Request")
	String error,
	@Schema(description = "Readable explanation of what failed.", example = "reportType must not be null")
	String message,
	@Schema(description = "Request path that produced the error.", example = "/api/resources/not-a-uuid")
	String path,
	@Schema(description = "Request correlation ID, also returned in the X-Correlation-Id response header.", example = "7a51c0e1-d9f0-4bc1-9be8-90a7f0d9f1a8")
	String correlationId
) {
}
