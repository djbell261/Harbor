package com.harbor.resourceservice.verification.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.harbor.resourceservice.verification.enums.VerificationReportType;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

class VerificationRequestValidationTest {

	private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Test
	void rejectsOverlongReportDescription() {
		var request = new CreateVerificationReportRequest(
			VerificationReportType.other,
			"x".repeat(2001),
			null
		);

		assertThat(validator.validate(request)).isNotEmpty();
	}

	@Test
	void rejectsOverlongReviewNotesAndReviewer() {
		var request = new ReviewVerificationReportRequest("x".repeat(2001), "a".repeat(121));

		assertThat(validator.validate(request))
			.extracting(violation -> violation.getPropertyPath().toString())
			.contains("reviewNotes", "reviewedBy");
	}
}
