package com.harbor.resourceservice.category.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "resource_categories")
@Getter
@Setter
@NoArgsConstructor
public class ResourceCategory {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false, unique = true, length = 80)
	private String code;

	@Column(nullable = false, length = 120)
	private String name;

	private String description;

	@Column(name = "icon_name", length = 80)
	private String iconName;

	@Column(name = "sort_order", nullable = false)
	private Integer sortOrder = 0;

	@Column(name = "is_active", nullable = false)
	private boolean active = true;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;
}
