package com.romif.securityalarm.repository;

import com.romif.securityalarm.domain.Alarm;
import com.romif.securityalarm.domain.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.Optional;

@SuppressWarnings("unused")
public interface AlarmRepository extends JpaRepository<Alarm, Long> {

}
