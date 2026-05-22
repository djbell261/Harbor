package com.harbor.resourceservice.category.dto;

import com.harbor.resourceservice.category.entity.ResourceCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Resource category used for filtering and grouping Harbor listings.")
public record CategoryResponse(
	@Schema(description = "Category UUID.", example = "aaaaaaaa-1111-4111-8111-111111111111")
	UUID id,
	@Schema(description = "Stable category code used by query filters.", example = "food")
	String code,
	@Schema(description = "Human-readable category name.", example = "Food")
	String name,
	@Schema(description = "Short category explanation.", example = "Food pantries, meal programs, and grocery help")
	String description,
	@Schema(description = "Frontend icon hint.", example = "utensils")
	String iconName,
	@Schema(description = "Display order for category navigation.", example = "10")
	Integer sortOrder
) {
	public static CategoryResponse from(ResourceCategory category) {
		return new CategoryResponse(
			category.getId(),
			category.getCode(),
			category.getName(),
			category.getDescription(),
			category.getIconName(),
			category.getSortOrder()
		);
	}
}
