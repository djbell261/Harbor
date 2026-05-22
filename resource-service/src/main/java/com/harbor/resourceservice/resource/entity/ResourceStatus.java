package com.harbor.resourceservice.resource.entity;

import com.harbor.resourceservice.resource.enums.ReporterType;
import com.harbor.resourceservice.resource.enums.ResourceAvailability;
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
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "resource_status")
@Getter
@Setter
@NoArgsConstructor
public class ResourceStatus {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "resource_id", nullable = false)
	private Resource resource;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 40)
	private ResourceAvailability status;

	private String reason;

	@Column(name = "effective_from", nullable = false)
	private Instant effectiveFrom;

	@Column(name = "effective_until")
	private Instant effectiveUntil;

	@Enumerated(EnumType.STRING)
	@Column(name = "reported_by_type", nullable = false, length = 40)
	private ReporterType reportedByType = ReporterType.system;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;
}
