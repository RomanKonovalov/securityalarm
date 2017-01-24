package com.romif.securityalarm.repository;

import com.romif.securityalarm.domain.Alarm;
import com.romif.securityalarm.domain.Device;
import com.romif.securityalarm.domain.Status;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("unused")
public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    @Cacheable("alarms")
    List<Alarm> findAll();

    Set<Alarm> findAllByCreatedBy(String createdBy);

}
