package com.harbor.resourceservice.common.controller;

import com.harbor.resourceservice.common.dto.VersionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/version")
@Tag(name = "Version", description = "Application version and deployment metadata.")
public class VersionController {

	private final Environment environment;
	private final String application;
	private final String version;
	private final String build;
	private final String commit;

	public VersionController(
		Environment environment,
		@Value("${spring.application.name}") String application,
		@Value("${info.app.version}") String version,
		@Value("${info.app.build}") String build,
		@Value("${info.app.commit}") String commit
	) {
		this.environment = environment;
		this.application = application;
		this.version = version;
		this.build = build;
		this.commit = commit;
	}

	@GetMapping
	@Operation(summary = "Get Harbor resource service version metadata")
	public VersionResponse version() {
		return new VersionResponse(
			application,
			version,
			build,
			commit,
			Arrays.asList(environment.getActiveProfiles())
		);
	}
}
