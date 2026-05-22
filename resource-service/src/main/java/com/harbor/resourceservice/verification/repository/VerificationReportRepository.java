package com.harbor.resourceservice.verification.repository;

import com.harbor.resourceservice.verification.entity.VerificationReport;
import com.harbor.resourceservice.verification.enums.VerificationStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationReportRepository extends JpaRepository<VerificationReport, UUID> {

	long countByResourceId(UUID resourceId);

	long countByResourceIdAndStatus(UUID resourceId, VerificationStatus status);

	@EntityGraph(attributePaths = {"resource"})
	List<VerificationReport> findByStatusOrderByCreatedAtAsc(VerificationStatus status);

	@EntityGraph(attributePaths = {"resource"})
	Optional<VerificationReport> findWithResourceById(UUID id);

	Optional<VerificationReport> findFirstByResourceIdOrderByCreatedAtDesc(UUID resourceId);

	List<VerificationReport> findTop5ByResourceIdOrderByCreatedAtDesc(UUID resourceId);

	boolean existsByResourceIdAndCreatedAtAfter(UUID resourceId, Instant createdAt);
}
