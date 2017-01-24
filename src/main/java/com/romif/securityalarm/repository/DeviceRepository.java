package com.romif.securityalarm.repository;

import com.romif.securityalarm.domain.Device;
import com.romif.securityalarm.domain.GenericUser;
import com.romif.securityalarm.domain.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Spring Data JPA repository for the Device entity.
 */
public interface DeviceRepository extends JpaRepository<Device, Long> {

    Page<Device> findAllByCreatedBy(String createdBy, Pageable pageable);

    Optional<Device> findOneByLogin(String login);

    Optional<Device> findOneById(Long login);

}
