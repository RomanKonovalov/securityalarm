package com.romif.securityalarm.repository;

import com.romif.securityalarm.domain.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Device entity.
 */
public interface DeviceRepository extends JpaRepository<Device, Long> {

    List<Device> findAllByUserLogin(String createdBy);

    Optional<Device> findOneByLogin(String login);

    Optional<Device> findOneById(Long login);

}
