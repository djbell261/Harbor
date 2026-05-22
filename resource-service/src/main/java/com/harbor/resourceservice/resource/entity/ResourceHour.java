package com.harbor.resourceservice.resource.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "resource_hours")
@Getter
@Setter
@NoArgsConstructor
public class ResourceHour {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "resource_id", nullable = false)
	private Resource resource;

	@Column(name = "day_of_week", nullable = false)
	private Short dayOfWeek;

	@Column(name = "opens_at")
	private LocalTime opensAt;

	@Column(name = "closes_at")
	private LocalTime closesAt;

	@Column(name = "is_closed", nullable = false)
	private boolean closed;

	private String notes;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;
}
