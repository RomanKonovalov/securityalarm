package com.romif.securityalarm.repository;

import com.romif.securityalarm.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Spring Data JPA repository for the Status entity.
 */
@SuppressWarnings("unused")
public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByDateTimeAfterAndDateTimeBeforeAndStatusCreatedByOrderByDateTimeAsc(ZonedDateTime startDate, ZonedDateTime endDate, String createdBy);

}
