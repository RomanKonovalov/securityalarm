package com.romif.securityalarm.repository;

import com.romif.securityalarm.domain.Authority;
import com.romif.securityalarm.domain.Device;
import com.romif.securityalarm.domain.DeviceCredentials;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceCredentialsRepository extends JpaRepository<DeviceCredentials, String> {

    Optional<DeviceCredentials> findOneByDevice(Device device);

    Optional<DeviceCredentials> findOneByDeviceLogin(String login);
}
