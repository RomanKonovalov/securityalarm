package com.romif.securityalarm.repository;

import com.romif.securityalarm.domain.Device;
import com.romif.securityalarm.domain.User;

import java.time.ZonedDateTime;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findOneByActivationKey(String activationKey);

    List<User> findAllByActivatedIsFalseAndCreatedDateBefore(ZonedDateTime dateTime);

    Optional<User> findOneByResetKey(String resetKey);

    Optional<User> findOneByEmail(String email);

    Optional<User> findOneByLogin(String login);

    Optional<User> findOneById(Long login);

    @Query(value = "select distinct user from User user left join fetch user.authorities where 'ROLE_DEVICE' NOT MEMBER OF user.authorities",
        countQuery = "select count(user) from User user where 'ROLE_DEVICE' NOT MEMBER OF user.authorities")
    Page<User> findAllWithAuthorities(Pageable pageable);

    @Query(value = "select distinct user.devices from User user where user.login in (select alarm.createdBy from Alarm alarm)")
    @Cacheable("userLogins")
    Set<Device> findAllUserLogins();

}
