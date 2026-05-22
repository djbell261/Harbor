package com.harbor.resourceservice.resource.dto;

import com.harbor.resourceservice.resource.entity.ResourceHour;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import java.util.UUID;

@Schema(description = "Operating hours for one day of the week.")
public record ResourceHourResponse(
	@Schema(description = "Hour row UUID.", example = "a910bb9f-3a86-49b6-ae43-9594b1667c13")
	UUID id,
	@Schema(description = "Day of week where 0 is Sunday and 6 is Saturday.", example = "1", minimum = "0", maximum = "6")
	Short dayOfWeek,
	@Schema(description = "Opening time in local time, or null when closed.", example = "09:00:00")
	LocalTime opensAt,
	@Schema(description = "Closing time in local time, or null when closed.", example = "12:00:00")
	LocalTime closesAt,
	@Schema(description = "Whether the resource is closed on this day.", example = "false")
	boolean closed,
	@Schema(description = "Human-readable notes about the hours.", example = "Food pantry distribution")
	String notes
) {
	public static ResourceHourResponse from(ResourceHour hour) {
		return new ResourceHourResponse(
			hour.getId(),
			hour.getDayOfWeek(),
			hour.getOpensAt(),
			hour.getClosesAt(),
			hour.isClosed(),
			hour.getNotes()
		);
	}
}
