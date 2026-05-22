package com.harbor.resourceservice.organization.dto;

import com.harbor.resourceservice.organization.entity.Organization;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Compact organization relationship shown on resource details.")
public record OrganizationSummaryResponse(
	@Schema(description = "Organization UUID.", example = "aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaa1")
	UUID id,
	@Schema(description = "Organization name.", example = "Lutheran Community Services Delaware")
	String name,
	@Schema(description = "Organization website URL, when available.", example = "https://lcsde.org/")
	String websiteUrl,
	@Schema(description = "Organization phone number, when available.", example = "(302) 654-8886")
	String phone,
	@Schema(description = "Trust status for the organization record.", example = "verified")
	String trustedStatus
) {
	public static OrganizationSummaryResponse from(Organization organization) {
		if (organization == null) {
			return null;
		}

		return new OrganizationSummaryResponse(
			organization.getId(),
			organization.getName(),
			organization.getWebsiteUrl(),
			organization.getPhone(),
			organization.getTrustedStatus()
		);
	}
}
