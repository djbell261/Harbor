package com.harbor.resourceservice.resource.entity;

import com.harbor.resourceservice.category.entity.ResourceCategory;
import com.harbor.resourceservice.organization.entity.Organization;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "resources")
@Getter
@Setter
@NoArgsConstructor
public class Resource {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "category_id", nullable = false)
	private ResourceCategory category;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organization_id")
	private Organization organization;

	@Column(nullable = false, length = 220)
	private String name;

	private String description;

	@Column(name = "address_line1", length = 220)
	private String addressLine1;

	@Column(name = "address_line2", length = 220)
	private String addressLine2;

	@Column(length = 120)
	private String city;

	@Column(length = 120)
	private String region;

	@Column(name = "postal_code", length = 30)
	private String postalCode;

	@Column(name = "country_code", nullable = false, length = 2)
	private String countryCode = "US";

	@Column(precision = 9, scale = 6)
	private BigDecimal latitude;

	@Column(precision = 9, scale = 6)
	private BigDecimal longitude;

	@Column(length = 50)
	private String phone;

	@Column(name = "website_url")
	private String websiteUrl;

	@Column(name = "eligibility_notes")
	private String eligibilityNotes;

	@Column(name = "intake_notes")
	private String intakeNotes;

	@Column(name = "accessibility_notes")
	private String accessibilityNotes;

	@Column(name = "data_source", length = 120)
	private String dataSource;

	@Column(name = "source_url")
	private String sourceUrl;

	@Column(name = "last_verified_at")
	private Instant lastVerifiedAt;

	@Column(name = "confidence_score", nullable = false, precision = 4, scale = 3)
	private BigDecimal confidenceScore = BigDecimal.valueOf(0.500);

	@Column(nullable = false, length = 40)
	private String visibility = "public";

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	@Column(name = "deleted_at")
	private Instant deletedAt;
}
