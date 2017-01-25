package com.romif.securityalarm.repository;

import com.romif.securityalarm.domain.Authority;
import com.romif.securityalarm.domain.DeviceCredentials;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceCredentialsRepository extends JpaRepository<DeviceCredentials, String> {
}
