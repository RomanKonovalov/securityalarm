package com.romif.securityalarm.service.dto;

import com.romif.securityalarm.config.Constants;

import com.romif.securityalarm.domain.Authority;
import com.romif.securityalarm.domain.User;

import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.*;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A DTO representing a user, with his authorities.
 */
public class UserDTO {

    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    private String login;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @Email
    @Size(min = 5, max = 100)
    private String email;

    private boolean activated = false;

    @Size(min = 2, max = 5)
    private String langKey;

    private Set<String> authorities;

    private LocationDTO location;

    private Set<DeviceDTO> devices;

    public UserDTO() {
    }

    public UserDTO(User user) {
        this(user.getLogin(), user.getFirstName(), user.getLastName(),
            user.getEmail(), user.getActivated(), user.getLangKey(),
            user.getAuthorities().stream().map(Authority::getName)
                .collect(Collectors.toSet()),
            new LocationDTO(user.getLatitude(), user.getLongitude()),
            user.getDevices());
    }

    public UserDTO(String login, String firstName, String lastName,
        String email, boolean activated, String langKey, Set<String> authorities, LocationDTO location, Set<User> devices) {

        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.activated = activated;
        this.langKey = langKey;
        this.authorities = authorities;
        this.location = location;
        this.devices = devices.stream().map(DeviceDTO::new).collect(Collectors.toSet());
    }

    public String getLogin() {
        return login;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public LocationDTO getLocation() {
        return location;
    }

    public void setLocation(LocationDTO location) {
        this.location = location;
    }

    public boolean isActivated() {
        return activated;
    }

    public String getLangKey() {
        return langKey;
    }

    public Set<String> getAuthorities() {
        return authorities;
    }

    public Set<DeviceDTO> getDevices() {
        return devices;
    }

    public void setDevices(Set<DeviceDTO> devices) {
        this.devices = devices;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
            "login='" + login + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", email='" + email + '\'' +
            ", activated=" + activated +
            ", langKey='" + langKey + '\'' +
            ", authorities=" + authorities +
            "}";
    }
}
