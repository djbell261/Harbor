package com.harbor.resourceservice.verification.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.harbor.resourceservice.resource.entity.Resource;
import com.harbor.resourceservice.resource.repository.ResourceRepository;
import com.harbor.resourceservice.verification.dto.CreateVerificationReportRequest;
import com.harbor.resourceservice.verification.dto.ReviewVerificationReportRequest;
import com.harbor.resourceservice.verification.dto.VerificationReportResponse;
import com.harbor.resourceservice.verification.entity.VerificationReport;
import com.harbor.resourceservice.verification.enums.VerificationReportType;
import com.harbor.resourceservice.verification.enums.VerificationStatus;
import com.harbor.resourceservice.verification.repository.VerificationReportRepository;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

class VerificationReportServiceTest {

	@Test
	void submitsPendingReport() {
		Resource resource = resource();
		AtomicReference<VerificationReport> savedReport = new AtomicReference<>();
		VerificationReportService service = new VerificationReportService(
			resourceRepositoryReturning(resource),
			reportRepository((proxy, method, args) -> {
				if (method.getName().equals("save")) {
					savedReport.set((VerificationReport) args[0]);
					return args[0];
				}
				return defaultValue(method.getReturnType());
			})
		);

		service.createReport(
			resource.getId(),
			new CreateVerificationReportRequest(VerificationReportType.incorrect_hours, "Hours changed", null)
		);

		VerificationReport report = savedReport.get();
		assertThat(report.getResource()).isSameAs(resource);
		assertThat(report.getStatus()).isEqualTo(VerificationStatus.pending);
		assertThat(report.getDescription()).isEqualTo("Hours changed");
	}

	@Test
	void listsPendingReports() {
		VerificationReport report = report();
		VerificationReportService service = new VerificationReportService(
			resourceRepositoryReturning(report.getResource()),
			reportRepository((proxy, method, args) -> {
				if (method.getName().equals("findByStatusOrderByCreatedAtAsc")) {
					return List.of(report);
				}
				return defaultValue(method.getReturnType());
			})
		);

		List<VerificationReportResponse> reports = service.findReports(VerificationStatus.pending);

		assertThat(reports).hasSize(1);
		assertThat(reports.getFirst().status()).isEqualTo("pending");
	}

	@Test
	void acceptingReportAddsReviewMetadataAndRefreshesResourceTrust() {
		VerificationReport report = report();
		VerificationReportService service = new VerificationReportService(
			resourceRepositoryReturning(report.getResource()),
			reviewRepositoryReturning(report)
		);

		VerificationReportResponse response = service.acceptReport(
			report.getId(),
			new ReviewVerificationReportRequest("Confirmed by phone", "ops-admin")
		);

		assertThat(response.status()).isEqualTo("accepted");
		assertThat(response.reviewDecision()).isEqualTo("accepted");
		assertThat(response.reviewNotes()).isEqualTo("Confirmed by phone");
		assertThat(response.reviewedBy()).isEqualTo("ops-admin");
		assertThat(report.getReviewedAt()).isNotNull();
		assertThat(report.getResource().getLastVerifiedAt()).isNotNull();
		assertThat(report.getResource().getConfidenceScore()).isEqualByComparingTo("0.550");
	}

	@Test
	void rejectingReportAddsReviewMetadataWithoutRefreshingResourceTrust() {
		VerificationReport report = report();
		VerificationReportService service = new VerificationReportService(
			resourceRepositoryReturning(report.getResource()),
			reviewRepositoryReturning(report)
		);

		VerificationReportResponse response = service.rejectReport(
			report.getId(),
			new ReviewVerificationReportRequest("Could not verify", null)
		);

		assertThat(response.status()).isEqualTo("rejected");
		assertThat(response.reviewDecision()).isEqualTo("rejected");
		assertThat(response.reviewedBy()).isEqualTo("admin");
		assertThat(report.getResource().getLastVerifiedAt()).isNull();
		assertThat(report.getResource().getConfidenceScore()).isEqualByComparingTo("0.500");
	}

	private ResourceRepository resourceRepositoryReturning(Resource resource) {
		return proxy(ResourceRepository.class, (proxy, method, args) -> {
			if (method.getName().equals("findPublicById")) {
				return Optional.of(resource);
			}
			return defaultValue(method.getReturnType());
		});
	}

	private VerificationReportRepository reviewRepositoryReturning(VerificationReport report) {
		return reportRepository((proxy, method, args) -> {
			if (method.getName().equals("findWithResourceById")) {
				return Optional.of(report);
			}
			if (method.getName().equals("save")) {
				return args[0];
			}
			return defaultValue(method.getReturnType());
		});
	}

	private VerificationReportRepository reportRepository(InvocationHandler handler) {
		return proxy(VerificationReportRepository.class, handler);
	}

	@SuppressWarnings("unchecked")
	private <T> T proxy(Class<T> type, InvocationHandler handler) {
		return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type}, handler);
	}

	private Object defaultValue(Class<?> returnType) {
		if (returnType.equals(boolean.class)) {
			return false;
		}
		if (returnType.equals(long.class)) {
			return 0L;
		}
		if (returnType.equals(int.class)) {
			return 0;
		}
		return null;
	}

	private Resource resource() {
		Resource resource = new Resource();
		resource.setId(UUID.randomUUID());
		resource.setConfidenceScore(BigDecimal.valueOf(0.500));
		return resource;
	}

	private VerificationReport report() {
		VerificationReport report = new VerificationReport();
		report.setId(UUID.randomUUID());
		report.setResource(resource());
		report.setReportType(VerificationReportType.incorrect_hours);
		report.setStatus(VerificationStatus.pending);
		return report;
	}
}
