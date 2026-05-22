package com.harbor.resourceservice.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Build and runtime version metadata for the Harbor resource service.")
public record VersionResponse(
	@Schema(description = "Application name.", example = "resource-service")
	String application,
	@Schema(description = "Application version supplied by deployment metadata.", example = "0.1.0")
	String version,
	@Schema(description = "Build identifier supplied by CI/CD.", example = "github-run-123456")
	String build,
	@Schema(description = "Git commit SHA supplied by CI/CD.", example = "abc1234")
	String commit,
	@Schema(description = "Active Spring profiles.", example = "[\"prod\"]")
	List<String> profiles
) {
}
