package com.harbor.resourceservice.resource.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Paginated public resource search response.")
public record ResourceSearchResponse(
	@Schema(description = "Resources on this page.")
	List<ResourceSummaryResponse> items,
	@Schema(description = "Zero-based page number.", example = "0")
	int page,
	@Schema(description = "Requested page size.", example = "25")
	int size,
	@Schema(description = "Total matching resources.", example = "91")
	long totalItems,
	@Schema(description = "Total available pages.", example = "4")
	int totalPages,
	@Schema(description = "True when another page is available.", example = "true")
	boolean hasNext,
	@Schema(description = "True when an earlier page is available.", example = "false")
	boolean hasPrevious
) {
}
