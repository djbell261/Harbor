package com.harbor.resourceservice.verification.repository;

import com.harbor.resourceservice.verification.entity.VerificationReport;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationReportRepository extends JpaRepository<VerificationReport, UUID> {

	long countByResourceId(UUID resourceId);

	Optional<VerificationReport> findFirstByResourceIdOrderByCreatedAtDesc(UUID resourceId);

	List<VerificationReport> findTop5ByResourceIdOrderByCreatedAtDesc(UUID resourceId);

	boolean existsByResourceIdAndCreatedAtAfter(UUID resourceId, Instant createdAt);
}
