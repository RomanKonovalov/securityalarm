package com.romif.securityalarm.repository;

import com.romif.securityalarm.domain.Status;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Status entity.
 */
@SuppressWarnings("unused")
public interface StatusRepository extends JpaRepository<Status,Long> {

}
