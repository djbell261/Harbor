package com.harbor.resourceservice.verification.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
	description = """
	Anonymous incident or data-quality report type. MVP+ report types include:
	food_unavailable, shelter_full, restroom_closed, wifi_offline, unsafe_location,
	incorrect_hours, inaccessible, and other. Legacy compatibility values are also accepted.
	"""
)
public enum VerificationReportType {
	food_unavailable,
	shelter_full,
	restroom_closed,
	wifi_offline,
	unsafe_location,
	incorrect_hours,
	inaccessible,
	closed,
	wrong_hours,
	wrong_address,
	wrong_phone,
	unsafe,
	duplicate,
	other
}
