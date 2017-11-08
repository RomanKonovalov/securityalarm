package com.romif.securityalarm.repository;

import com.romif.securityalarm.domain.Status;
import com.romif.securityalarm.service.dto.LocationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Spring Data JPA repository for the Status entity.
 */
@SuppressWarnings("unused")
public interface StatusRepository extends JpaRepository<Status, Long> {

    Page<Status> findByCreatedDateAfterAndCreatedDateBeforeAndCreatedBy(ZonedDateTime startDate, ZonedDateTime endDate,  String createdBy, Pageable pageable);

    Page<Status> findByCreatedBy(String createdBy, Pageable pageable);

    Optional<Status> findFirstByCreatedByOrderByCreatedDateDesc(String createdBy);

    Set<Status> findTop10ByCreatedByOrderByCreatedDateDesc(String createdBy);

    Optional<Status> findOneById(Long id);

    @Query("select s.location.id from Status s where s.location is not null and cast(s.location.latitude as java.lang.String) <> 'NaN' and cast(s.location.longitude as java.lang.String) <> 'NaN' and s.createdDate > ?#{[0]} and s.createdDate < ?#{[1]} and s.createdBy = ?#{[2]} order by s.createdDate asc ")
    List<Long> findIds(ZonedDateTime startDate, ZonedDateTime endDate,  String createdBy, Pageable pageable);

    @Query("select new com.romif.securityalarm.service.dto.LocationDTO(l.latitude, l.longitude) from Location l where l.id in ?#{[0]} order by l.id desc")
    List<LocationDTO> findByIds(List<Long> ids);


}
