package com.romif.securityalarm.repository;

import com.romif.securityalarm.domain.Image;
import com.romif.securityalarm.service.dto.LocationDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Spring Data JPA repository for the Status entity.
 */
@SuppressWarnings("unused")
public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByDateTimeAfterAndDateTimeBeforeAndStatusCreatedBy(ZonedDateTime startDate, ZonedDateTime endDate, String createdBy, Pageable pageable);

    @Query("select i.id from Image i where i.dateTime > ?#{[0]} and i.dateTime < ?#{[1]} and i.status.createdBy = ?#{[2]} order by i.dateTime asc ")
    List<Long> findIds(ZonedDateTime startDate, ZonedDateTime endDate,  String createdBy, Pageable pageable);

    @Query("select i from Image i where i.id in ?#{[0]} order by i.dateTime asc")
    List<Image> findByIds(List<Long> ids);

    Image findFirst1ByStatusCreatedByAndDateTimeIsNotNullOrderByDateTimeDesc(String createdBy);

}
