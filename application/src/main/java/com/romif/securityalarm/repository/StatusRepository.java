package com.romif.securityalarm.repository;

import com.romif.securityalarm.domain.Status;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

/**
 * Spring Data JPA repository for the Status entity.
 */
@SuppressWarnings("unused")
public interface StatusRepository extends JpaRepository<Status,Long> {

    Page<Status> findByCreatedDateAfterAndCreatedDateBeforeAndCreatedBy(ZonedDateTime startDate, ZonedDateTime endDate,  String createdBy, Pageable pageable);

    Page<Status> findByCreatedBy(String createdBy, Pageable pageable);

    Optional<Status> findFirstByCreatedByOrderByCreatedDateDesc(String createdBy);

    Set<Status> findTop10ByCreatedByOrderByCreatedDateDesc(String createdBy);

    Optional<Status> findOneById(Long id);

}
