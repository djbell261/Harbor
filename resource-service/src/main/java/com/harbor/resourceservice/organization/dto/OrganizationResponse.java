package com.harbor.resourceservice.organization.dto;

import com.harbor.resourceservice.organization.entity.Organization;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Organization associated with one or more Harbor resource listings.")
public record OrganizationResponse(
	@Schema(description = "Organization UUID.", example = "aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaa1")
	UUID id,
	@Schema(description = "Organization name.", example = "Lutheran Community Services Delaware")
	String name,
	@Schema(description = "Short organization description.", example = "Community organization supporting Wilmington residents.")
	String description,
	@Schema(description = "Organization website URL, when available.", example = "https://lcsde.org/")
	String websiteUrl,
	@Schema(description = "Organization phone number, when available.", example = "(302) 654-8886")
	String phone,
	@Schema(description = "Organization email, when available.", example = "info@example.org")
	String email,
	@Schema(description = "Trust status for the organization record.", example = "verified")
	String trustedStatus
) {
	public static OrganizationResponse from(Organization organization) {
		return new OrganizationResponse(
			organization.getId(),
			organization.getName(),
			organization.getDescription(),
			organization.getWebsiteUrl(),
			organization.getPhone(),
			organization.getEmail(),
			organization.getTrustedStatus()
		);
	}
}
