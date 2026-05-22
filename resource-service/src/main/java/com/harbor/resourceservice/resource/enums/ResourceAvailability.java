package com.harbor.resourceservice.resource.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resource availability status shown in summary and detail responses.")
public enum ResourceAvailability {
	open,
	closed,
	limited,
	unknown,
	temporarily_closed
}
