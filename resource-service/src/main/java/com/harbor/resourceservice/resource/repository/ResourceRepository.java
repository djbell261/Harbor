package com.harbor.resourceservice.resource.repository;

import com.harbor.resourceservice.resource.entity.Resource;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ResourceRepository extends JpaRepository<Resource, UUID> {

	@EntityGraph(attributePaths = {"category", "organization"})
	@Query("""
		select r from Resource r
		where r.deletedAt is null
		  and r.visibility = 'public'
		  and (:categoryCode is null or r.category.code = :categoryCode)
		  and (:city is null or r.city = :city)
		  and (:postalCode is null or r.postalCode = :postalCode)
		order by r.name asc
		""")
	List<Resource> findPublicResources(
		@Param("categoryCode") String categoryCode,
		@Param("city") String city,
		@Param("postalCode") String postalCode,
		Pageable pageable
	);

	@EntityGraph(attributePaths = {"category", "organization"})
	@Query("""
		select r from Resource r
		where r.id = :id
		  and r.deletedAt is null
		  and r.visibility = 'public'
		""")
	Optional<Resource> findPublicById(@Param("id") UUID id);
}
