package com.harbor.resourceservice.category.controller;

import com.harbor.resourceservice.category.dto.CategoryResponse;
import com.harbor.resourceservice.common.response.ApiErrorResponse;
import com.harbor.resourceservice.category.repository.ResourceCategoryRepository;
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
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "Public resource category lookup for filtering Harbor resources.")
public class CategoryController {

	private final ResourceCategoryRepository categoryRepository;

	public CategoryController(ResourceCategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	@GetMapping
	@Operation(
		summary = "List public resource categories",
		description = """
			Returns the active Harbor category list used by the frontend for quick filters.
			Categories are sorted by configured display order and then by name.
			"""
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "Active resource categories.",
			headers = @Header(name = "X-Correlation-Id", description = "Request correlation ID."),
			content = @Content(
				array = @ArraySchema(schema = @Schema(implementation = CategoryResponse.class)),
				examples = @ExampleObject(value = """
					[
					  {
					    "id": "aaaaaaaa-1111-4111-8111-111111111111",
					    "code": "food",
					    "name": "Food",
					    "description": "Food pantries, meal programs, and grocery help",
					    "iconName": "utensils",
					    "sortOrder": 10
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
	public List<CategoryResponse> listCategories() {
		return categoryRepository.findByActiveTrueOrderBySortOrderAscNameAsc()
			.stream()
			.map(CategoryResponse::from)
			.toList();
	}
}
