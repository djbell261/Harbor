package com.harbor.resourceservice.verification.entity;

import com.harbor.resourceservice.resource.entity.Resource;
import com.harbor.resourceservice.verification.enums.ReporterKind;
import com.harbor.resourceservice.verification.enums.VerificationReportType;
import com.harbor.resourceservice.verification.enums.VerificationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "verification_reports")
@Getter
@Setter
@NoArgsConstructor
public class VerificationReport {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "resource_id", nullable = false)
	private Resource resource;

	@Enumerated(EnumType.STRING)
	@Column(name = "report_type", nullable = false, length = 60)
	private VerificationReportType reportType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 40)
	private VerificationStatus status = VerificationStatus.pending;

	@Enumerated(EnumType.STRING)
	@Column(name = "reporter_kind", nullable = false, length = 40)
	private ReporterKind reporterKind = ReporterKind.anonymous;

	private String description;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "suggested_value", columnDefinition = "jsonb")
	private Map<String, Object> suggestedValue;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@PrePersist
	void prePersist() {
		if (createdAt == null) {
			createdAt = Instant.now();
		}
	}
}
